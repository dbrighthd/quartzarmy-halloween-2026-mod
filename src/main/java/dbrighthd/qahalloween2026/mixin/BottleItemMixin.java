package dbrighthd.qahalloween2026.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(BottleItem.class)
public abstract class BottleItemMixin {

    @ModifyArgs(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BottleItem;turnBottleIntoItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private void replaceBottleResult(
            Args args,
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack original = args.get(2);

        if (!original.is(Items.POTION)) {
            return;
        }

        ItemStack item = new ItemStack(Items.RABBIT_FOOT);

        if (level.dimension() == Level.NETHER) {
            item.set(DataComponents.ITEM_NAME,
                    Component.literal("Ichor Bottle"));
            item.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        } else {
            item.set(DataComponents.ITEM_NAME,
                    Component.literal("Water Bottle"));
        }

        item.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("minecraft", "potion"));

        item.set(DataComponents.FOOD, new FoodProperties(0, 0, true));
        item.set(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStackTemplate(Items.GLASS_BOTTLE)));
        item.set(DataComponents.CONSUMABLE, new Consumable(1F, ItemUseAnimation.DRINK, SoundEvents.GENERIC_DRINK, true, List.of()));
        item.set(DataComponents.MAX_STACK_SIZE, 1);
        args.set(2, item);
    }
    }