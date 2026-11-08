package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.attachments.TimerData;
import dbrighthd.qahalloween2026.systems.GlowflySystem;
import dbrighthd.qahalloween2026.utils.GeneralUtils;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

import static dbrighthd.qahalloween2026.mainloop.EndFight.endFightTick;
import static dbrighthd.qahalloween2026.mainloop.EndFight.spawnBoss;
import static dbrighthd.qahalloween2026.mainloop.GlobalVars.tudad;
import static dbrighthd.qahalloween2026.mainloop.TickLoop.hideEntityFromPlayer;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.playSoundAtEntity;

public class EntityTick {
    public static void entityTick(Entity entity)
    {
        if(entity == null)
        {
            return;
        }
        ServerLevel level = (ServerLevel)entity.level();
        switch (entity) {
            case Axolotl axolotl -> axolotlTick(axolotl, level);
            case Cat cat -> cat.setLying(true);
            case Chicken chicken -> chickenTick(chicken, level);
            case Silverfish silverfish -> pufferFihTick(silverfish,level);
            case EnderDragon dragon -> killDragonAndBeginEnderFight(dragon,level);
            case Phantom phantom -> phantom.setRemainingFireTicks(-10);
            case Panda panda -> pandaTick(panda);
            case ExperienceOrb _ -> entity.kill(level);
            case Zombie zombie -> zombieTick(zombie, level);
            case Enderman enderman -> endermanTick(enderman);
            case Boat boat -> boatTick(boat);
            case Ravager ravager -> ravagerTick(ravager);
            case Sheep sheep -> sheep.setHealth(100);
            case Arrow arrow -> arrowTick(arrow,level);
            case BreezeWindCharge breezeWindCharge -> breezeWindChargeTick(breezeWindCharge,level);
            default -> {}
        }
    }

