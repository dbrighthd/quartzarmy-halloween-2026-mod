package dbrighthd.qahalloween2026.utils;

import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.Server;

public class PlayerMethodUtils {

    public static void sendPacketToAllPlayers(ServerLevel serverLevel, Packet packet)
    {
        for (ServerPlayer player : serverLevel.players())
        {
                player.connection.send(packet);
        }
    }
    public static void playSoundAtEntity(Entity entity, SoundEvent sound, float volume, float pitch)
    {
        entity.level().playSound(null, entity.blockPosition(), sound, SoundSource.MASTER, volume, pitch);
    }

    public static void playSoundAtEntityForPlayer(LivingEntity entity, SoundEvent sound, float volume, float pitch, ServerPlayer player)
    {
        player.connection.send(new ClientboundSoundPacket(Holder.direct(sound), SoundSource.MASTER, entity.getX(), entity.getY(), entity.getZ(), volume, pitch, player.level().getRandom().nextLong()));
    }

    public static void informMiss(ServerPlayer player)
    {
        player.connection.send(
                new ClientboundSetTitleTextPacket(Component.empty())
        );

        player.connection.send(
                new ClientboundSetSubtitleTextPacket(Component.literal("Miss!").withColor(TextColor.RED))
        );
        playSoundAtEntityForPlayer(player, Config.missSound, 100, 2, player);
    }
    public static  <T extends net.minecraft.core.particles.ParticleOptions> void particleAtEntity(Entity entity, T particleType, float dx, float dy, float dz, int count, int speed)
    {
        ((ServerLevel)(entity.level())).sendParticles(
                particleType,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                count,
                dx,
                dy,
                dz,
                speed
        );    }
    public static  <T extends net.minecraft.core.particles.ParticleOptions> void particleAtEntityOffset(Entity entity, Vec3 offset, T particleType, float dx, float dy, float dz, int count, int speed)
    {
        ((ServerLevel)(entity.level())).sendParticles(
                particleType,
                entity.getX() + offset.x,
                entity.getY() + offset.y,
                entity.getZ() + offset.z,
                count,
                dx,
                dy,
                dz,
                speed
        );    }
    public static boolean isLookingAtSun(ServerPlayer player)
    {
        if(player.level().dimension() == Level.OVERWORLD)
        {
            long time = player.level().getOverworldClockTime()%24000L;
            if(time > 12000)
            {
                return false;
            }
            float midYaw = time <= 6000 ? -90 : 90;
            if(Math.abs(player.getXRot() - getSunPitch(time)) <= 4.5)
            {
                return Math.abs(player.getYRot() - midYaw) < getSunYaw(time);
            }
        }
        return false;
    }

    public static float getSunPitch(long time)
    {
        if(time > 6000)
        {
            time = (6000 + (6000-time));
        }
        return (float)(184.63485 * Math.sin(0.0000892826*time-2.75514) + 57.09115);
    }

    public static float getSunYaw(long time)
    {
        if(time > 6000)
        {
            time = (6000 + (6000-time));
        }
        return (float)(1.596 + (20902/(6117.3-time)));
    }

    public static void addAndSendMovementIfPlayer(Entity entity, Vec3 acceleration, boolean resetVert)
    {
        if(resetVert)
        {
            entity.setDeltaMovement(entity.getDeltaMovement().multiply(1,0,1));
        }
        entity.addDeltaMovement(acceleration);
        if(entity instanceof ServerPlayer player)
        {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }

    public static void triggerWindBurst(ServerPlayer player, double vertAmount)
    {
        player.setIgnoreFallDamageFromCurrentImpulse(true,player.position());
        particleAtEntity(player, ParticleTypes.GUST_EMITTER_SMALL,0,0,0,1,1);
        playSoundAtEntity(player, SoundEvents.WIND_CHARGE_BURST.value(), 1, 1);
        addAndSendMovementIfPlayer(player, new Vec3(0, vertAmount,0), true);
    }
}
