package dbrighthd.qahalloween2026.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static dbrighthd.qahalloween2026.mainloop.Config.creeperStartDistance;
import static dbrighthd.qahalloween2026.mainloop.Config.creeperSuckForce;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.addAndSendMovementIfPlayer;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.playSoundAtEntity;

@Mixin(Creeper.class)
public abstract class CreeperSwellMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onCreeperTick(CallbackInfo ci) {
        Creeper creeper = (Creeper) (Object) this;

        if (creeper.getSwellDir() > 0) {
            if (creeper.level() instanceof ServerLevel serverLevel  && creeper.swell == 0)
            {
                creeper.setPermanentlyInvulnerable(true);
                creeper.setNoAi(true);
                serverLevel.sendParticles(
                        ParticleTypes.PORTAL,
                        creeper.getX(),
                        creeper.getY(),
                        creeper.getZ(),
                        1000,
                        0.0,
                        0.0,
                        0.0,
                        5.0
                );
            }
            if(creeper.swell == 18)
            {
                playSoundAtEntity(creeper, SoundEvents.WARDEN_SONIC_CHARGE,1,1);
                creeper.setSilent(true);
            }
            if(creeper.swell > 40)
            {
                Objects.requireNonNull(creeper.getAttribute(Attributes.SCALE)).setBaseValue(1d-(creeper.swell - 40d)/20d);
            }
            double radius = creeperStartDistance;
            List<Entity> nearby = creeper.level().getEntities(creeper, creeper.getBoundingBox().inflate(radius), entity -> entity.distanceToSqr(creeper) <= radius * radius);
            for(Entity entity : nearby)
            {
                Vec3 directionToCreeper = creeper.position().subtract(entity.position()).normalize();
                addAndSendMovementIfPlayer(entity, directionToCreeper.scale(creeperSuckForce), false);
            }
        }
    }
}