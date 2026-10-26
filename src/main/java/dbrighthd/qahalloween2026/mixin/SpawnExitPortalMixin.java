package dbrighthd.qahalloween2026.mixin;

import net.minecraft.world.level.dimension.end.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragonFight.class)
public class SpawnExitPortalMixin {
    @Inject(method = "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;spawnExitPortal(Z)V", at = @At("HEAD"), cancellable = true)
    private void cancelTick(CallbackInfo ci) {
        ci.cancel();
    }
}
