package dbrighthd.qahalloween2026.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.xml.crypto.Data;
import java.util.List;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

    @ModifyExpressionValue(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack replaceLavaBucket(ItemStack original, Level level) {
        if (original.is(Items.LAVA_BUCKET)) {
            ItemStack item = new ItemStack(Items.RABBIT_FOOT);
            item.set(DataComponents.ITEM_NAME, Component.translatable("item.minecraft.lava_bucket"));
            item.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("minecraft","lava_bucket"));
            item.set(DataComponents.FOOD, new FoodProperties(0,0,true));
            item.set(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(Items.BUCKET)));
            item.set(DataComponents.CONSUMABLE, new Consumable(0.1F,ItemUseAnimation.DRINK, SoundEvents.GENERIC_DRINK,true, List.of()));
            item.set(DataComponents.MAX_STACK_SIZE, 1);
            return item;
        }
        if (original.is(Items.WATER_BUCKET)) {
            ItemStack item = new ItemStack(Items.RABBIT_FOOT);
            if(level.dimension() == Level.NETHER)
            {
                item.set(DataComponents.ITEM_NAME, Component.literal("Ichor Bucket"));
                item.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

            }
            else
            {
                item.set(DataComponents.ITEM_NAME, Component.translatable("item.minecraft.water_bucket"));

            }
            item.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("minecraft","water_bucket"));
            item.set(DataComponents.FOOD, new FoodProperties(0,0,true));
            item.set(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(Items.BUCKET)));
            item.set(DataComponents.CONSUMABLE, new Consumable(0.1F,ItemUseAnimation.DRINK, SoundEvents.GENERIC_DRINK,true, List.of()));
            item.set(DataComponents.MAX_STACK_SIZE, 1);
            return item;
        }

        return original;
    }
}