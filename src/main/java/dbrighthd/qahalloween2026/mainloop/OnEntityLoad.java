package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.chicken.ChickenVariants;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariants;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.nautilus.Nautilus;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.BreezeWindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;
import java.util.UUID;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.*;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.grantAdvancement;

public class OnEntityLoad {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void onEntityLoad(Entity entity, ServerLevel level)
    {
        switch (entity) {
            case Phantom phantom -> onPhantomLoad(phantom);
            case Vindicator vindicator -> onVindicatorLoad(vindicator,level);
            case Pillager pillager -> onPillagerLoad(pillager);
            case IronGolem golem -> changeAttribute(golem,Attributes.ATTACK_DAMAGE,0);
            case Wolf wolf -> replaceEntity(level,wolf, EntityTypes.PANDA,1);
            case Witch witch -> replaceEntity(level,witch, EntityTypes.ZOMBIE,1);
            case Frog frog -> replaceEntity(level,frog, EntityTypes.CAT,1);
            case Panda panda -> onPandaLoad(panda);
            case Ravager ravager -> onRavagerLoad(ravager);
            case Breeze breeze -> onBreezeLoad(breeze);
            case PiglinBrute brute -> onBruteLoad(brute);
            case Silverfish silverfish -> onSilverfishLoad(silverfish);
            case WanderingTrader trader -> onWanderingTraderLoad(trader);
            case Axolotl axolotl -> axolotl.setSilent(true);
            case Sniffer sniffer -> onSnifferLoad(sniffer,level);
            case Villager villager -> replaceEntity(level, villager, EntityTypes.VINDICATOR, 1);
            case AbstractFish fih -> onFishLoad(fih, level);
            case Enderman enderman -> onEndermanLoad(enderman, level);
            case EndCrystal crystal -> crystal.discard();
            case Squid squid -> replaceEntityIfLessThanAround(level, squid, EntityTypes.AXOLOTL, 1, Config.maxAxolotlsNearby);
            case Nautilus nautilus -> replaceEntityIfLessThanAround(level, nautilus, EntityTypes.AXOLOTL, 1, Config.maxAxolotlsNearby);
            case Dolphin dolphin -> replaceEntityIfLessThanAround(level, dolphin, EntityTypes.AXOLOTL, 1, Config.maxAxolotlsNearby);
            case Cow cow -> replaceEntity(level, cow, EntityTypes.CHICKEN, 1);
            case Chicken chicken -> chicken.setVariant(level.registryAccess().get(ChickenVariants.TEMPERATE).get());
            case Cat cat -> onCatLoad(cat, level);
            case Rabbit rabbit -> onRabbitLoad(rabbit,level);
            case Pig pig -> replaceEntity(level, pig, EntityTypes.SHEEP, 1);
            case AbstractHorse horse -> replaceEntity(level, horse, EntityTypes.PHANTOM, 3);
            case Armadillo armadillo -> replaceEntity(level, armadillo, EntityTypes.CHICKEN, 1);
            case Spider spider -> replaceEntity(level, spider, EntityTypes.ZOMBIE, 1);
            case Zombie _ when !(entity.getClass() == Zombie.class) -> replaceEntity(level, entity, EntityTypes.ZOMBIE, 1);
            case AbstractSkeleton _ when !(entity instanceof Skeleton) -> replaceEntity(level, entity, EntityTypes.SKELETON, 1);
            case Skeleton skeleton -> onSkeletonLoad(skeleton);
            case Creeper creeper -> onCreeperLoad(creeper);
            case ItemEntity item -> onItemLoad(item,level);
            case Arrow arrow -> onArrowLoad(arrow);
            case BreezeWindCharge windCharge -> onWindChargeLoad(windCharge);
            default -> {
            }
        }

        if (entity instanceof Monster monster)
        {
            level.getServer().getScoreboard().addPlayerToTeam(monster.getScoreboardName(),GlobalVars.monsterTeam);
        }
    }

