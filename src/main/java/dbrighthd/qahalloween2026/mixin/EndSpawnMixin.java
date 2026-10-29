package dbrighthd.qahalloween2026.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class EndSpawnMixin {
    @Shadow
    @Final
    @Mutable
    public static BlockPos END_SPAWN_POINT;

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void qahalloween$changeEndSpawnPoint(CallbackInfo ci)
    {
        END_SPAWN_POINT = new BlockPos(200, 50, 0);
    }
}
