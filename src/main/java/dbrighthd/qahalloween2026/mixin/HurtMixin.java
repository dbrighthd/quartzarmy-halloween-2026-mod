package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.events.EventsStorage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class HurtMixin {

    @Inject(
            method = "hurtServer",
            at = @At("HEAD")
    )
    private void onHurt(
            ServerLevel level,
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Boolean> cir
    ) {
        ServerPlayer player = (ServerPlayer)(Object) this;
        EventsStorage.hurtThisTick.put(player.getUUID(), (int)amount);
    }
}