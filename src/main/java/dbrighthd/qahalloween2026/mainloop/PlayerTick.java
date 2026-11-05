package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.systems.*;
import dbrighthd.qahalloween2026.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;

import java.util.Objects;
import java.util.Set;

import static dbrighthd.qahalloween2026.mainloop.Config.stringRequiredForBind;
import static dbrighthd.qahalloween2026.mainloop.PlayerSpecificStuff.tickPlayerSpecifics;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.getCurrentTide;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.grantAdvancement;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.isLookingAtSun;

public class PlayerTick {

    public static void doPlayerTick(ServerPlayer player, ServerLevel serverLevel)
    {
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0f);
        PlayerTick.dirtPathCheck(player,serverLevel);
        PlayerTick.siftPlayerTick(player);
        PlayerTick.playerDataAttachments(player);
        GlobalVars.hasGlowFlies = GlobalVars.hasGlowFlies || GlowflySystem.isPlayerInfected(player);
        JumpAttackCountSystem.countJumpAttackTick(player);
        EndRespawnSystem.endRespawnTick(player);
        PlayerTick.lookAtSunCheck(player);
        PlayerTick.endGravityCheck(player);
        stringBar(player);
        player.resetRecipes(player.level().getServer().getRecipeManager().getRecipes());
        miscAdvancementChecks(player);
    }


    public static void playerDataAttachments(ServerPlayer player)
    {
        BindSystem.bindTick(player);
        JumpChainSystem.countJumpTick(player);
        tickPlayerSpecifics(player);
        WetIchorSystem.wetIchorTick(player);
        SheepSystem.sheepTick(player);
        GlowflySystem.glowflyTick(player);
        ArrowParticleSystem.arrowParticleTick(player);
    }

    public static void beforeTick(ServerPlayer player)
    {
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0f);
        if(player.position().y < 300 || player.position().y > 305)
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

    public static void dirtPathCheck(ServerPlayer player, ServerLevel serverLevel)
    {
        if(player.gameMode.isSurvival())
        {
            BlockPos standingOn = player.blockPosition().below();
            if (serverLevel.getBlockState(standingOn).is(Blocks.GRASS_BLOCK) || serverLevel.getBlockState(standingOn).is(Blocks.PODZOL))
            {
                serverLevel.setBlockAndUpdate(standingOn,Blocks.DIRT_PATH.defaultBlockState());
            }
            if (serverLevel.getBlockState(player.blockPosition()).is(Blocks.LEAF_LITTER))
            {
                player.level().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK,player.blockPosition(), Block.getId(Blocks.LEAF_LITTER.defaultBlockState()));
                player.gameMode.destroyBlock(player.blockPosition());
            }
        }
    }

    public static void lookAtSunCheck(ServerPlayer player)
    {
        if(!isLookingAtSun(player))
        {
            return;
        }
        grantAdvancement(player,"main/sunlook");
        if(!player.hasEffect(MobEffects.BLINDNESS))
        {
            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("Don't Look at the Sun!")));
            player.addEffect( new MobEffectInstance(MobEffects.BLINDNESS, 60, 1, true, false));
            player.setRemainingFireTicks(60);

        }
    }

    public static void endGravityCheck(ServerPlayer player)
    {
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
            if(player.position().length() > 700)
            {
                //i thought this would be a funny way to limit position and. yeah kinda
                player.teleportTo(player.level(),player.position().x * 0.97, player.position().y, player.position().z * 0.97, Set.of(), player.getYRot(), player.getXRot(), false);
            }
        }
        else
        {
            changeAttribute(player,Attributes.GRAVITY,0.08);
            changeAttribute(player,Attributes.FALL_DAMAGE_MULTIPLIER,1);
        }

    }


    public static void siftPlayerTick(ServerPlayer player)
    {
        if(player.level().dimension() != Level.NETHER)
        {
            return;
        }
        if (player.level() instanceof ServerLevel level && level.getGameTime() % Config.siftParticleTickMod == 0)
        {
            SiftParticles(level,player, ParticleTypes.SCULK_SOUL,1.02,0);
            SiftParticles(level,player,ParticleTypes.END_ROD,0.5,0.25);
        }
        if (Objects.requireNonNull(GlobalVars.tide) == GeneralUtils.Tide.Thrive) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 2, 1, true, false));
            if(player.tickCount % 40 == 0)
            {
                player.heal(1);
            }
        } else {
            player.removeEffect(MobEffects.REGENERATION);
        }
        player.sendOverlayMessage(Component.literal("Current Tide: " + getCurrentTide(player.level()).name()));
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


    public static void stringBar(ServerPlayer player)
    {
        float progress = (player.getInventory().countItem(Items.STRING)% stringRequiredForBind)/(float) stringRequiredForBind;
        int stacks = player.getInventory().countItem(Items.STRING)/ stringRequiredForBind;
        player.connection.send(new ClientboundSetExperiencePacket(progress, stringRequiredForBind, stacks));
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
            if(player.getY() < 0 && player.level().dimension() == Level.END)
            {
                grantAdvancement(player,"challenges/voiddeath");
            }
        }
    }
}
