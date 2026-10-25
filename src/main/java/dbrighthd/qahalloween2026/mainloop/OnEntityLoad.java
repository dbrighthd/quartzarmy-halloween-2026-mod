package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.mixin.PlayerUseItemOnMixin;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Parched;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;

import java.util.List;
import java.util.UUID;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.replaceEntity;

public class OnEntityLoad {
    public static void init() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if(entity instanceof Phantom phantom)
            {
                phantom.setSilent(true);
            }
            if(entity instanceof  Spider spider)
            {
                replaceEntity(level,spider, EntityTypes.ZOMBIE);
            }
            if(entity instanceof Zombie && !(entity.getClass() == Zombie.class))
            {
                replaceEntity(level,entity, EntityTypes.ZOMBIE);
            }
            if(entity instanceof AbstractSkeleton && !(entity instanceof Skeleton))
            {
                replaceEntity(level,entity, EntityTypes.SKELETON);
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
            if (entity instanceof Skeleton skeleton) {
                skeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CARVED_PUMPKIN));
            }
        });
    }
}
