package dbrighthd.qahalloween2026.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "Lnet/minecraft/server/level/ServerLevel;getSeed()J", at = @At("HEAD"), cancellable = true)
    private void changeSeed(CallbackInfoReturnable<Long> cir) {
        ServerLevel level = (ServerLevel) (Object) this;
        if(level.dimension().equals(ServerLevel.NETHER)){
            cir.setReturnValue(-1611025626L);

        }
    }
}
