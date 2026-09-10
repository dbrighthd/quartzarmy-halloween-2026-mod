package dbrighthd.qahalloween2026.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.swing.text.html.parser.Entity;

public class PlayerMethodUtils {
    public static void playSoundAtEntity(LivingEntity entity, SoundEvent sound, float volume, float pitch)
    {
        entity.level().playSound(null, entity.blockPosition(), sound, SoundSource.MASTER, volume, pitch);
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
}
