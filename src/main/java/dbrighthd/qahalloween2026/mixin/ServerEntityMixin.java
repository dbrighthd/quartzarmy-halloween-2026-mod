package dbrighthd.qahalloween2026.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerEntity.class)
public abstract class ServerEntityMixin {

    @Shadow @Final
    private Entity entity;

    @ModifyArg(
            method = {"sendPairingData", "sendDirtyEntityData"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSetEntityDataPacket;<init>(ILjava/util/List;)V"),
            index = 1
    )
    private List<SynchedEntityData.DataValue<?>> filterMetadata(List<SynchedEntityData.DataValue<?>> original)
    {

        if (entity instanceof WanderingTrader) {

            VillagerData nitwit = new VillagerData(BuiltInRegistries.VILLAGER_TYPE.getOrThrow(VillagerType.PLAINS), BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NITWIT), 1); //I SPECIFICALLY need it to be a nitwit for the joke but also i want it to be tradable so im just maknig it a wandering trader
            List<SynchedEntityData.DataValue<?>> result = new ArrayList<>(original);
            result.removeIf(data -> data.id() >= Villager.DATA_VILLAGER_DATA.id());
            result.add(new SynchedEntityData.DataValue<>(Villager.DATA_VILLAGER_DATA.id(), EntityDataSerializers.VILLAGER_DATA, nitwit));
            return result;
        }
        if (entity instanceof Silverfish silverfish) {
            List<SynchedEntityData.DataValue<?>> result = new ArrayList<>(original);
            result.removeIf(data -> data.id() == Pufferfish.PUFF_STATE.id());
            result.add(new SynchedEntityData.DataValue<>(Pufferfish.PUFF_STATE.id(), EntityDataSerializers.INT, silverfish.getHealth() < 100 ? 2 : 1));
            return result;
        }
        if (!(entity instanceof Axolotl || entity instanceof Rabbit))
        {
            return original;
        }
        return List.of(); //discard special information or else network protocol error. whatever
    }
}