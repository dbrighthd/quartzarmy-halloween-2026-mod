package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.GlowflyData;
import dbrighthd.qahalloween2026.attachments.IchorData;
import dbrighthd.qahalloween2026.attachments.WetData;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import javax.xml.crypto.Data;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.particleAtEntity;

public class WetIchorSystem {



    public static void wetIchorTick(ServerPlayer player)
    {
        boolean sift = player.level().dimension() == Level.NETHER;
        WetData wetDataThisTick = updateWet(player.getAttachedOrCreate(DataAttachments.WET_DATA),player.isInWaterOrRain(), sift);
        IchorData ichorDataThisTick = updateIchor(player.getAttachedOrCreate(DataAttachments.ICHOR_DATA),player.isInWaterOrRain(), sift);
        boolean wet = wetDataThisTick.wetState() > 0;
        boolean ichorized = ichorDataThisTick.ichorState() > 0;
        double frictionValue =  wet ? Config.maxSlipperyFriction : 1;
        changeAttribute(player, Attributes.FRICTION_MODIFIER,frictionValue);
        changeAttribute(player, Attributes.AIR_DRAG_MODIFIER,wet ? Config.maxAirSlipperyFriction : 1);
        if(wet)
        {
            particleAtEntity(player,ParticleTypes.RAIN,0.5f,1,0.5f,10,1);
        }
        if(ichorized)
        {
            particleAtEntity(player,ParticleTypes.SOUL_FIRE_FLAME,0.5f,1,0.5f,10,0);
            if(player.tickCount % (player.isInWaterOrRain() ? Config.ichorTickModInIchor : Config.ichorTickMod) == 0) // no rain in sift
            {
                player.hurtServer(player.level(),player.level().damageSources().inFire(),Config.ichorDamageAmount);
            }
        }
        player.setAttached(DataAttachments.ICHOR_DATA,ichorDataThisTick);
        player.setAttached(DataAttachments.WET_DATA, wetDataThisTick);
    }


    private static WetData updateWet(WetData oldData, boolean isInWater, boolean isSift)
    {
        return new WetData(Math.clamp(isInWater && !isSift ? Config.maxWet : oldData.wetState() -1,0, Config.maxWet));
    }

    private static IchorData updateIchor(IchorData oldData, boolean isInWater, boolean isSift)
    {
        return new IchorData(Math.clamp(isInWater && isSift ? Config.maxIchor : oldData.ichorState() -1,0, Config.maxIchor));
    }

    public static boolean isPlayerWet(LivingEntity player)
    {
        return (player.getAttachedOrCreate(DataAttachments.WET_DATA).wetState() > 0);
    }
    public static boolean isPlayerIchorized(LivingEntity player)
    {
        return (player.getAttachedOrCreate(DataAttachments.ICHOR_DATA).ichorState() > 0);
    }
    public  static void setIchorData(ServerPlayer player, int ichorState)
    {
        player.setAttached(DataAttachments.ICHOR_DATA,new IchorData(ichorState));

    }

    public  static void setWet(ServerPlayer player, int wetState)
    {
        player.setAttached(DataAttachments.WET_DATA,new WetData(wetState));
    }
}
