package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.systems.JumpChainSystem;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.chainJumpCheck;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.grantAdvancement;

@Mixin(ServerPlayer.class)
public abstract class PlayerJumpMixin {
    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void jumpCount(CallbackInfo ci)
    {
        ServerPlayer player = (ServerPlayer)(Object)this;
        int amount = JumpChainSystem.addJump(player);
        if(amount < 3)
        {
            if(chainJumpCheck(player))
            {
                PlayerMethodUtils.playSoundAtEntityForPlayer(player, SoundEvents.BOAT_PADDLE_LAND,100,2,player);
                PlayerMethodUtils.particleAtEntity(player, ParticleTypes.POOF,0,0,0,5,0.05);
            }

        }
        else
        {
            grantAdvancement(player,"main/triplejump");
            player.setIgnoreFallDamageFromCurrentImpulse(true,player.position());
            PlayerMethodUtils.playSoundAtEntityForPlayer(player, SoundEvents.VAULT_OPEN_SHUTTER,100,2,player);
            PlayerMethodUtils.particleAtEntity(player, ParticleTypes.ELECTRIC_SPARK,0,0,0,20,0.5);
            PlayerMethodUtils.particleAtEntity(player, ParticleTypes.POOF,0,0,0,2,0.01);
        }

    }
}
