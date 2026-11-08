package dbrighthd.qahalloween2026.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dbrighthd.qahalloween2026.mainloop.PlayerTick.stringBar;

@Mixin(ServerPlayer.class)
public class OnItemPickupMixin {
    @Inject(method = "onItemPickup(Lnet/minecraft/world/entity/item/ItemEntity;)V", at = @At("RETURN"))
    private void checkString(ItemEntity entity, CallbackInfo ci)
    {
        if(entity.getItem().is(Items.STRING))
        {
            stringBar((ServerPlayer)(Object)this);
        }
    }
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZLnet/minecraft/util/Prediction;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    private void checkDroppedString(ItemStack itemStack, boolean thrownFromHand, Prediction prediction, CallbackInfoReturnable<ItemEntity> cir){
        if(itemStack.is(Items.STRING))
        {
            stringBar((ServerPlayer)(Object)this);
        }
    }

}
