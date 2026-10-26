package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.mainloop.Config;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.levelgen.feature.EndSpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Mixin(targets = "net.minecraft.world.level.levelgen.feature.EndSpikeFeature$SpikeCacheLoader")
public abstract class SpikeCacheLoaderMixin {

    @Inject(method = "load(Ljava/lang/Long;)Ljava/util/List;",
            at = @At("HEAD"), cancellable = true)
    private void customSpikes(Long seed, CallbackInfoReturnable<List<EndSpikeFeature.EndSpike>> cir) {
        int count = Config.spikeCount;
        double distance = Config.spikeDistance;

        IntArrayList sizes = Util.toShuffledList(
                IntStream.range(0, count),
                RandomSource.createThreadLocalInstance(seed)
        );

        List<EndSpikeFeature.EndSpike> result = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            double angle = 2.0 * Math.PI * i / count;

            int x = Mth.floor(distance * Math.cos(angle));
            int z = Mth.floor(distance * Math.sin(angle));

            int size = sizes.getInt(i);
            int radius = 2 + size / 3;
            int height = 76 + size * 3;
            boolean guarded = false;

            result.add(new EndSpikeFeature.EndSpike(
                    x, z, radius, height, guarded
            ));
        }

        cir.setReturnValue(result);
    }
}
