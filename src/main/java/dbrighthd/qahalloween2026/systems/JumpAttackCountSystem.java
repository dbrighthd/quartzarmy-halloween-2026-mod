package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.JumpAttackData;
import net.minecraft.server.level.ServerPlayer;

public class JumpAttackCountSystem {

    public static void countJumpTick(ServerPlayer player)
    {
        if(player == null)
        {
            return;
        }
        int jumpAmount = player.getAttachedOrCreate(DataAttachments.JUMP_ATTACK_DATA).attacksChained();
        if(player.onGround())
        {
            jumpAmount = 0;
        }
        player.setAttached(DataAttachments.JUMP_ATTACK_DATA,new JumpAttackData(jumpAmount));
    }

    public static int addJumpAttack(ServerPlayer player, int amount)
    {
        int newChained = player.getAttachedOrCreate(DataAttachments.JUMP_ATTACK_DATA).attacksChained()+amount;
        player.setAttached(DataAttachments.JUMP_ATTACK_DATA,new JumpAttackData(newChained));
        return newChained;
    }

    public static int getJumpChain(ServerPlayer player)
    {
        return player.getAttachedOrCreate(DataAttachments.JUMP_ATTACK_DATA).attacksChained();
    }
}
