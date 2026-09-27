package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerGamePacketListenerImpl.class)
public class PlayerUseItemOnMixin {

    @Shadow
    public ServerPlayer player;

    @ModifyVariable(
            method = "handleUseItemOn",
            at = @At("STORE"),
            name = "blockHit")
    private BlockHitResult changePlacementTarget(BlockHitResult originalHit) {
        BlockPos clicked = originalHit.getBlockPos();
        Direction intendedDirection = originalHit.getDirection();

        BlockPos intended = clicked.relative(intendedDirection);
        BlockPos target = getRandomPlaceTarget(clicked, intended);

        if (target.equals(intended)) {
            return originalHit;
        }

        Direction targetDirection = getDirectionBetween(clicked, target);

        if (targetDirection == null) {
            return originalHit;
        }

        return new BlockHitResult(Vec3.atCenterOf(clicked), targetDirection, clicked, false);
    }

    @Unique
    private BlockPos getRandomPlaceTarget(BlockPos clicked, BlockPos intended)
    {
        if (Config.placeMissChance <= 0.0)
        {
            return intended;
        }

        List<BlockPos> candidates = new ArrayList<>();

        for (Direction direction : Direction.values())
        {
            BlockPos target = clicked.relative(direction);

            if (target.equals(intended))
            {
                continue;
            }

            BlockState state = player.level().getBlockState(target);

            if (state.isAir())
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

    @Unique
    private Direction getDirectionBetween(BlockPos from, BlockPos to)
    {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        if (dx == 1 && dy == 0 && dz == 0) {
            return Direction.EAST;
        }
        if (dx == -1 && dy == 0 && dz == 0) {
            return Direction.WEST;
        }
        if (dx == 0 && dy == 1 && dz == 0) {
            return Direction.UP;
        }
        if (dx == 0 && dy == -1 && dz == 0) {
            return Direction.DOWN;
        }
        if (dx == 0 && dy == 0 && dz == 1) {
            return Direction.SOUTH;
        }
        if (dx == 0 && dy == 0 && dz == -1) {
            return Direction.NORTH;
        }
        return null;
    }
}
