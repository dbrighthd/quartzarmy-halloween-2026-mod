package dbrighthd.qahalloween2026.utils;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

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
        int count = serverLevel.getEntities(newType, entity.getBoundingBox().inflate(16), selectEntity -> selectEntity != entity).size();

        replaceEntity(serverLevel, entity, newType, count < maxCount? countToSpawn : 0);
    }


    public static void changeAttribute(LivingEntity entity, Holder<Attribute> attribute, double value)
    {
        var attributeToGet = entity.getAttribute(attribute);
        if(attributeToGet != null)
        {
            attributeToGet.setBaseValue(value);
        }
    }
}
