package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.dialogs.DeathDialog;
import dbrighthd.qahalloween2026.systems.*;
import dbrighthd.qahalloween2026.events.EventsStorage;
import dbrighthd.qahalloween2026.utils.GeneralUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static dbrighthd.qahalloween2026.mainloop.Config.stringRequiredForBind;
import static dbrighthd.qahalloween2026.mainloop.EndFight.endFightTick;
import static dbrighthd.qahalloween2026.mainloop.EndFight.spawnBoss;
import static dbrighthd.qahalloween2026.mainloop.PlayerSpecificStuff.tickPlayerSpecifics;
import static dbrighthd.qahalloween2026.mainloop.Setup.spawnDataCheck;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.getCurrentTide;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;

public class TickLoop {
    public static boolean hasGlowFlies = false;
    public static GeneralUtils.Tide tide;
    public static void init()
    {
        ServerTickEvents.END_SERVER_TICK.register(TickLoop::tick);
        ServerTickEvents.END_SERVER_TICK.register(EventsStorage::refreshAndDoStuff);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, _) -> {
            if (entity instanceof Skeleton
                    && source.getDirectEntity() instanceof Arrow arrow) {
                TargetData arrowData = arrow.getAttachedOrCreate(DataAttachments.TARGET_DATA);
                Entity target = arrow.level().getEntity(arrowData.target());
                if(target instanceof Player player)
                {
                    grantAdvancement((ServerPlayer)player,"main/arrowskeleton");
                }

            }

            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((_, newPlayer, _) -> {
            DeathDialog.show(newPlayer);
            newPlayer.setGameMode(GameType.SPECTATOR);
        });
    }


    public static void tick(MinecraftServer server)
    {


        tide = getCurrentTide(server.overworld());
        if(spawnDataCheck(server) == Setup.StartPhase.IN_GAME)
        {
            for(ServerPlayer player : server.getPlayerList().getPlayers())
            {
                playerTick(player, player.level());
            }
            for (ServerLevel level : server.getAllLevels()) {
                List<Entity> snapshot = new ArrayList<>();
                level.getAllEntities().forEach(snapshot::add); // NO CONCURRENCY ISSUES PLEASE I WILL CRY
                for(Entity entity : snapshot)
                {
                    if(entity == null)
                    {
                        break;
                    }
                    if(entity.tickCount <= 1)
                    {
                        OnEntityLoad.onEntityLoad(entity,level);
                    }
                    entityTick(entity);
                }
            }
        }
        else
        {
            for(ServerPlayer player : server.getPlayerList().getPlayers())
            {
                beforeTick(player);
            }
        }

    }

    public static void beforeTick(ServerPlayer player)
    {
        if(player.position().y < 300)
        {
            BlockPos tpTo = player.level().getRespawnData().pos().atY(301);
            player.teleportTo(tpTo.getX(), tpTo.getY(), tpTo.getZ());
        }
        if(player.gameMode.isSurvival())
        {
            player.setGameMode(GameType.ADVENTURE);
        }
        player.setHealth(20);
        player.addEffect( new MobEffectInstance(MobEffects.WEAKNESS, 2, 255,true,true));
    }

