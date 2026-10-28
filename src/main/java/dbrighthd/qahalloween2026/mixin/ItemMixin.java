package dbrighthd.qahalloween2026.mixin;


import dbrighthd.qahalloween2026.mainloop.Config;
import dbrighthd.qahalloween2026.systems.WetIchorSystem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void onConsume(
            ItemStack itemStack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir
    ) {
        if (!(level instanceof ServerLevel)) return;
        if (!(entity instanceof ServerPlayer player)) return;

        if (itemStack.is(Items.RABBIT_FOOT))
        {
            if(Identifier.parse("minecraft:lava_bucket").equals(itemStack.get(DataComponents.ITEM_MODEL))) {
                player.setRemainingFireTicks(100);
            }
            if(Identifier.parse("minecraft:water_bucket").equals(itemStack.get(DataComponents.ITEM_MODEL))) {
                if(Component.literal("Ichor Bucket").equals(itemStack.get(DataComponents.ITEM_NAME)))
                {
                    WetIchorSystem.setIchorData(player, Config.maxIchor);
                }
                else
                {
                    WetIchorSystem.setWet(player,Config.maxWet);
                }
            }
        }
    }
}
