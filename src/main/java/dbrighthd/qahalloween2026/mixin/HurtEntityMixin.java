package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.events.EventsStorage;
import dbrighthd.qahalloween2026.mainloop.PlayerSpecificStuff;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Mannequin;
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
    private void onDamaged(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity target = (LivingEntity) (Object) this;
        Entity attacker = source.getEntity();
        if(attacker != target && attacker instanceof Player player && !target.isInvulnerable() && !(target instanceof Mannequin))
        {
            EventsStorage.attackedThisTick.put(player.getUUID(), new EventsStorage.AttackInfo(target, (int)damage));
        }
        else if(attacker instanceof Player player && target instanceof  Mannequin mannequin)
        {
            if(mannequin.entityTags().contains("cherryClone"))
            {
                PlayerSpecificStuff.qeuedCherryDamage = damage;
                PlayerSpecificStuff.queuedSource = source;
            }
        }
        if(attacker instanceof ServerPlayer player1 && target instanceof Player target2)
        {
            if(player1.entityTags().contains(PlayerSpecificStuff.MeanTag.Byte.name()))
            {
                target2.addEffect( new MobEffectInstance(MobEffects.NAUSEA, 140, 1,false,true));
                target2.addEffect( new MobEffectInstance(MobEffects.SLOWNESS, 140, 1,false,true));
                level.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal(target2.getScoreboardName() + " was bitten by " + player1.getScoreboardName()),
                        false
                );
            }
        }
    }
}