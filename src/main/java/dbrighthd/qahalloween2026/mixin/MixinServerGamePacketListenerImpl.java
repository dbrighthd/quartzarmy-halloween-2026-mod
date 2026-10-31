package dbrighthd.qahalloween2026.mixin;

import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {

    @Inject(method = "send*", at = @At("HEAD"), cancellable = true)
    private void blockRecipePackets(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClientboundRecipeBookAddPacket) {
            ci.cancel();
        }
        else if (packet instanceof ClientboundRecipeBookRemovePacket) {
            ci.cancel();
        }
        else if (packet instanceof ClientboundRecipeBookSettingsPacket) {
            ci.cancel();
        }
        else if (packet instanceof ClientboundUpdateRecipesPacket) {
            ci.cancel();
        }
    }
}
