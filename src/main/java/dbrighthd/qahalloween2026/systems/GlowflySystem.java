package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TimerData;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;

public class GlowflySystem {
    public static void glowflyTick(ServerPlayer player) {
        TimerData dataThisTick = player.getAttachedOrCreate(DataAttachments.GLOWFLY_DATA);
        int glowFlyTicks = dataThisTick.ticksLeft();

        if(player.level().getBlockState(player.blockPosition()).getBlock() == Blocks.FIREFLY_BUSH || player.level().getBlockState(player.blockPosition().above()).getBlock() == Blocks.FIREFLY_BUSH)
        {
            glowFlyTicks = Config.glowFlyTicks;
        }
        if(glowFlyTicks > 0)
        {
            grantAdvancement(player,"main/glowflies");
            player.setGlowingTag(true);
            particleAtEntity(player, ParticleTypes.FIREFLY,0.1f,0.1f,0.1f,5,100);
            particleAtEntity(player,new TrailParticleOption(player.position().add(0,1,0), 0xFFFFAA, 10), 1, 1, 1,2, 0);
            if(player.tickCount % 5 == 0)
            {
                playSoundAtEntity(player, SoundEvents.FIREFLY_BUSH_IDLE,100,1);
            }
            glowFlyTicks--;
        }
        else
        {
            player.setGlowingTag(false);
        }
        player.setAttached(DataAttachments.GLOWFLY_DATA,new TimerData(glowFlyTicks));
    }

    public static boolean isPlayerInfected(LivingEntity player)
    {
        return (player.getAttachedOrCreate(DataAttachments.GLOWFLY_DATA).ticksLeft() > 0);
    }

    public static void setInfected(Player player, int infectedTicks)
    {
        player.setAttached(DataAttachments.GLOWFLY_DATA,new TimerData(infectedTicks));
    }
}
