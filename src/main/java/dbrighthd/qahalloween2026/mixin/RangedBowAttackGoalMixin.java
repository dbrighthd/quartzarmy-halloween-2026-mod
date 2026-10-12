package dbrighthd.qahalloween2026.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(RangedBowAttackGoal.class)
public class RangedBowAttackGoalMixin
{
    @ModifyConstant(
            method = "tick",
            constant = @Constant(
                    intValue = 20,
                    ordinal = 2
            )
    )
    private int qahalloween2026$changeBowPullTime(int original)
    {
        return Config.arrowInterval;
    }
}