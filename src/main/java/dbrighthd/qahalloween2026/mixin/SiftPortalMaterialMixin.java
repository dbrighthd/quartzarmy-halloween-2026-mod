package dbrighthd.qahalloween2026.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CryingObsidianBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(BaseFireBlock.class)
public class SiftPortalMaterialMixin {
        @Redirect(method = "isPortal", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
        private static boolean BaseFireBlock_isPortal(Predicate<BlockState> framePredicate, Object object) {
            BlockState state = (BlockState)object;
            if (state.is(Blocks.CINNABAR) || state.is(Blocks.WARPED_NYLIUM))
            {
                return true;
            }
            return framePredicate.test(state);
        }
}