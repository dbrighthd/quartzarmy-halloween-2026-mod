package dbrighthd.qahalloween2026.mixin;

import net.minecraft.world.level.biome.TheEndBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TheEndBiomeSource.class)

public class EndBiomeMixin {
    @ModifyConstant(
            method = "getNoiseBiome",
            constant = @Constant(longValue = 4096L)
    )
    private static long qahalloween$changeEndGap(long original)
    {
        return 512L;
    }
}

