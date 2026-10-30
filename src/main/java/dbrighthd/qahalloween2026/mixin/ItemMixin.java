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

import java.util.Objects;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.grantAdvancement;

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
                grantAdvancement(player,"main/drinklava");
            }
            if(Identifier.parse("minecraft:water_bucket").equals(itemStack.get(DataComponents.ITEM_MODEL)) || Identifier.parse("minecraft:potion").equals(itemStack.get(DataComponents.ITEM_MODEL)))
            {
                if(Objects.requireNonNull(itemStack.get(DataComponents.ITEM_NAME)).toString().contains("Ichor"))
                {
                    WetIchorSystem.setIchorData(player, Config.maxIchor);
                    grantAdvancement(player,"main/drinkichor");
                }
                else
                {
                    WetIchorSystem.setWet(player,Config.maxWet);
                    grantAdvancement(player,"main/drinkwater");
                }
            }
        }
    }
}
