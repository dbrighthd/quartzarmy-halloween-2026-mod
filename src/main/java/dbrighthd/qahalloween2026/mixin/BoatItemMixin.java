package dbrighthd.qahalloween2026.mixin;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.grantAdvancement;

@Mixin(BoatItem.class)
public abstract class BoatItemMixin {

    @Inject(method = "use", at = @At("RETURN"))
    private void onBoatPlaced(
            Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!(cir.getReturnValue() instanceof InteractionResult.Success)) return;

        grantAdvancement(serverPlayer,"main/boat");
    }
}
