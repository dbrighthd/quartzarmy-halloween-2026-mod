package dbrighthd.qahalloween2026.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin {

    @Inject(method = "getCategory", at = @At("HEAD"), cancellable = true)
    private void changeCategory(CallbackInfoReturnable<MobCategory> cir) {
        if ((Object) this == EntityTypes.AXOLOTL) {
            cir.setReturnValue(MobCategory.WATER_AMBIENT);
        }
    }
}
