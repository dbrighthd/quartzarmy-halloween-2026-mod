package dbrighthd.qahalloween2026.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import javax.xml.crypto.Data;
import java.util.List;

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

    public static ItemStack createNitWitBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        book.set(DataComponents.WRITTEN_BOOK_CONTENT,
                new WrittenBookContent(
                        Filterable.passThrough("Knowledge of this world"),
                        "that one guy",
                        0,
                        List.of(
                                Filterable.passThrough(
                                        Component.literal("I can tell you that everything you have left for your journey lies in this dimension.")),
                                Filterable.passThrough(
                                        Component.literal("For the pearls, do what the children yearn for.")),
                                Filterable.passThrough(
                                        Component.literal("For the blaze, look in my attic or something idk")),
                                Filterable.passThrough(
                                        Component.literal("After that, well, I would recommend collecting a lot of blocks for what is to come."))
                        ),
                        true
                ));
        book.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("minecraft","knowledge_book"));
        return book;
    }
}
