package dbrighthd.qahalloween2026.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;

public class GeneralUtils {
    public static void replaceEntity(ServerLevel level, Entity entity, EntityType newType, int count)
    {
        for(int i = 0; i < count; i++)
        {
            Entity entityToSpawn = newType.create(
                    level,
                    EntitySpawnReason.NATURAL
            );
            if(entityToSpawn != null)
            {
                entityToSpawn.setPos(entity.position());
                entityToSpawn.moveOrInterpolateTo(entity.position(),entity.getYRot(),entity.getXRot());
                entityToSpawn.setYHeadRot(entity.getYHeadRot());
                level.addFreshEntity(entityToSpawn);

            }
        }
        entity.discard();
    }
}
