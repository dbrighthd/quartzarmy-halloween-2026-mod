package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.nautilus.Nautilus;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.squid.Squid;
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
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.UUID;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.replaceEntity;

public class OnEntityLoad {
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if(entity instanceof Phantom phantom)
            {
                phantom.setSilent(true);
                phantom.setPersistenceRequired();
                var damageAttribute = phantom.getAttribute(Attributes.ATTACK_DAMAGE);
                if(damageAttribute != null)
                {
                    damageAttribute.setBaseValue(0.1);
                }

                var knockbackAttribute = phantom.getAttribute(Attributes.ATTACK_KNOCKBACK);
                if(knockbackAttribute != null)
                {
                    knockbackAttribute.setBaseValue(1);
                }
            }
            if(entity instanceof Vindicator vindicator)
            {
                vindicator.setSilent(true);
                vindicator.setPersistenceRequired();
            }
            if(entity instanceof Ravager ravager)
            {
                ravager.setSilent(true);
                ravager.setPersistenceRequired();
                var damageAttribute = ravager.getAttribute(Attributes.ATTACK_DAMAGE);
                if(damageAttribute != null)
                {
                    damageAttribute.setBaseValue(1);
                }
            }
            if(entity instanceof Silverfish silverfish)
            {
                silverfish.setSilent(true);
                silverfish.setPersistenceRequired();
            }
            if(entity instanceof Sniffer sniffer)
            {
                if(level.getFluidState(sniffer.blockPosition().below()).is(Fluids.WATER))
                {
                    sniffer.discard();
                }
                else {
                    switch ((level.getRandom().nextInt()) % 3) {
                        case 1 -> replaceEntity(level, sniffer, EntityTypes.RAVAGER, 1);
                        case 0 -> replaceEntity(level, sniffer, EntityTypes.SILVERFISH, 1);
                        default -> replaceEntity(level, sniffer, EntityTypes.BREEZE, 1);
                    }
                }
            }
            if(entity instanceof Villager villager)
            {
                replaceEntity(level,villager, EntityTypes.VINDICATOR,1);
            }
            if(entity instanceof AbstractFish fih)
            {
                if(!(level.dimension() == Level.NETHER))
                {
                    replaceEntity(level,fih, EntityTypes.AXOLOTL,1);
                }
            }
            if(entity instanceof Enderman enderman)
            {
                if(!(level.dimension() == Level.END))
                {
                    replaceEntity(level,enderman, EntityTypes.CREEPER,1);
                }
            }
            if(entity instanceof Squid squid)
            {
                replaceEntity(level,squid, EntityTypes.AXOLOTL,1);
            }
            if(entity instanceof Nautilus nautilus)
            {
                replaceEntity(level,nautilus, EntityTypes.AXOLOTL,1);
            }
            if(entity instanceof Dolphin dolphin)
            {
                replaceEntity(level,dolphin, EntityTypes.AXOLOTL,1);
            }
            if(entity instanceof Cow cow)
            {
                replaceEntity(level,cow, EntityTypes.CHICKEN,1);
            }
            if(entity instanceof Rabbit rabbit)
            {
                replaceEntity(level,rabbit, EntityTypes.PHANTOM,2);
            }
            if(entity instanceof Pig pig)
            {
                replaceEntity(level,pig, EntityTypes.SHEEP,1);
            }
            if(entity instanceof AbstractHorse horse)
            {
                replaceEntity(level,horse, EntityTypes.PHANTOM,3);
            }
            if(entity instanceof Armadillo armadillo)
            {
                replaceEntity(level,armadillo, EntityTypes.CHICKEN,1);
            }
            if(entity instanceof  Spider spider)
            {
                replaceEntity(level,spider, EntityTypes.ZOMBIE,1);
            }
            if(entity instanceof Zombie && !(entity.getClass() == Zombie.class))
            {
                replaceEntity(level,entity, EntityTypes.ZOMBIE,1);
            }
            if(entity instanceof AbstractSkeleton && !(entity instanceof Skeleton))
            {
                replaceEntity(level,entity, EntityTypes.SKELETON,1);
            }
            if (entity instanceof Monster monster)
            {
                level.getServer().getScoreboard().addPlayerToTeam(monster.getScoreboardName(),GlobalVars.monsterTeam);
            }
            if (entity instanceof Creeper creeper) {
                creeper.explosionRadius = 0;
                creeper.maxSwell = 50;
                creeper.setSilent(false);
            }
            if (entity instanceof ItemEntity item) {
                ItemStack stack = item.getItem();

                if (!stack.has(DataComponents.CUSTOM_MODEL_DATA) && !stack.is(Items.STRING)) {
                    int modelData = level.getRandom().nextInt(Config.stackAmounts) + 1;

                    stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) modelData), List.of(), List.of(), List.of()));

                    item.setItem(stack);
                }
            }
            if (entity instanceof Arrow arrow) {
                arrow.setDeltaMovement(arrow.getDeltaMovement().normalize().scale(Config.arrowSpeed));

                Entity owner = arrow.getOwner();
                if (owner instanceof Mob mob) {
                    arrow.setNoGravity(true);
                    Entity target = mob.getTarget();
                    if (target != null) {
                        UUID targetUUID = target.getUUID();
                        arrow.setAttached(DataAttachments.TARGET_DATA, new TargetData(targetUUID, false));
                    }
                }
                else
                {
                    if(owner instanceof Player && !arrow.hasAttached(DataAttachments.TARGET_DATA))
                    {
                        PlayerMethodUtils.addAndSendMovementIfPlayer(owner,arrow.getDeltaMovement().scale(-20),false);
                        arrow.setDeltaMovement(0,0,0);
                        arrow.setAttached(DataAttachments.TARGET_DATA, new TargetData(new UUID(0,1), true));
                        arrow.setOwner(arrow);
                    }
                }
                arrow.setBaseDamage(0);
            }
            if (entity instanceof BreezeWindCharge windCharge) {
                windCharge.setDeltaMovement(windCharge.getDeltaMovement().normalize().scale(Config.arrowSpeed));

                Entity owner = windCharge.getOwner();
                if (owner instanceof Mob mob) {
                    windCharge.setNoGravity(true);
                    Entity target = mob.getTarget();
                    if (target != null) {
                        UUID targetUUID = target.getUUID();
                        windCharge.setAttached(DataAttachments.TARGET_DATA, new TargetData(targetUUID, false));
                    }
                }
                else
                {
                    if(owner instanceof Player && !windCharge.hasAttached(DataAttachments.TARGET_DATA))
                    {
                        PlayerMethodUtils.addAndSendMovementIfPlayer(owner,windCharge.getDeltaMovement().scale(-20),false);
                        windCharge.setDeltaMovement(0,0,0);
                        windCharge.setAttached(DataAttachments.TARGET_DATA, new TargetData(new UUID(0,1), true));
                        windCharge.setOwner(windCharge);
                    }
                }
            }
            if (entity instanceof Skeleton skeleton) {
                skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN));
            }
        });
    }
}
