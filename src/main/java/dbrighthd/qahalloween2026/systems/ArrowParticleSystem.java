package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.BooleanData;
import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class ArrowParticleSystem {

    public static void arrowParticleTick(ServerPlayer player)
    {
        if(player.onGround() || player.getDeltaMovement().length() < 1.3)
        {
            setShowArrowParticles(player, false);
        }
        if(getShowArrowParticles(player))
        {
            PlayerMethodUtils.particleAtEntityOffset(player,new Vec3(0,1,0), ParticleTypes.CRIT,0,0,0,1,0);
        }
    }

    public static void setShowArrowParticles(ServerPlayer player, boolean value)
    {
        player.setAttached(DataAttachments.PLAYER_ARROW_PARTICLE_DATA,new BooleanData(value));
    }

    public static boolean getShowArrowParticles(ServerPlayer player)
    {
        return player.getAttachedOrCreate(DataAttachments.PLAYER_ARROW_PARTICLE_DATA).value();
    }
}
