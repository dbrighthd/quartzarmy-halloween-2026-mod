package dbrighthd.qahalloween2026.mixin;

import net.minecraft.world.level.levelgen.densityfunction.generator.EndIslandFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EndIslandFunction.class)
public class EndIslandStupidMixin {
        @ModifyConstant(
                method = "getHeightValue",
                constant = @Constant(longValue = 4096L)
        )
        private static long qahalloween$changeEndGap(long original)
        {
            return 512L;
        }
}
