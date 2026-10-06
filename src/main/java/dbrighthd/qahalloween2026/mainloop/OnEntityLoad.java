package dbrighthd.qahalloween2026.mainloop;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

import java.util.List;

public class OnEntityLoad {
    public static void init()
    {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Creeper creeper) {
                creeper.explosionRadius = 0;
                creeper.maxSwell = 50;
                creeper.setSilent(false);
            }
            if (entity instanceof ItemEntity item)
            {
                ItemStack stack = item.getItem();

                if (!stack.has(DataComponents.CUSTOM_MODEL_DATA))
                {
                    int modelData = level.getRandom().nextInt(Config.stackAmounts) + 1;

                    stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) modelData), List.of(), List.of(), List.of()));

                    item.setItem(stack);
                }
            }
        });
    }
}
