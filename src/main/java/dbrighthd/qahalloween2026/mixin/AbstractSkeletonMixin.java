package dbrighthd.qahalloween2026.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeleton.class)
public class AbstractSkeletonMixin
{
    @ModifyConstant(
            method = "performRangedAttack",
            constant = @Constant(doubleValue = 0.20000000298023224) //wow this sucks
    )
    private double removeArrowArc(double constant)
    {
        return 0.1F;
    }

    @ModifyReturnValue(
            method = "getHardAttackInterval()I",
            at = @At("RETURN")
    )
    private int changeCooldownTime(int original)
    {
        return 0;
    }

    @ModifyReturnValue(
            method = "getAttackInterval()I",
            at = @At("RETURN")
    )
    private int changeNonHardInterval(int original)
    {
        return 0;
    }

    @ModifyReturnValue(
            method = "isShaking()Z",
            at = @At("RETURN")
    )
    private boolean isShakingOverride(boolean original)
    {
        return true;
    }

    @Shadow
    @Final
    private RangedBowAttackGoal<AbstractSkeleton> bowGoal;

    @Inject(
            method = "reassessWeaponGoal",
            at = @At("TAIL")
    )
    private void setFastAttackInterval(CallbackInfo ci)
    {
        bowGoal.setMinAttackInterval(0);
    }
}