    private  static  void onVindicatorLoad(Vindicator vindicator, ServerLevel level)
    {
        level.getServer().getScoreboard().addPlayerToTeam(vindicator.getScoreboardName(),GlobalVars.monsterTeam);
        vindicator.setSilent(true);
        vindicator.setPersistenceRequired();

        if(vindicator.level().dimension() == Level.OVERWORLD)
        {
            vindicator.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.OBSIDIAN));
            vindicator.setDropChance(EquipmentSlot.MAINHAND,100);
            changeAttribute(vindicator,Attributes.MAX_HEALTH,15);
            changeAttribute(vindicator,Attributes.ATTACK_DAMAGE,3);
        }
        else
        {
            vindicator.setDropChance(EquipmentSlot.MAINHAND,0);
            changeAttribute(vindicator,Attributes.MAX_HEALTH,10);
            changeAttribute(vindicator,Attributes.ATTACK_DAMAGE,2);
        }
    }

    public static void onWanderingTraderLoad(WanderingTrader trader)
    {
        changeAttribute(trader,Attributes.MOVEMENT_SPEED,0);
        trader.setCustomName(Component.literal("This is a vintage comedy prop..."));
        MerchantOffers offers = trader.getOffers();
        offers.clear();
        offers.add(new MerchantOffer(
                new ItemCost(Items.LEAF_LITTER, 64),
                new ItemStack(Items.GOLD_INGOT,10),
                1000000,
                0,
                0
        ));
        offers.add(new MerchantOffer(
                new ItemCost(Items.LEAF_LITTER, 64),
                createNitWitBook(),
                1000000,
                0,
                0
        ));
        offers.add(new MerchantOffer(
                new ItemCost(Items.LEAF_LITTER, 1),
                createMisInfoBook(),
                1000000,
                0,
                0
        ));
        offers.add(new MerchantOffer(
                new ItemCost(Items.LEAF_LITTER, 5),
                new ItemStack(Items.BLAST_FURNACE),
                1000000,
                0,
                0
        ));
        offers.add(new MerchantOffer(
                new ItemCost(Items.LEAF_LITTER, 1),
                new ItemStack(Items.STRING),
                1000000,
                0,
                0
        ));
        offers.add(new MerchantOffer(
                new ItemCost(Items.LEAF_LITTER, 1),
                new ItemStack(Items.COAL),
                1000000,
                0,
                0
        ));
        offers.add(new MerchantOffer(
                new ItemCost(Items.LEAF_LITTER, 32),
                new ItemStack(Items.ARROW,64),
                1000000,
                0,
                0
        ));
        trader.setPermanentlyInvulnerable(true);
    }

    public static void onPhantomLoad(Phantom phantom)
    {
        phantom.setSilent(true);
        phantom.setPersistenceRequired();
        changeAttribute(phantom, Attributes.ATTACK_DAMAGE, 0.1);
        changeAttribute(phantom, Attributes.ATTACK_KNOCKBACK, 4);
        if(phantom.level().dimension() == Level.END)
        {
            changeAttribute(phantom, Attributes.MAX_HEALTH, 6);
        }
        else
        {
            changeAttribute(phantom, Attributes.MAX_HEALTH, 2);
        }
    }

    private  static  void onPillagerLoad(Pillager pillager)
    {
        if(pillager.level().dimension() == Level.OVERWORLD)
        {
            pillager.setSilent(true);
        }
        changeAttribute(pillager,Attributes.MAX_HEALTH,8);
        pillager.setPersistenceRequired();
    }


    //THEY AREN'T INVISIBLE!!!!!!!!!!!!!!!!!!!!
    private static void onSnifferLoad(Sniffer sniffer, ServerLevel level)
    {
        if (level.getFluidState(sniffer.blockPosition().below()).is(Fluids.WATER)) {
            sniffer.discard();
        } else {
            switch ((level.getRandom().nextInt()) % 6) {
                case 1 -> replaceEntity(level, sniffer, EntityTypes.RAVAGER, 1);
                case 2  -> replaceEntity(level, sniffer, EntityTypes.BREEZE, 1);
                case 3 -> replaceEntity(level, sniffer, EntityTypes.RABBIT, 1);
                default -> replaceEntity(level, sniffer, EntityTypes.SILVERFISH, 1);
            }
        }
    }

    private static void onPandaLoad(Panda panda)
    {
        panda.setMainGene(Panda.Gene.AGGRESSIVE);
        panda.setHiddenGene(Panda.Gene.AGGRESSIVE);
        changeAttribute(panda,Attributes.MOVEMENT_SPEED,0.2);
    }

    private static void onRavagerLoad(Ravager ravager)
    {
        ravager.setSilent(true);
        ravager.setPersistenceRequired();
        changeAttribute(ravager,Attributes.MAX_HEALTH,8);
    }

    private static void onSkeletonLoad(Skeleton skeleton)
    {

        if (skeleton.getMainHandItem().getItem() != Items.BOW) {
            skeleton.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW, 1));
        }
        if (skeleton.getItemBySlot(EquipmentSlot.HEAD).getItem() != Items.CARVED_PUMPKIN) {
            skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN));

        }
    }

    private static void onBreezeLoad(Breeze breeze)
    {
        breeze.setPersistenceRequired();
        changeAttribute(breeze,Attributes.MAX_HEALTH,8);
    }

    private static void onBruteLoad(PiglinBrute brute)
    {
        brute.setSilent(true);
        brute.setPersistenceRequired();
        changeAttribute(brute,Attributes.MAX_HEALTH,10);
        brute.setDropChance(EquipmentSlot.MAINHAND,0);
    }

    private static void onSilverfishLoad(Silverfish silverfish)
    {
        silverfish.setSilent(true);
        changeAttribute(silverfish,Attributes.MAX_HEALTH,101);
        changeAttribute(silverfish,Attributes.ATTACK_DAMAGE,0);
        if(silverfish.level().dimension() == Level.END)
        {
            changeAttribute(silverfish,Attributes.AIR_DRAG_MODIFIER,0.5);
        }
        silverfish.setHealth(100);
        silverfish.setPersistenceRequired();
    }

    private static void onFishLoad(AbstractFish fih, ServerLevel level)
    {
        if (!(level.dimension() == Level.NETHER)) {
            replaceEntityIfLessThanAround(level, fih, EntityTypes.AXOLOTL, 1, Config.maxAxolotlsNearby);
        }
    }

    private static void onEndermanLoad(Enderman enderman, ServerLevel level)
    {
        if (!(level.dimension() == Level.END)) {
            replaceEntity(level, enderman, EntityTypes.CREEPER, 1);
        }
    }

    private static void onCatLoad(Cat cat, ServerLevel level)
    {
        cat.setVariant(level.registryAccess().get(CatVariants.RED).get());
        cat.setPersistenceRequired();
        changeAttribute(cat,Attributes.MOVEMENT_SPEED,0);
        cat.setCustomName(Component.literal("Hi").withStyle(ChatFormatting.BOLD));
        if(!cat.isSilent())
        {
            spawnAtEntity(level,cat, EntityTypes.CHICKEN,1);
        }
        cat.setSilent(true);
        cat.setPermanentlyInvulnerable(true);
    }

    private static void onRabbitLoad(Rabbit rabbit, ServerLevel level)
    {
        if (level.dimension() == Level.OVERWORLD) {
            replaceEntity(level, rabbit, EntityTypes.PHANTOM, 2);
        }
        else {
            rabbit.setPermanentlyInvulnerable(true);
            rabbit.setCustomName(Component.literal("Blub"));
        }
    }

    private static  void onCreeperLoad(Creeper creeper)
    {
        creeper.explosionRadius = 0;
        creeper.maxSwell = 50;
        creeper.setSilent(false);
    }

    private static  void onItemLoad(ItemEntity item, ServerLevel level)
    {
        ItemStack stack = item.getItem();

        if (!stack.has(DataComponents.CUSTOM_MODEL_DATA) && !stack.is(Items.STRING) && !stack.is(Items.ARROW) && !stack.is(Items.OBSIDIAN)) {
            int modelData = level.getRandom().nextInt(Config.stackAmounts) + 1;

            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) modelData), List.of(), List.of(), List.of()));

            item.setItem(stack);
        }
    }

    private static void onArrowLoad(Arrow arrow)
    {
        Entity owner = arrow.getOwner();
        arrow.setCritArrow(false);
        if (owner instanceof Skeleton mob) {
            arrow.setDeltaMovement(arrow.getDeltaMovement().normalize().scale(Config.arrowSpeed));
            arrow.setNoGravity(true);
            Entity target = mob.getTarget();
            if (target != null) {
                UUID targetUUID = target.getUUID();
                arrow.setAttached(DataAttachments.TARGET_DATA, new TargetData(targetUUID, TargetData.SourceType.SKELETON));
            }
            arrow.setBaseDamage(0);
        } else {
            if (owner instanceof Player player && !arrow.hasAttached(DataAttachments.TARGET_DATA)) {
                arrow.setNoGravity(true);
                Vec3 correctedMotion = player.getHeadLookAngle().normalize().scale(Config.arrowSpeed);
                if(player.level().dimension() == Level.END && player.position().y < 30 && correctedMotion.y > 0)
                {
                    correctedMotion = new Vec3(correctedMotion.x,0.2,correctedMotion.z);
                }
                PlayerMethodUtils.addAndSendMovementIfPlayer(owner, correctedMotion.scale(-20), false);
                arrow.setDeltaMovement(player.getHeadLookAngle().normalize().scale(0.001));
                arrow.setAttached(DataAttachments.TARGET_DATA, new TargetData(new UUID(0, 1), TargetData.SourceType.PLAYER));
                arrow.setOwner(arrow);
                player.setIgnoreFallDamageFromCurrentImpulse(true,player.position());
                if(!(correctedMotion.y() > -0.01))
                {
                    grantAdvancement((ServerPlayer) player, "challenges/bow");
                }
                arrow.setBaseDamage(0);
            }
        }
    }

    private static void onWindChargeLoad(BreezeWindCharge windCharge)
    {
        windCharge.setDeltaMovement(windCharge.getDeltaMovement().normalize().scale(Config.arrowSpeed));

        Entity owner = windCharge.getOwner();
        if (owner instanceof Mob mob) {
            windCharge.setNoGravity(true);
            Entity target = mob.getTarget();
            if (target != null) {
                UUID targetUUID = target.getUUID();
                windCharge.setAttached(DataAttachments.TARGET_DATA, new TargetData(targetUUID, TargetData.SourceType.NORMALMOB));
            }
        } else {
            if (owner instanceof Player && !windCharge.hasAttached(DataAttachments.TARGET_DATA)) {
                PlayerMethodUtils.addAndSendMovementIfPlayer(owner, windCharge.getDeltaMovement().scale(-20), false);
                windCharge.setDeltaMovement(0, 0, 0);
                windCharge.setAttached(DataAttachments.TARGET_DATA, new TargetData(new UUID(0, 1), TargetData.SourceType.NORMALMOB));
                windCharge.setOwner(windCharge);
            }
        }
    }
}
