package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.BooleanData;
import dbrighthd.qahalloween2026.attachments.ChainedActionData;
import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.chainJumpCheck;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.particleAtEntity;

public class JumpChainSystem {
    public static void countJumpTick(ServerPlayer player) {
        if (player == null) {
            return;
        }
        if(isInBigJump(player))
        {
            particleAtEntity(player, ParticleTypes.CRIT,0,0,0,1,0);
        }
        if(player.getDeltaMovement().y < 0.5 || player.onGround())
        {
            JumpChainSystem.setIsInBigJump(player,false);
        }
        ChainedActionData jumpData = getJumpData(player);
        int jumpChain = jumpData.chainedAmount();
        int groundTime = jumpData.chainTime();
        if(player.onGround() || !chainJumpCheck(player))
        {
            groundTime++;
        }
        else
        {
            groundTime = 0;
        }
        if(groundTime > Config.tripleJumpGrace || jumpChain > 2)
        {
            jumpChain = 0;
        }
        changeAttribute(player, Attributes.JUMP_STRENGTH, jumpChain == 2 ? 0.42* Config.tripleJumpBoost : 0.42);
        player.setAttached(DataAttachments.JUMP_CHAIN_DATA,new ChainedActionData(jumpChain,groundTime));
    }

    public static ChainedActionData getJumpData(ServerPlayer player)
    {
        return player.getAttachedOrCreate(DataAttachments.JUMP_CHAIN_DATA);
    }

    public static int addJump(ServerPlayer player)
    {
        ChainedActionData oldData = getJumpData(player);
        player.setAttached(DataAttachments.JUMP_CHAIN_DATA,new ChainedActionData(oldData.chainedAmount()+1, oldData.chainTime()));
        return oldData.chainedAmount()+1;
    }

    public static void setIsInBigJump(ServerPlayer player, boolean value)
    {
        player.setAttached(DataAttachments.PLAYER_JUMP_PARTICLE_DATA,new BooleanData(value));
    }

    public static boolean isInBigJump(ServerPlayer player)
    {
        return player.getAttachedOrCreate(DataAttachments.PLAYER_JUMP_PARTICLE_DATA).value();
    }
}
