package dbrighthd.qahalloween2026.mainloop;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.world.entity.monster.Creeper;

public class OnEntityLoad {
    public static void init()
    {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Creeper creeper) {
                creeper.explosionRadius = 0;
                creeper.maxSwell = 50;
                creeper.setSilent(false);
            }
        });
    }
}
