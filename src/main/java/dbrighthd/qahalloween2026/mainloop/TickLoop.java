package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.systems.BindSystem;
import dbrighthd.qahalloween2026.events.EventsStorage;
import dbrighthd.qahalloween2026.systems.GlowflySystem;
import dbrighthd.qahalloween2026.systems.SheepSystem;
import dbrighthd.qahalloween2026.systems.WetSystem;
import dbrighthd.qahalloween2026.utils.GeneralUtils;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
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
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.BreezeWindCharge;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.phys.Vec3;

import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import static dbrighthd.qahalloween2026.Qahalloween2026.MOD_ID;
import static dbrighthd.qahalloween2026.mainloop.Config.stringRequiredForBind;
import static dbrighthd.qahalloween2026.mainloop.EndFight.endFightTick;
import static dbrighthd.qahalloween2026.mainloop.EndFight.spawnBoss;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;

public class TickLoop {
    public static void init()
    {
        ServerTickEvents.END_SERVER_TICK.register(TickLoop::tick);
        ServerTickEvents.END_SERVER_TICK.register(EventsStorage::refreshAndDoStuff);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
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
    }


    public static void tick(MinecraftServer server)
    {
        for(ServerPlayer player : server.getPlayerList().getPlayers())
        {
            playerTick(player, player.level());
        }
        for (ServerLevel level : server.getAllLevels()) {
            for(Entity entity : level.getAllEntities())
            {
                entityTick(entity);
            }
        }

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
            case EnderDragon dragon -> {
                EnderDragonFight fight = level.getDragonFight();
                if (fight != null && fight.dragonUUID() != null) {
                    fight.setDragonKilled(dragon);
                    spawnBoss(level);
                    dragon.discard();
                }
            }
            case Phantom phantom -> phantom.setRemainingFireTicks(-10);
            case ExperienceOrb _ -> entity.kill(level);
            case Zombie zombie -> zombieTick(zombie, level);
            case Enderman enderman -> {
                if (level.dimension() == Level.END) {

                    endermanTick(enderman, level);
                }
            }
            case Boat boat -> boat.setDeltaMovement(0, 1, 0);
            case Sheep sheep -> sheep.setHealth(100);
            case Arrow arrow -> {
                TargetData arrowData = arrow.getAttachedOrCreate(DataAttachments.TARGET_DATA);
                Entity target = level.getEntity(arrowData.target());
                if (!arrowData.playerOwned()) {
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
                    sendPacketToAllPlayers(level, new ClientboundSetEntityMotionPacket(arrow));
                    PositionMoveRotation change = new PositionMoveRotation(arrow.position(), arrow.getDeltaMovement(), arrow.getYRot(), arrow.getXRot());
                    ClientboundTeleportEntityPacket teleportPacket = new ClientboundTeleportEntityPacket(arrow.getId(), change, Set.of(), arrow.onGround());
                    sendPacketToAllPlayers(level, teleportPacket);
                }


            }
            case BreezeWindCharge breezeWindCharge -> {
                TargetData arrowData = breezeWindCharge.getAttachedOrCreate(DataAttachments.TARGET_DATA);
                Entity target = level.getEntity(arrowData.target());
                if (!arrowData.playerOwned()) {
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
    private static UUID dashBossBarId(ServerPlayer player) {
        return UUID.nameUUIDFromBytes(
                (MOD_ID + ":string:" + player.getUUID()).getBytes(StandardCharsets.UTF_8)
        );
    }

    public static void playerTick(ServerPlayer player, ServerLevel serverLevel)
    {
        if(player.isDeadOrDying())
        {
            grantAdvancement(player,"main/death");
        }
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
        GlowflySystem.glowflyTick(player);
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
                    grantAdvancement(player,"end/endfall");
                    attribute.setBaseValue(-0.5);
                }
                else
                {
                    attribute.setBaseValue(0.08);
                }
            }
        }

        if (player.level().dimension().equals(Level.NETHER) && player.level() instanceof ServerLevel level && level.getGameTime() % Config.siftParticleTickMod == 0)
        {
            SiftParticles(level,player,ParticleTypes.SCULK_SOUL,1.02,0);
            SiftParticles(level,player,ParticleTypes.END_ROD,0.5,0.25);
        }
        stringBar(player);
    }

    public static void lookAtSun(ServerPlayer player)
    {
        grantAdvancement(player,"main/sunlook");
        if(!player.hasEffect(MobEffects.BLINDNESS))
        {
            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("Don't Look at the Sun!")));
            player.addEffect( new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));
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
                    level.sendParticles(player, particleType, false, true, x+0.5, y + aboveBelow, z+0.5, 3, 0.25, dy, 0.25, 0);
                    break;
                }
            }
        }
    }

    public static void axolotlTick(Axolotl axolotl, ServerLevel serverLevel)
    {
        ServerPlayer player = (ServerPlayer)serverLevel.getNearestPlayer(TargetingConditions.forCombat().range(20).selector((p,_) -> GlowflySystem.isPlayerInfected(p)), axolotl);
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
            changeAttribute(axolotl,Attributes.MOVEMENT_SPEED,10);
            changeAttribute(axolotl,Attributes.AIR_DRAG_MODIFIER,0.5);

        }
        else
        {
            changeAttribute(axolotl,Attributes.MOVEMENT_SPEED,1);
        }

    }

    public static void endermanTick(Enderman enderman, ServerLevel serverLevel)
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

    public static void hideEntityFromPlayer(ServerPlayer player, Entity targetEntity) {
        ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(targetEntity.getId());
        player.connection.send(destroyPacket);
    }
}
