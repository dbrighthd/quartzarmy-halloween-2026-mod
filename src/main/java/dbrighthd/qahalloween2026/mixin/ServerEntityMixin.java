package dbrighthd.qahalloween2026.mixin;

import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.world.entity.animal.rabbit.Rabbit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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
        if (!(entity instanceof Axolotl || entity instanceof Rabbit))
        {
            return original;
        }
        return List.of(); //discard special information or else network protocol error. whatever
    }
}