    public static void entityTick(Entity entity)
    {
        if(entity == null)
        {
            return;
        }
        ServerLevel level = (ServerLevel)entity.level();
        switch (entity) {
            case Axolotl axolotl -> axolotlTick(axolotl, level);
            case Chicken chicken -> chickenTick(chicken, level);
            case EnderDragon dragon -> killDragonAndBeginEnderFight(dragon,level);
            case Phantom phantom -> phantom.setRemainingFireTicks(-10);
            case Panda panda -> pandaTick(panda);
            case ExperienceOrb _ -> entity.kill(level);
            case Zombie zombie -> zombieTick(zombie, level);
            case Enderman enderman -> {
                if (level.dimension() == Level.END) {

                    endermanTick(enderman);
                }
            }
            case Boat boat -> boat.setDeltaMovement(0, 1, 0);
            case Ravager ravager ->
            {
                if (Objects.requireNonNull(tide) == GeneralUtils.Tide.Endure) {
                    changeAttribute(ravager, Attributes.ATTACK_DAMAGE, 4);
                } else {
                    changeAttribute(ravager, Attributes.ATTACK_DAMAGE, 1);
                }
            }
            case Sheep sheep -> sheep.setHealth(100);
            case Arrow arrow -> {
                TargetData arrowData = arrow.getAttachedOrCreate(DataAttachments.TARGET_DATA);
                Entity target = level.getEntity(arrowData.target());

                if(arrowData.sourceType() == TargetData.SourceType.SKELETON || arrowData.sourceType() == TargetData.SourceType.PLAYER) {
                    if (arrowData.sourceType() == TargetData.SourceType.SKELETON) {
                        if (arrow.tickCount > Config.arrowBurst) {
                            arrow.setBaseDamage(20);
                        }
                        if (target != null && arrow.tickCount <= Config.arrowTrackTime) {

                            Vec3 targetPos = new Vec3(target.getX(), target.getY(0.5), target.getZ());
                            Vec3 direction = targetPos.subtract(arrow.position()).normalize();
                            arrow.setDeltaMovement(direction.scale(arrow.tickCount >= Config.arrowBurst ? Config.arrowSpeed : Config.arrowBurstSpeed));
                            particleAtEntity(arrow, new TrailParticleOption(target.position().add(0, 1, 0), 0xFF0000, 10), 0f, 0f, 0f, 10, 0);


                        }
                        if (arrow.tickCount > Config.arrowLifeTime) {
                            arrow.kill(level);
                        }
                    }
                    sendPacketToAllPlayers(level, new ClientboundSetEntityMotionPacket(arrow));
                    PositionMoveRotation change = new PositionMoveRotation(arrow.position(), arrow.getDeltaMovement(), arrow.getYRot(), arrow.getXRot());
                    ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(arrow.getId(), change, Set.of(), arrow.onGround());
                    sendPacketToAllPlayers(level, teleportPacket);
                }

            }
            case BreezeWindCharge breezeWindCharge -> {
                TargetData arrowData = breezeWindCharge.getAttachedOrCreate(DataAttachments.TARGET_DATA);
                Entity target = level.getEntity(arrowData.target());
                if (target != null && breezeWindCharge.tickCount <= Config.arrowTrackTime) {

                    Vec3 targetPos = new Vec3(target.getX(), target.getY(0.5), target.getZ());

                    Vec3 direction = targetPos.subtract(breezeWindCharge.position()).normalize();

                    breezeWindCharge.setDeltaMovement(direction.scale(breezeWindCharge.tickCount >= Config.arrowBurst ? Config.arrowSpeed * 2 : Config.arrowBurstSpeed));
                    particleAtEntity(breezeWindCharge, new TrailParticleOption(target.position().add(0, 1, 0), 0x0000FF, 10), 0f, 0f, 0f, 10, 0);


                }
                if (breezeWindCharge.tickCount > Config.arrowLifeTime) {
                    breezeWindCharge.kill(level);
                }
                sendPacketToAllPlayers(level, new ClientboundSetEntityMotionPacket(breezeWindCharge));
                PositionMoveRotation change = new PositionMoveRotation(breezeWindCharge.position(), breezeWindCharge.getDeltaMovement(), breezeWindCharge.getYRot(), breezeWindCharge.getXRot());
                ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(breezeWindCharge.getId(), change, Set.of(), breezeWindCharge.onGround());
                sendPacketToAllPlayers(level, teleportPacket);
            }
            default -> {
            }
        }
    }
    public static void stringBar(ServerPlayer player)
    {
        float progress = (player.getInventory().countItem(Items.STRING)% stringRequiredForBind)/(float) stringRequiredForBind;
        int stacks = player.getInventory().countItem(Items.STRING)/ stringRequiredForBind;
        player.connection.send(new ClientboundSetExperiencePacket(progress, stringRequiredForBind, stacks));
    }

    public static void killDragonAndBeginEnderFight(EnderDragon dragon, ServerLevel level)
    {
        EnderDragonFight fight = level.getDragonFight();
        if (fight != null && fight.dragonUUID() != null) {
            fight.setDragonKilled(dragon);
            spawnBoss(level);
            dragon.discard();
        }
    }

