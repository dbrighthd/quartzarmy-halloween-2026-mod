package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.nautilus.Nautilus;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.BreezeWindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.level.levelgen.feature.EndSpikeFeature;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.UUID;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.*;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.grantAdvancement;

public class OnEntityLoad {
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            switch (entity) {
                case Phantom phantom -> {
                    phantom.setSilent(true);
                    phantom.setPersistenceRequired();
                    changeAttribute(phantom, Attributes.ATTACK_DAMAGE, 0.1);
                    changeAttribute(phantom, Attributes.ATTACK_KNOCKBACK, 4);
                    changeAttribute(phantom, Attributes.MAX_HEALTH, 4);
                }
                case Vindicator vindicator -> {
                    vindicator.setSilent(true);
                    vindicator.setPersistenceRequired();
                }
                case Wolf wolf -> {
                    replaceEntity(level,wolf, EntityTypes.PANDA,1);
                }
                case Panda panda -> {
                    panda.setMainGene(Panda.Gene.AGGRESSIVE);
                    panda.setHiddenGene(Panda.Gene.AGGRESSIVE);
                    changeAttribute(panda,Attributes.MOVEMENT_SPEED,0.2);
                }
                case Ravager ravager -> {
                    ravager.setSilent(true);
                    ravager.setPersistenceRequired();
                    changeAttribute(ravager, Attributes.ATTACK_DAMAGE, 1);
                }
                case Silverfish silverfish -> {
                    silverfish.setSilent(true);
                    silverfish.setPersistenceRequired();
                }
                case Axolotl axolotl -> axolotl.setSilent(true);
                case Sniffer sniffer -> {
                    if (level.getFluidState(sniffer.blockPosition().below()).is(Fluids.WATER)) {
                        sniffer.discard();
                    } else {
                        switch ((level.getRandom().nextInt()) % 3) {
                            case 1 -> replaceEntity(level, sniffer, EntityTypes.RAVAGER, 1);
                            case 0 -> replaceEntity(level, sniffer, EntityTypes.SILVERFISH, 1);
                            default -> replaceEntity(level, sniffer, EntityTypes.BREEZE, 1);
                        }
                    }
                }
                case Villager villager -> replaceEntity(level, villager, EntityTypes.VINDICATOR, 1);
                case AbstractFish fih -> {
                    if (!(level.dimension() == Level.NETHER)) {
                        replaceEntityIfLessThanAround(level, fih, EntityTypes.AXOLOTL, 1, Config.maxAxolotlsNearby);
                    }
                }
                case Enderman enderman -> {
                    if (!(level.dimension() == Level.END)) {
                        replaceEntity(level, enderman, EntityTypes.CREEPER, 1);
                    }
                }
                case EndCrystal crystal -> crystal.discard();
                case Squid squid ->
                        replaceEntityIfLessThanAround(level, squid, EntityTypes.AXOLOTL, 1, Config.maxAxolotlsNearby);
                case Nautilus nautilus ->
                        replaceEntityIfLessThanAround(level, nautilus, EntityTypes.AXOLOTL, 1, Config.maxAxolotlsNearby);
                case Dolphin dolphin ->
                        replaceEntityIfLessThanAround(level, dolphin, EntityTypes.AXOLOTL, 1, Config.maxAxolotlsNearby);
                case Cow cow -> replaceEntity(level, cow, EntityTypes.CHICKEN, 1);
                case Rabbit rabbit -> {
                    if (level.dimension() == Level.OVERWORLD) {
                        replaceEntity(level, rabbit, EntityTypes.PHANTOM, 2);
                    }
                }
                case Pig pig -> replaceEntity(level, pig, EntityTypes.SHEEP, 1);
                case AbstractHorse horse -> replaceEntity(level, horse, EntityTypes.PHANTOM, 3);
                case Armadillo armadillo -> replaceEntity(level, armadillo, EntityTypes.CHICKEN, 1);
                case Spider spider -> replaceEntity(level, spider, EntityTypes.ZOMBIE, 1);
                case Zombie _ when !(entity.getClass() == Zombie.class) ->
                        replaceEntity(level, entity, EntityTypes.ZOMBIE, 1);
                case AbstractSkeleton _ when !(entity instanceof Skeleton) ->
                        replaceEntity(level, entity, EntityTypes.SKELETON, 1);
                case Skeleton skeleton -> {

                    if (skeleton.getMainHandItem().getItem() != Items.BOW) {
                        skeleton.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW, 1));
                    }
                    if (skeleton.getItemBySlot(EquipmentSlot.HEAD).getItem() != Items.CARVED_PUMPKIN) {
                        skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN));

                    }
                }
                case Creeper creeper -> {
                    creeper.explosionRadius = 0;
                    creeper.maxSwell = 50;
                    creeper.setSilent(false);
                }
                case ItemEntity item -> {
                    ItemStack stack = item.getItem();

                    if (!stack.has(DataComponents.CUSTOM_MODEL_DATA) && !stack.is(Items.STRING)) {
                        int modelData = level.getRandom().nextInt(Config.stackAmounts) + 1;

                        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) modelData), List.of(), List.of(), List.of()));

                        item.setItem(stack);
                    }
                }
                case Arrow arrow -> {
                    arrow.setDeltaMovement(arrow.getDeltaMovement().normalize().scale(Config.arrowSpeed));

                    Entity owner = arrow.getOwner();
                    if (owner instanceof Mob mob) {
                        arrow.setNoGravity(true);
                        Entity target = mob.getTarget();
                        if (target != null) {
                            UUID targetUUID = target.getUUID();
                            arrow.setAttached(DataAttachments.TARGET_DATA, new TargetData(targetUUID, false));
                        }
                    } else {
                        if (owner instanceof Player player && !arrow.hasAttached(DataAttachments.TARGET_DATA)) {
                            PlayerMethodUtils.addAndSendMovementIfPlayer(owner, arrow.getDeltaMovement().scale(-20), false);
                            arrow.setDeltaMovement(0, 0, 0);
                            arrow.setAttached(DataAttachments.TARGET_DATA, new TargetData(new UUID(0, 1), true));
                            arrow.setOwner(arrow);
                            grantAdvancement((ServerPlayer) player, "main/bow");
                        }
                    }
                    arrow.setBaseDamage(0);
                }
                case BreezeWindCharge windCharge -> {
                    windCharge.setDeltaMovement(windCharge.getDeltaMovement().normalize().scale(Config.arrowSpeed));

                    Entity owner = windCharge.getOwner();
                    if (owner instanceof Mob mob) {
                        windCharge.setNoGravity(true);
                        Entity target = mob.getTarget();
                        if (target != null) {
                            UUID targetUUID = target.getUUID();
                            windCharge.setAttached(DataAttachments.TARGET_DATA, new TargetData(targetUUID, false));
                        }
                    } else {
                        if (owner instanceof Player && !windCharge.hasAttached(DataAttachments.TARGET_DATA)) {
                            PlayerMethodUtils.addAndSendMovementIfPlayer(owner, windCharge.getDeltaMovement().scale(-20), false);
                            windCharge.setDeltaMovement(0, 0, 0);
                            windCharge.setAttached(DataAttachments.TARGET_DATA, new TargetData(new UUID(0, 1), true));
                            windCharge.setOwner(windCharge);
                        }
                    }
                }
                default -> {
                }
            }

            if (entity instanceof Monster monster)
            {
                level.getServer().getScoreboard().addPlayerToTeam(monster.getScoreboardName(),GlobalVars.monsterTeam);
            }
        });
    }
}
