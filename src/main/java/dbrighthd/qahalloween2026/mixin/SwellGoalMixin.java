package dbrighthd.qahalloween2026.mixin;

import net.minecraft.world.entity.ai.goal.SwellGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static dbrighthd.qahalloween2026.mainloop.Config.creeperCancelDistance;
import static dbrighthd.qahalloween2026.mainloop.Config.creeperStartDistance;

@Mixin(SwellGoal.class)
public abstract class SwellGoalMixin {

    @ModifyConstant(
            method = "canUse",
            constant = @Constant(doubleValue = 9.0)
    )
    private double changeStartDistance(double original) {
        return creeperStartDistance * creeperStartDistance;
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(doubleValue = 49.0)
    )
    private double changeStopDistance(double original) {
        return creeperCancelDistance * creeperCancelDistance;
    }
}