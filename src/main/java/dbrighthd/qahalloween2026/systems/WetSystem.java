package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.BindData;
import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.WetData;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.particleAtEntity;

public class WetSystem {


    public static void wetTick(ServerPlayer player)
    {
        WetData dataThisTick = updateWet(player.getAttachedOrCreate(DataAttachments.WET_DATA),player.isInWaterOrRain(), player.isOnPortalCooldown());
        boolean wet = dataThisTick.wetState() > 0;
        boolean sift = player.level().dimension() == Level.NETHER;
        double frictionValue =  sift ? 1 : wet ? Config.maxSlipperyFriction : 1;
        var attribute = player.getAttribute(Attributes.FRICTION_MODIFIER);
        if(attribute != null)
        {
            attribute.setBaseValue(frictionValue);
        }
        var air_attribute = player.getAttribute(Attributes.AIR_DRAG_MODIFIER);
        if(air_attribute != null)
        {
            air_attribute.setBaseValue(frictionValue);
        }
        if(wet)
        {
            if(sift && player.tickCount % (player.isInWaterOrRain() ? Config.ichorTickModInIchor : Config.ichorTickMod) == 0) // no rain in sift
            {
                player.hurtServer(player.level(),player.level().damageSources().inFire(),Config.ichorDamageAmount);
            }
            particleAtEntity(player,sift ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.RAIN,0.5f,1,0.5f,10,sift ? 0 : 1);
        }
        player.setAttached(DataAttachments.WET_DATA,dataThisTick);
    }


    private static WetData updateWet(WetData oldData, boolean isInWater, boolean isChangingDimension)
    {
        return new WetData(isChangingDimension ? 0 : Math.clamp(isInWater ? Config.maxWet : oldData.wetState() -1,0, Config.maxWet));
    }
}
