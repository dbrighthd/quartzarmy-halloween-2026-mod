package dbrighthd.qahalloween2026.mixin;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CryingObsidianBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(value = PortalShape.class, priority = 1001)
public class PortalShapeMixin {
    @Shadow
    private static @Final
    @Mutable Predicate<BlockState> FRAME;

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void PortalShape(CallbackInfo ci) {
        FRAME = (BlockState blockState) -> {
            return blockState.is(BlockTags.NETHER_PORTAL_FRAME) || (blockState.is(Blocks.CINNABAR) || blockState.is(Blocks.WARPED_NYLIUM));
        };
    }
}