package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "getSeed()J", at = @At("HEAD"), cancellable = true)
    private void changeSeed(CallbackInfoReturnable<Long> cir) {
        if(Config.useHardcodedSeed)
        {
            ServerLevel level = (ServerLevel) (Object) this;
            if(level.dimension().equals(ServerLevel.NETHER)){
                cir.setReturnValue(-1611025626L);
            }
            else
            {
                cir.setReturnValue(8945791016058249044L);
            }
        }

    }
}
