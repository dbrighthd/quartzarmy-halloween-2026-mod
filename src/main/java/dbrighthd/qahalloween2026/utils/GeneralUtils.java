package dbrighthd.qahalloween2026.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.phys.Vec3;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

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

    public static void respawnEffects(ServerPlayer player)
    {
        PlayerMethodUtils.playSoundAtEntity(player, SoundEvents.WARDEN_ATTACK_IMPACT,100,1);
        PlayerMethodUtils.particleAtEntityOffset(player,new Vec3(0,1,0), ParticleTypes.TRIAL_SPAWNER_DETECTED_PLAYER_OMINOUS,0.25f,0.25f,0.25f,20,0);
    }

    public static ItemStack createNitWitBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        book.set(DataComponents.WRITTEN_BOOK_CONTENT,
                new WrittenBookContent(
                        Filterable.passThrough("Book of Information"),
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

    public static ItemStack createMisInfoBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

        book.set(DataComponents.WRITTEN_BOOK_CONTENT,
                new WrittenBookContent(
                        Filterable.passThrough("Book of Misinformation"),
                        "that two guy",
                        0,
                        List.of(
                                Filterable.passThrough(
                                        Component.literal("There are no ").append(Component.literal("orange").withStyle(ChatFormatting.DARK_GREEN)).append(Component.literal(" blocks"))),
                                Filterable.passThrough(
                                        Component.literal("Today is "+ LocalDate.now().minusMonths(6).getMonth().getDisplayName(TextStyle.FULL, Locale.CANADA) + " 27")),
                                Filterable.passThrough(
                                        Component.literal("QA stands for Quantum Mechanics")),
                                Filterable.passThrough(
                                        Component.literal("If you were to replace every block of Ichor in the sift with water, then it would become far more dangerous for you")),
                                Filterable.passThrough(
                                        Component.literal("If ice were solid, it would be considered edible.")),
                                Filterable.passThrough(
                                        Component.literal("Of all people on earth, there are only 12")),
                                Filterable.passThrough(
                                        Component.literal("Tuesday\nFirst of all, let me get something straight: This is a JOURNAL, not a diary. I Know what is says on the cover, but when Mom went out to buy this thing, I SPECIFICALLY told her to get one that didn't say \"diary\" on it. Great. All I need is for some jerk to catch me carrying this book around and get the wrong idea.")),
                                Filterable.passThrough(
                                        Component.literal("Purple is a spectral color")),
                                Filterable.passThrough(
                                        Component.literal("Hi")),
                                Filterable.passThrough(
                                        Component.literal("This is the last page")),
                                Filterable.passThrough(
                                        Component.literal("Pencils were invented in Pencilvannia")),
                                Filterable.passThrough(
                                        Component.literal("Answer me these questions three and yee shall be a bee: \n50+7= \n\nIf three bees believe in trees why is he the one who leaves?")),
                                Filterable.passThrough(
                                        Component.literal("This game is not modified")),
                                Filterable.passThrough(
                                        Component.literal("You are currently in the Sift")),
                                Filterable.passThrough(
                                        Component.literal("Good afternoon, Good evening, and Goodnight")),
                                Filterable.passThrough(
                                        Component.literal(""))
                        ),
                        true
                ));
        book.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("minecraft","knowledge_book"));
        return book;
    }
}