    public static void playerTick(ServerPlayer player, ServerLevel serverLevel)
    {


        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0f);
        BlockPos standingOn = player.blockPosition().below();
        if (serverLevel.getBlockState(standingOn).is(Blocks.GRASS_BLOCK) || serverLevel.getBlockState(standingOn).is(Blocks.PODZOL))
        {
            serverLevel.setBlockAndUpdate(standingOn,Blocks.DIRT_PATH.defaultBlockState());
        }
        if (serverLevel.getBlockState(player.blockPosition()).is(Blocks.LEAF_LITTER))
        {
            player.gameMode.destroyBlock(player.blockPosition());
        }
        if(player.level().dimension() == Level.NETHER)
        {
            siftPlayerTick(player);
        }
        BindSystem.bindTick(player);
        JumpChainSystem.countJumpTick(player);
        tickPlayerSpecifics(player);
        WetIchorSystem.wetIchorTick(player);
        SheepSystem.sheepTick(player);
        GlowflySystem.glowflyTick(player);
        hasGlowFlies = hasGlowFlies || GlowflySystem.isPlayerInfected(player);
        JumpAttackCountSystem.countJumpAttackTick(player);
        EndRespawnSystem.endRespawnTick(player);
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
                    grantAdvancement(player,"main/endfall");
                    attribute.setBaseValue(-0.36);
                }
                else
                {
                    attribute.setBaseValue(0.08);
                }
            }
            changeAttribute(player,Attributes.FALL_DAMAGE_MULTIPLIER,0);
        }
        else
        {
            changeAttribute(player,Attributes.FALL_DAMAGE_MULTIPLIER,1);
        }

        if (player.level().dimension().equals(Level.NETHER) && player.level() instanceof ServerLevel level && level.getGameTime() % Config.siftParticleTickMod == 0)
        {
            SiftParticles(level,player,ParticleTypes.SCULK_SOUL,1.02,0);
            SiftParticles(level,player,ParticleTypes.END_ROD,0.5,0.25);
        }
        stringBar(player);
        player.resetRecipes(player.level().getServer().getRecipeManager().getRecipes());
        miscAdvancementChecks(player);
    }

    public static void siftPlayerTick(ServerPlayer player)
    {
        if (Objects.requireNonNull(tide) == GeneralUtils.Tide.Thrive) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1, 1, true, true));
            if(player.tickCount % 40 == 0)
            {
                player.heal(1);
            }
        } else {
            player.removeEffect(MobEffects.REGENERATION);
        }
        player.sendOverlayMessage(Component.literal("Current Tide: " + getCurrentTide(player.level()).name()));
    }
    public static void miscAdvancementChecks(ServerPlayer player)
    {
        if(WetIchorSystem.isPlayerIchorized(player) && WetIchorSystem.isPlayerWet(player) && GlowflySystem.isPlayerInfected(player) && SheepSystem.isPlayerSleepy(player))
        {
            grantAdvancement(player,"challenges/alleffects");
        }
        if(player.isDeadOrDying())
        {
            grantAdvancement(player,"main/death");
            if(player.getY() < (player.level().dimension() == Level.END ? 0 : -64))
            {
                grantAdvancement(player,"challenges/voiddeath");
            }
        }
    }

    public static void lookAtSun(ServerPlayer player)
    {
        grantAdvancement(player,"main/sunlook");
        if(!player.hasEffect(MobEffects.BLINDNESS))
        {
            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("Don't Look at the Sun!")));
            player.addEffect( new MobEffectInstance(MobEffects.BLINDNESS, 60, 1, true, false));
            player.setRemainingFireTicks(60);

        }

    }

    public static <T extends net.minecraft.core.particles.ParticleOptions> void SiftParticles(ServerLevel level, ServerPlayer player, T particleType, double aboveBelow, double dy)
    {
        RandomSource random = level.getRandom();
        BlockPos playerPos = player.blockPosition();

        for (int i = 0; i < Config.siftParticleAmount; i++) {
            int x = playerPos.getX() + random.nextInt(Config.siftParticleHorDistance*2) - Config.siftParticleHorDistance;
            int z = playerPos.getZ() + random.nextInt(Config.siftParticleHorDistance*2) - Config.siftParticleHorDistance;
            for (int y = playerPos.getY() + Config.siftParticleVertDistance; y >= playerPos.getY() - Config.siftParticleVertDistance; y--)
            {
                BlockPos pos = new BlockPos(x, y, z);
                if (level.getFluidState(pos).is(FluidTags.WATER) && level.getFluidState(pos).isSource() && level.getBlockState(pos.above()).isAir())
                {
                    level.sendParticles(player, particleType, false, true, x+0.5, y + aboveBelow, z+0.5, 1, 0.25, dy, 0.25, 0);
                    break;
                }
            }
        }
    }

    public static void pandaTick(Panda panda)
    {
        panda.setTarget(panda.level().getNearestPlayer(panda,10));
    }

    public static void axolotlTick(Axolotl axolotl, ServerLevel serverLevel)
    {
        ServerPlayer player = hasGlowFlies ? (ServerPlayer)serverLevel.getNearestPlayer(TargetingConditions.forCombat().range(30).selector((p,_) -> GlowflySystem.isPlayerInfected(p)), axolotl) : null;
        if (player != null) {axolotl.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, player);}
        if(axolotl.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isPresent() && !GlowflySystem.isPlayerInfected(axolotl.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get()))
        {
            axolotl.getBrain().clearMemories();
        }
        if(!axolotl.isInWater())
        {
            if(axolotl.onGround() && axolotl.level().getRandom().nextBoolean())
            {
                axolotl.jumpFromGround();
                playSoundAtEntity(axolotl,SoundEvents.SALMON_FLOP,1,1);
            }
            changeAttribute(axolotl,Attributes.MOVEMENT_SPEED,5);
            changeAttribute(axolotl,Attributes.AIR_DRAG_MODIFIER,0.5);

        }
        else
        {
            changeAttribute(axolotl,Attributes.MOVEMENT_SPEED,1);
        }

    }

    public static void endermanTick(Enderman enderman)
    {
        endFightTick(enderman);
    }


    public static void chickenTick(Chicken chicken, ServerLevel serverLevel) {

        if(serverLevel.getNearestPlayer(chicken,5) != null) {
            GeneralUtils.replaceEntity(serverLevel,chicken, EntityTypes.PHANTOM,1);
        }
    }


    public static void zombieTick(Zombie zombie, ServerLevel serverLevel)
    {
        Vec3 belowBlock = zombie.position().subtract(0,1,0);
        boolean clutch = (zombie.fallDistance >= 2);
        boolean inDanger = false;

        if(serverLevel.getNearestPlayer(zombie,5) != null)
        {
            if(serverLevel.getBlockState(zombie.blockPosition().above(2)).is(BlockTags.LEAVES))
            {
                serverLevel.setBlockAndUpdate(zombie.blockPosition().above(2), Blocks.AIR.defaultBlockState());
            }
            inDanger = true;
            if(zombie.onGround())
            {
                zombie.jumpFromGround();
            }
            zombie.lookAt(EntityAnchorArgument.Anchor.EYES, belowBlock);
        }
        zombie.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(clutch ? Items.WATER_BUCKET : inDanger ? Items.OAK_PLANKS : Items.AIR,1));
        if(!zombie.onGround() && zombie.getDeltaMovement().y <= 0 && !serverLevel.getBlockState(BlockPos.containing(belowBlock.subtract(0,1,0))).canBeReplaced()  && serverLevel.getBlockState(BlockPos.containing(belowBlock)).canBeReplaced()) {
            if(clutch)
            {
                playSoundAtEntity(zombie, SoundEvents.BUCKET_EMPTY,1,1);
                serverLevel.setBlockAndUpdate(BlockPos.containing(belowBlock), Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 15));
            }
            else
            {
                playSoundAtEntity(zombie, SoundEvents.WOOD_PLACE,1,1);
                serverLevel.setBlockAndUpdate(BlockPos.containing(belowBlock), Blocks.OAK_PLANKS.defaultBlockState());

            }
            zombie.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT);
        }

    }

    @SuppressWarnings("unused")
    public static void hideEntityFromPlayer(ServerPlayer player, Entity targetEntity) {
        ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(targetEntity.getId());
        player.connection.send(destroyPacket);
    }

}
