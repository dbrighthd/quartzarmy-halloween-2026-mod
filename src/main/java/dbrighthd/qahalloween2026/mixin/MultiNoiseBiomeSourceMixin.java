package dbrighthd.qahalloween2026.mixin;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin {

    @Shadow
    protected abstract Stream<Holder<Biome>> collectPossibleBiomes();

    @Inject(
            method = "getNoiseBiome",
            at = @At("RETURN"),
            cancellable = true
    )
    private void replaceBiomes(
            Climate.TargetPoint target, CallbackInfoReturnable<Holder<Biome>> cir
    ) {
        Holder<Biome> biome = cir.getReturnValue();

        if (biome.is(Biomes.SNOWY_PLAINS) || biome.is(Biomes.SNOWY_TAIGA) || biome.is(Biomes.SNOWY_BEACH) || biome.is(Biomes.ICE_SPIKES)) {
            Holder<Biome> replacement = this.collectPossibleBiomes()
                    .toList()
                    .stream()
                    .filter(holder -> holder.is(Biomes.DAPPLED_FOREST))
                    .findFirst()
                    .orElseThrow();
            cir.setReturnValue(replacement);
        }

        if (biome.is(BiomeTags.IS_DEEP_OCEAN)) {
            Holder<Biome> replacement = this.collectPossibleBiomes()
                    .toList()
                    .stream()
                    .filter(holder -> holder.is(Biomes.SWAMP))
                    .findFirst()
                    .orElseThrow();
            cir.setReturnValue(replacement);
        }
    }
}