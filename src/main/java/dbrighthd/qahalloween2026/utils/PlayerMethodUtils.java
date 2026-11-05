package dbrighthd.qahalloween2026.utils;

import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class PlayerMethodUtils {

    @SuppressWarnings("rawtypes")
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
                new ClientboundSetSubtitleTextPacket(Component.literal("Miss!").withColor(TextColor.RED))
        );
        player.connection.send(
                new ClientboundSetTitleTextPacket(Component.empty())
        );
        playSoundAtEntityForPlayer(player, Config.missSound, 100, 2, player);
        grantAdvancement(player,"main/missed");
    }

    public static void informWin(ServerPlayer player)
    {

        player.connection.send(
                new ClientboundSetSubtitleTextPacket(Component.literal("congrations"))
        );
        player.connection.send(
                new ClientboundSetTitleTextPacket(Component.literal("your did it"))
        );
    }

    public static boolean chainJumpCheck(ServerPlayer player)
    {
        //I genuinely forgot that toggle sprint players exist
        return !player.isSprinting() && !player.getLastClientInput().sprint() && player.getLastClientInput().forward();
    }
    public static void grantAdvancement(ServerPlayer player, String advancement)
    {
        String namespace = "dbright"; //listen idc if this is 'bad' it works
        AdvancementHolder advancementHolder = player.level().getServer().getAdvancements().get(Identifier.fromNamespaceAndPath(namespace, advancement));
        if (advancementHolder != null) {
            AdvancementProgress progress = player.getAdvancements()
                    .getOrStartProgress(advancementHolder);

            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancementHolder, criterion);
            }
        }
    }

    public static  <T extends net.minecraft.core.particles.ParticleOptions> void particleAtEntity(Entity entity, T particleType, float dx, float dy, float dz, int count, double speed)
    {
        if(entity.level() instanceof ServerLevel level)
        {
            level.sendParticles(particleType, entity.getX(), entity.getY(), entity.getZ(), count, dx, dy, dz, speed);
        }
    }


    public static  <T extends net.minecraft.core.particles.ParticleOptions> void particleAtEntityToAllPlayers(Entity entity, T particleType, float dx, float dy, float dz, int count, double speed)
    {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                particleType,
                true,
                true,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                dx,
                dy,
                dz,
                (float)speed,
                count
        );

        for (Player player : entity.level().players()) {
            if(player instanceof  ServerPlayer serverPlayer)
            {
                serverPlayer.connection.send(packet);
            }
        }
    }

    public static  <T extends net.minecraft.core.particles.ParticleOptions> void particleAtEntityOffset(Entity entity, Vec3 offset, T particleType, float dx, float dy, float dz, int count, double speed)
    {
        if(entity.level() instanceof ServerLevel level)
        {
            level.sendParticles(
                    particleType,
                    entity.getX() + offset.x,
                    entity.getY() + offset.y,
                    entity.getZ() + offset.z,
                    count,
                    dx,
                    dy,
                    dz,
                    speed
            );
        }

    }

    public static  <T extends net.minecraft.core.particles.ParticleOptions> void particleAtEntityOffsetToAllPlayers(Entity entity, Vec3 offset, T particleType, float dx, float dy, float dz, int count, int speed)
    {
        ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(
                particleType,
                true,
                true,
                entity.getX() + offset.x,
                entity.getY() + offset.y,
                entity.getZ() + offset.z,
                dx,
                dy,
                dz,
                speed,
                count
        );

        for (Player player : entity.level().players()) {
            if(player instanceof  ServerPlayer serverPlayer)
            {
                serverPlayer.connection.send(packet);
            }
        }
    }
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

    public static @Nullable ServerPlayer getRandomPlayer(ServerPlayer original)
    {
        PlayerList list = original.level().getServer().getPlayerList();
        List<ServerPlayer> players = list.getPlayers().stream().filter(player -> player.gameMode().isSurvival() && !player.getUUID().equals(original.getUUID())).toList();
        if(players.isEmpty())
        {
            return null;
        }
        return players.get(original.getRandom().nextInt(players.size()));
    }
}
