package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.systems.BindSystem;
import dbrighthd.qahalloween2026.events.EventsStorage;
import dbrighthd.qahalloween2026.systems.SheepSystem;
import dbrighthd.qahalloween2026.systems.WetSystem;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.data.worldgen.DimensionTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.util.Set;
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
            if(entity instanceof Villager villager)
            {
                villagerTick(villager);
            }
            if(entity instanceof ExperienceOrb)
            {
                entity.kill(serverLevel);
            }
            if(entity instanceof  Zombie zombie)
            {
                zombieTick(zombie, serverLevel);
            }
            if(entity instanceof  Enderman enderman)
            {
                endermanTick(enderman, serverLevel);
            }
            if(entity instanceof Boat boat)
            {
                boat.setDeltaMovement(0,1,0);
            }
            if(entity instanceof Sheep sheep)
            {
                sheep.setHealth(100);
            }
            if (entity instanceof Arrow arrow)
            {
                TargetData arrowData = arrow.getAttachedOrCreate(DataAttachments.TARGET_DATA);
                Entity target = serverLevel.getEntity(arrowData.target());
                if(!arrowData.playerOwned())
                {
                    if(arrow.tickCount > Config.arrowBurst)
                    {
                        arrow.setBaseDamage(20);
                    }
                    if (target != null && arrow.tickCount <= Config.arrowTrackTime)
                    {

                        Vec3 targetPos = new Vec3(target.getX(), target.getY(0.5), target.getZ());

                        Vec3 direction = targetPos.subtract(arrow.position()).normalize();

                        arrow.setDeltaMovement(direction.scale(arrow.tickCount >= Config.arrowBurst ? Config.arrowSpeed : Config.arrowBurstSpeed));
                        particleAtEntity(arrow,new TrailParticleOption(target.position().add(0,1,0), 0xFF0000, 10), 0f, 0f, 0f,10, 0);


                    }
                    if(arrow.tickCount > Config.arrowLifeTime)
                    {
                        arrow.kill(serverLevel);
                    }
                    sendPacketToAllPlayers(serverLevel, new ClientboundSetEntityMotionPacket(arrow));
                    PositionMoveRotation change = new PositionMoveRotation(arrow.position(), arrow.getDeltaMovement(), arrow.getYRot(), arrow.getXRot());
                    ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(arrow.getId(), change, Set.of(), arrow.onGround());
                    sendPacketToAllPlayers(serverLevel, teleportPacket);
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
        SheepSystem.sheepTick(player);
        if(isLookingAtSun(player))
        {
            lookAtSun(player);
        }
        if(player.level().dimension().equals(Level.END))
        {
            var attribute = player.getAttribute(Attributes.GRAVITY);
            if(attribute != null)
            {
                if(player.getY() < 0)
                {
                    attribute.setBaseValue(-0.5);
                }
                else
                {
                    attribute.setBaseValue(0.08);
                }
            }
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

    public static void villagerTick(Villager villager)
    {
        if(!villager.getItemInHand(InteractionHand.MAIN_HAND).is(Items.DIAMOND_SPEAR))
        {
            villager.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.DIAMOND_SPEAR));
        }
    }

    public static void endermanTick(Enderman enderman, ServerLevel serverLevel)
    {
        particleAtEntityOffset(enderman, new Vec3(0,1.5,0), ParticleTypes.SOUL,0.25f,1f,0.25f,5,0);
    }


    public static void zombieTick(Zombie zombie, ServerLevel serverLevel)
    {
        Vec3 belowBlock = zombie.position().subtract(0,1,0);
        boolean clutch = (zombie.fallDistance > 4);
        boolean inDanger = false;

        if(serverLevel.getNearestPlayer(zombie,3) != null)
        {
            inDanger = true;
            if(zombie.onGround())
            {
                zombie.jumpFromGround();
            }
            zombie.lookAt(EntityAnchorArgument.Anchor.EYES, belowBlock);
        }
        zombie.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(clutch ? Items.WATER_BUCKET : inDanger ? Items.OAK_PLANKS : Items.AIR,1));
        if(!zombie.onGround() && zombie.getDeltaMovement().y <= 0 && !serverLevel.getBlockState(BlockPos.containing(belowBlock.subtract(0,1,0))).isAir()) {
            if(clutch)
            {
                serverLevel.setBlockAndUpdate(BlockPos.containing(belowBlock), Blocks.WATER.defaultBlockState());
            }
            else
            {
                serverLevel.setBlockAndUpdate(BlockPos.containing(belowBlock), Blocks.OAK_PLANKS.defaultBlockState());

            }
            zombie.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT);
        }
    }

    public static void hideEntityFromPlayer(ServerPlayer player, Entity targetEntity) {
        ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(targetEntity.getId());
        player.connection.send(destroyPacket);
    }
}
