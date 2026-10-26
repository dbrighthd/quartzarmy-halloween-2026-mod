package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.informMiss;

@Mixin(ServerGamePacketListenerImpl.class)
public class PlayerUseItemOnMixin {

    @Shadow
    public ServerPlayer player;

    @ModifyVariable(
            method = "handleUseItemOn",
            at = @At("STORE"),
            name = "blockHit")
    private BlockHitResult changePlacementTarget(BlockHitResult originalHit, ServerboundUseItemOnPacket packet) {
        BlockPos clicked = originalHit.getBlockPos();
        Direction intendedDirection = originalHit.getDirection();

        InteractionHand hand = packet.hand();
        ItemStack stack = player.getItemInHand(hand);
        if(!(stack.getItem() instanceof BlockItem blockItem))
        {
            return originalHit;
        }
        BlockPlaceContext contect = new BlockPlaceContext(new UseOnContext(player, hand, originalHit));
        BlockState placementState = blockItem.getBlock().getStateForPlacement(contect);

        if (!player.level().isUnobstructed(placementState,clicked.relative(intendedDirection), CollisionContext.of(player)))
        {
            return originalHit;
        }

        BlockPos intended = clicked.relative(intendedDirection);
        BlockPos target = getRandomPlaceTarget(placementState, intended);

        if (target.equals(intended)) {
            return originalHit;
        }
        informMiss(player);
        return new BlockHitResult(Vec3.atCenterOf(target).subtract(0,-1,0), Direction.UP, target, false);
    }

    @Unique
    private BlockPos getRandomPlaceTarget(BlockState placementState, BlockPos intended)
    {
        if (Config.placeMissChance <= 0.0)
        {
            return intended;
        }

        List<BlockPos> candidates = new ArrayList<>();

        for (Direction direction : Direction.values())
        {
            BlockPos target = intended.relative(direction);

            if (target.equals(intended))
            {
                continue;
            }
            boolean canPlace = placementState.canSurvive(player.level(), target) && player.level().isUnobstructed(placementState, target, CollisionContext.of(player));
            BlockState state = player.level().getBlockState(target);
            if (canPlace && state.canBeReplaced())
            {
                candidates.add(target);
            }
        }

        if (candidates.isEmpty())
        {
            return intended;
        }
        if (player.level().getRandom().nextDouble() >= Config.placeMissChance)
        {
            return intended;
        }
        return candidates.get(player.level().getRandom().nextInt(candidates.size()));
    }
}
