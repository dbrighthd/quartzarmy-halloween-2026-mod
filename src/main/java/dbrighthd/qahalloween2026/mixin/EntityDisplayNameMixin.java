package dbrighthd.qahalloween2026.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.illager.Vindicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityDisplayNameMixin {

    @Inject(
            method = "getDisplayName",
            at = @At("HEAD"),
            cancellable = true
    )
    private void replaceAxolotlDisplayName(
            CallbackInfoReturnable<Component> cir
    ) {
        Entity entity = (Entity) (Object)this;
        if (entity instanceof Axolotl) {
            cir.setReturnValue(
                    EntityTypes.SALMON.getDescription()
            );
        }
        if (entity instanceof Phantom) {
            cir.setReturnValue(
                    EntityTypes.CHICKEN.getDescription()
            );
        }
        if (entity instanceof Vindicator) {
            cir.setReturnValue(
                    EntityTypes.VILLAGER.getDescription()
            );
        }
    }
}
