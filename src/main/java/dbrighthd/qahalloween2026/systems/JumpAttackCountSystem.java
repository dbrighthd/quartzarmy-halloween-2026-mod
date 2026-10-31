package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.ChainedActionData;
import net.minecraft.server.level.ServerPlayer;

public class JumpAttackCountSystem {

    public static void countJumpAttackTick(ServerPlayer player)
    {
        if(player == null)
        {
            return;
        }
        int jumpAmount = player.getAttachedOrCreate(DataAttachments.JUMP_ATTACK_DATA).chainedAmount();
        if(player.onGround())
        {
            jumpAmount = 0;
        }
        player.setAttached(DataAttachments.JUMP_ATTACK_DATA,new ChainedActionData(jumpAmount,0));
    }

    public static int addJumpAttack(ServerPlayer player, int amount)
    {
        int newChained = player.getAttachedOrCreate(DataAttachments.JUMP_ATTACK_DATA).chainedAmount()+amount;
        player.setAttached(DataAttachments.JUMP_ATTACK_DATA,new ChainedActionData(newChained,0));
        return newChained;
    }

    @SuppressWarnings("unused")
    public static int getJumpAttackChain(ServerPlayer player)
    {
        return player.getAttachedOrCreate(DataAttachments.JUMP_ATTACK_DATA).chainedAmount();
    }
}
