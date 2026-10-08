package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.systems.BindSystem;
import dbrighthd.qahalloween2026.events.EventsStorage;
import dbrighthd.qahalloween2026.systems.WetSystem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static dbrighthd.qahalloween2026.Qahalloween2026.MOD_ID;
import static dbrighthd.qahalloween2026.mainloop.Config.bindTimeTicks;
import static dbrighthd.qahalloween2026.mainloop.Config.stringRequiredForBind;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;

public class TickLoop {
    public static void init()
    {
        ServerTickEvents.END_LEVEL_TICK.register(TickLoop::tick);
        ServerTickEvents.END_LEVEL_TICK.register(EventsStorage::refreshAndDoStuff);

    }


    public static void tick(ServerLevel serverLevel)
    {
        actOneTick(serverLevel);
    }

    public static void actOneTick(ServerLevel serverLevel)
    {
        for(ServerPlayer player : serverLevel.players())
        {
            actOnePlayerTick(player, serverLevel);
            stringBar(player);
        }
        for(Entity entity: serverLevel.getAllEntities())
        {
            if(entity instanceof ExperienceOrb)
            {
                entity.kill(serverLevel);
            }
            if (entity instanceof Arrow arrow)
            {
                Entity target = serverLevel.getEntity(
                        arrow.getAttachedOrCreate(DataAttachments.TARGET_DATA).target()
                );

                if (target != null && arrow.tickCount <= Config.arrowTrackTime)
                {

                    Vec3 targetPos = new Vec3(
                            target.getX(),
                            target.getY(0.5),
                            target.getZ()
                    );

                    Vec3 direction = targetPos
                            .subtract(arrow.position())
                            .normalize();

                    arrow.setDeltaMovement(direction.scale(Config.arrowSpeed));
                    particleAtEntity(arrow,new TrailParticleOption(target.position().add(0,1,0), 0xFF0000, 5), 0f, 0f, 0f,10, 0);

                    sendPacketToAllPlayers(serverLevel, new ClientboundSetEntityMotionPacket(arrow));

                }
                if(arrow.tickCount > Config.arrowLifeTime)
                {
                    arrow.kill(serverLevel);
                }
            }
        }
    }

    public static void stringBar(ServerPlayer player)
    {
        float progress = (player.getInventory().countItem(Items.STRING)% stringRequiredForBind)/(float) stringRequiredForBind;
        int stacks = player.getInventory().countItem(Items.STRING)/ stringRequiredForBind;
        player.connection.send(new ClientboundSetExperiencePacket(progress, stringRequiredForBind, stacks));
    }
    private static UUID dashBossBarId(ServerPlayer player) {
        return UUID.nameUUIDFromBytes(
                (MOD_ID + ":string:" + player.getUUID()).getBytes(StandardCharsets.UTF_8)
        );
    }
    public static void actOnePlayerTick(ServerPlayer player, ServerLevel serverLevel)
    {
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0f);
        BlockPos standingOn = player.blockPosition().below();
        if (serverLevel.getBlockState(standingOn).is(Blocks.GRASS_BLOCK))
        {
            serverLevel.setBlockAndUpdate(standingOn,Blocks.DIRT_PATH.defaultBlockState());
        }
        BindSystem.bindTick(player);
        WetSystem.wetTick(player);
        if(isLookingAtSun(player))
        {
            lookAtSun(player);
        }
    }

    public static void lookAtSun(ServerPlayer player)
    {
        if(!player.hasEffect(MobEffects.BLINDNESS))
        {
            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("Don't Look at the Sun!")));
            player.addEffect( new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));
            player.setRemainingFireTicks(60);

        }

    }

    public static void hideEntityFromPlayer(ServerPlayer player, Entity targetEntity) {
        ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(targetEntity.getId());
        player.connection.send(destroyPacket);
    }
}
