package dbrighthd.qahalloween2026.mixin;

import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

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
}