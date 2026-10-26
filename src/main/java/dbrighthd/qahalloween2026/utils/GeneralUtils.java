package dbrighthd.qahalloween2026.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

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

    public static void replaceEntityIfLessThanAround(ServerLevel serverLevel, Entity entity, EntityType newType, int countToSpawn, int maxCount)
    {
        int count = serverLevel.getEntities(
                newType,
                entity.getBoundingBox().inflate(16),
                selectEntity -> selectEntity != entity).size();

        replaceEntity(serverLevel, entity, newType, count < maxCount? countToSpawn : 0);
    }
}