    public static void killArrow(Arrow arrow, ServerLevel level, double dropChance)
    {
        PlayerMethodUtils.particleAtEntity(arrow, ParticleTypes.POOF,0,0,0,5,0.05);
        if(dropChance > arrow.getRandom().nextDouble())
        {
            ItemEntity item = EntityTypes.ITEM.create(level, EntitySpawnReason.COMMAND);
            if(item != null)
            {
                item.setItem(new ItemStack(Items.ARROW));
                item.setPos(arrow.position());
                level.addFreshEntity(item);
            }
        }
        arrow.kill(level);
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

    public static void arrowTick(Arrow arrow, ServerLevel level)
    {
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
                    killArrow(arrow, level,0.05);
                }
            }
            else
            {
                if (arrow.tickCount > Config.playerArrowLifeTime) {
                    killArrow(arrow, level,1);
                }
            }
            GeneralUtils.sendEntityPositionAndMotionPackets(level,arrow);

        }
    }

    public static void breezeWindChargeTick(BreezeWindCharge breezeWindCharge, ServerLevel level)
    {
        if(breezeWindCharge.getOwner() instanceof Player)
        {
            return;
        }
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
        GeneralUtils.sendEntityPositionAndMotionPackets(level,breezeWindCharge);
    }

    public static void ravagerTick(Ravager ravager)
    {
        if (Objects.requireNonNull(GlobalVars.tide) == GeneralUtils.Tide.Endure) {
            changeAttribute(ravager, Attributes.ATTACK_DAMAGE, 4);
        } else {
            changeAttribute(ravager, Attributes.ATTACK_DAMAGE, 1);
        }
    }

    public static void boatTick(Boat boat)
    {
        if(boat.tickCount > 40)
        {
            boat.setDeltaMovement(0, 2, 0);
        }
        else
        {
            boat.setDeltaMovement(0, 0.1, 0);
        }
        if(boat.hasControllingPassenger())
        {
            boat.ejectPassengers();
            boat.teleportRelative(0,0.1,0);
        }
        if(boat.position().y > 256)
        {
            boat.discard();
        }
    }

    public static void pufferFihTick(Silverfish fusefly, ServerLevel level)
    {
        fusefly.setNoGravity(true);
        fusefly.setSilent(true);
        BlockPos below = fusefly.blockPosition().below();
        BlockPos belowTwo = fusefly.blockPosition().below(2);
        if(!level.getBlockState(below).is(Blocks.AIR))
        {
            fusefly.setDeltaMovement(fusefly.getDeltaMovement().x,0.1, fusefly.getDeltaMovement().z);
        }
        else if (level.getBlockState(belowTwo).is(Blocks.AIR))
        {
            fusefly.setDeltaMovement(fusefly.getDeltaMovement().x,-0.1, fusefly.getDeltaMovement().z);
        }
        else
        {
            fusefly.setDeltaMovement(fusefly.getDeltaMovement().x, fusefly.getDeltaMovement().y*0.5, fusefly.getDeltaMovement().z);

        }
        if(fusefly.tickCount < 40)
        {
            return;
        }
        if(fusefly.tickCount == 41)
        {
            fusefly.setHealth(101);
            playSoundAtEntity(fusefly, SoundEvents.PUFFER_FISH_BLOW_OUT,1,1);
        }
        if(level.getNearestPlayer(fusefly,4) != null || fusefly.getHealth() < 100)
        {
            if(fusefly.tickCount % 5 == 0)
            {
                playSoundAtEntity(fusefly,SoundEvents.PUFFER_FISH_BLOW_UP,1,1);
                particleAtEntityOffset(fusefly, new Vec3(0,0.275,0), ParticleTypes.GUST,0,0,0,1,0);
            }
            particleAtEntityOffset(fusefly, new Vec3(0,0.275,0), ParticleTypes.POOF,0,0,0,5,level.dimension() == Level.NETHER && GlobalVars.tide == GeneralUtils.Tide.Thrive ? 0.2 : 0.5);
            fusefly.setHealth(fusefly.getHealth()-5);
        }
        if(fusefly.isDeadOrDying() || fusefly.getAirSupply() < 1 ||  level.getNearestPlayer(fusefly,2) != null)
        {
            playSoundAtEntity(fusefly,SoundEvents.PUFFER_FISH_STING,100,1);
            level.explode(fusefly, fusefly.getX(), fusefly.getY(), fusefly.getZ(), 0, Level.ExplosionInteraction.NONE);
            List<Entity> nearby = fusefly.level().getEntities(fusefly, fusefly.getBoundingBox().inflate(3), entity -> entity.distanceToSqr(fusefly) <= 3 * 3);
            for(Entity entity : nearby)
            {
                Vec3 explode = fusefly.position().subtract(entity.position().add(0,-1,0)).normalize().scale(-4);
                Vec3 movement = new Vec3(explode.x,1,explode.z);
                addAndSendMovementIfPlayer(entity, movement, false);
                if(entity instanceof ServerPlayer player)
                {
                    player.setIgnoreFallDamageFromCurrentImpulse(true,player.position());
                    grantAdvancement(player,"main/pufferfish");
                }

            }
            Entity newFuseFly = EntityTypes.SILVERFISH.create(level,EntitySpawnReason.COMMAND);
            if(newFuseFly != null)
            {
                newFuseFly.setPos(fusefly.position());
                level.addFreshEntity(newFuseFly);
            }
            fusefly.discard();

        }
    }



    public static void pandaTick(Panda panda)
    {
        panda.setTarget(panda.level().getNearestPlayer(panda,10));
    }

    public static void axolotlTick(Axolotl axolotl, ServerLevel serverLevel)
    {
        ServerPlayer player = GlobalVars.hasGlowFlies ? (ServerPlayer)serverLevel.getNearestPlayer(TargetingConditions.forCombat().range(30).selector((p, _) -> GlowflySystem.isPlayerInfected(p) && !p.entityTags().contains(PlayerSpecificStuff.MeanTag.Tudad.name())), axolotl) : null;
        if(tudad != null)
        {
            hideEntityFromPlayer(tudad,axolotl);
        }
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
        if (enderman.level().dimension() == Level.END) {
            endFightTick(enderman);
        }
    }


    public static void chickenTick(Chicken chicken, ServerLevel serverLevel) {

        if(serverLevel.getNearestPlayer(chicken,5) != null) {
            GeneralUtils.replaceEntity(serverLevel,chicken, EntityTypes.PHANTOM,1);
        }
    }


    public static void zombieTick(Zombie zombie, ServerLevel serverLevel)
    {
        if(zombie.getItemBySlot(EquipmentSlot.HEAD).is(Items.CARVED_PUMPKIN))
        {
            zombie.setItemSlot(EquipmentSlot.HEAD,new ItemStack(Items.AIR));
        }

        int mlgTicks = zombie.getAttachedOrCreate(DataAttachments.ZOMBIE_MLG_DATA).ticksLeft();
        Vec3 belowBlock = zombie.position().subtract(0,1,0);
        Vec3 belowBlockFallCheck = zombie.position().subtract(0,3,0);
        BlockState fallState = serverLevel.getBlockState(BlockPos.containing(belowBlockFallCheck));
        boolean clutch = (zombie.fallDistance >= 2 || fallState.is(Blocks.AIR) && zombie.fallDistance > 0.5);
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
        zombie.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(mlgTicks > 1 ? Items.BUCKET : mlgTicks > 0 ? Items.WATER_BUCKET : clutch ? Items.WATER_BUCKET : inDanger ? Items.OAK_PLANKS : Items.AIR,1));
        if(!zombie.onGround() && zombie.getDeltaMovement().y <= 0 && !serverLevel.getBlockState(BlockPos.containing(belowBlock.subtract(0,1,0))).canBeReplaced()  && serverLevel.getBlockState(BlockPos.containing(belowBlock)).canBeReplaced()) {
            if(clutch)
            {
                playSoundAtEntity(zombie, SoundEvents.BUCKET_EMPTY,1,1);
                mlgTicks = 5;
                serverLevel.setBlockAndUpdate(BlockPos.containing(belowBlock), Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 15));
            }
            else
            {
                playSoundAtEntity(zombie, SoundEvents.WOOD_PLACE,1,1);
                serverLevel.setBlockAndUpdate(BlockPos.containing(belowBlock), Blocks.OAK_PLANKS.defaultBlockState());

            }
            zombie.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT);
        }
        zombie.setAttached(DataAttachments.ZOMBIE_MLG_DATA,new TimerData(Math.max(mlgTicks-1,0)));
    }
}
