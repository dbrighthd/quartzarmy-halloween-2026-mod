package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.events.EventsStorage;
import dbrighthd.qahalloween2026.mainloop.PlayerSpecificStuff;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class HurtMixin {

    @Inject(
            method = "hurtServer",
            at = @At("HEAD")
    )
    private void onHurt(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = (ServerPlayer)(Object) this;
        Entity attacker = source.getEntity();
        if(player.entityTags().contains(PlayerSpecificStuff.MeanTag.CherryBee.name()) && player != attacker)
        {
            EventsStorage.cherryHurtThisTick.put(player.getUUID(), (int)amount);

        }
        EventsStorage.hurtThisTick.put(player.getUUID(), (int)amount);

    }
}