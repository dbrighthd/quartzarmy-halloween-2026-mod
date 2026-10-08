package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

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

        if (target.equals(original))
        {
            return;
        }
        ci.cancel();

        ServerPlayerGameMode gameMode = player.gameMode;
        BlockState state = player.level().getBlockState(original);

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
