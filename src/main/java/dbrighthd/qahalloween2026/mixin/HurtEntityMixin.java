package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.events.EventsStorage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HurtEntityMixin {

    @Inject(
            method = "hurtServer",
            at = @At("HEAD")
    )
    private void onDamaged(
            ServerLevel level,
            DamageSource source,
            float damage,
            CallbackInfoReturnable<Boolean> cir
    ) {
        LivingEntity target = (LivingEntity) (Object) this;
        Entity attacker = source.getEntity();
        if(attacker instanceof Player player && !target.isInvulnerable())
        {
            EventsStorage.attackedThisTick.put(player.getUUID(), new EventsStorage.AttackInfo(target, (int)damage));
        }
    }
}