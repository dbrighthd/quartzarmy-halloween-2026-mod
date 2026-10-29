package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.mainloop.Config;
import dbrighthd.qahalloween2026.systems.BindSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.grantAdvancement;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.informMiss;
@Mixin(ServerGamePacketListenerImpl.class)
public class PlayerActionMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(
            method = "handlePlayerAction",
            at = @At("HEAD"),
            cancellable = true
    )
    private void interceptBreak(ServerboundPlayerActionPacket packet, CallbackInfo ci)
        {
        if (packet.getAction() != ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK)
        {
            return;
        }


        BlockPos original = packet.getPos();
        BlockPos target = getRandomTarget(original);
        BindSystem.giveString(player,Config.stringPerBlockBreak);
        BlockState state = player.level().getBlockState(original);
        ServerPlayerGameMode gameMode = player.gameMode;
        if(state.is(Blocks.NETHERRACK))
        {
            gameMode.destroyBlock(original);
            for(int x = -1; x <= 1; x++)
            {
                for(int y = -1; y <= 1; y++)
                {
                    for(int z = -1; z <= 1; z++)
                    {
                        if(z == 0 && x == 0 && y == 0)
                        {
                            continue;
                        }
                        BlockPos testBlock = new BlockPos(original.getX()+x,original.getY()+y,original.getZ()+z);
                        if(player.level().getBlockState(testBlock).is(Blocks.NETHERRACK))
                        {
                            gameMode.destroyBlock(testBlock);
                        }
                    }
                }
            }
            return;
        }
        if (target.equals(original))
        {
            return;
        }
        ci.cancel();
        if(player.level().getBlockState(target).is(Blocks.BEDROCK))
        {
            grantAdvancement(player,"challenges/bedrock");
        }
        gameMode.destroyBlock(target);

        informMiss(player);
        player.connection.send(new ClientboundBlockUpdatePacket(original, state));
        player.connection.send(new ClientboundBlockDestructionPacket(player.getId(), original, -1));
        ((ServerGamePacketListenerImpl) (Object) this).ackBlockChangesUpTo(packet.getSequence());

    }

    @Unique
    private BlockPos getRandomTarget(BlockPos original)
    {
        if (Config.breakMissChance <= 0.0)
        {
            return original;
        }
        List<BlockPos> candidates = new ArrayList<>();

        for (Direction direction : Direction.values())
        {
            BlockPos pos = original.relative(direction);
            if (!player.level().getBlockState(pos).isAir())
            {
                candidates.add(pos);
            }
        }

        if (candidates.isEmpty())
        {
            return original;
        }

        if (player.level().getRandom().nextDouble() >= Config.breakMissChance)
        {
            return original;
        }

        return candidates.get(player.level().getRandom().nextInt(candidates.size()));
    }
}
