package dbrighthd.qahalloween2026.mixin;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundAddEntityPacket.class)
public abstract class ClientboundAddEntityPacketMixin {

    @Shadow
    @Final
    @Mutable
    private EntityType<?> type;

    @Inject(
            method = "<init>(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerEntity;I)V",
            at = @At("TAIL")
    )
    private void onSpawnPacket(
            Entity entity, ServerEntity serverEntity, int data, CallbackInfo ci
    ) {
        if (entity instanceof Vindicator) {
            this.type = EntityTypes.VILLAGER;
        }
        else if (entity instanceof Pillager) {
            this.type = EntityTypes.VILLAGER;
        }
        else if (entity instanceof Phantom) {
            this.type = EntityTypes.CHICKEN;
        }
        else if (entity instanceof Axolotl) {
            this.type = EntityTypes.SALMON;
        }
        else if (entity instanceof Ravager) {
            this.type = EntityTypes.SNIFFER;
        }
        else if (entity instanceof Breeze) {
            this.type = EntityTypes.STRIDER;
        }
        else if (entity instanceof WanderingTrader) {
            this.type = EntityTypes.VILLAGER;
        }
        else if (entity instanceof Rabbit && entity.level().dimension() == Level.NETHER) {
            this.type = EntityTypes.ALLAY;
        }
    }
}