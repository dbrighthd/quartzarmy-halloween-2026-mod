package dbrighthd.qahalloween2026.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BaseCoralPlantTypeBlock.class)
public class CoralMixin {
    @ModifyReturnValue(
            method = "scanForWater(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z",
            at = @At("RETURN")
    )
    private static boolean changeScanForWater(boolean original)
    {
        return true;
    }

}
