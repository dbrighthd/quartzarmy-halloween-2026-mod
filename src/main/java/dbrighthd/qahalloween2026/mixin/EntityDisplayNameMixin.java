package dbrighthd.qahalloween2026.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.level.Level;
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
        if (entity instanceof Vindicator && entity.level().dimension() != Level.NETHER) {
            cir.setReturnValue(
                    EntityTypes.VILLAGER.getDescription()
            );
        }
        if (entity instanceof Pillager && entity.level().dimension() != Level.NETHER) {
            cir.setReturnValue(
                    EntityTypes.VILLAGER.getDescription()
            );
        }
        if (entity instanceof Breeze) {
            cir.setReturnValue(Component.literal("Sentinel"));
        }
        if (entity instanceof Silverfish) {
            cir.setReturnValue(Component.literal("Fusefly"));
        }
        if (entity instanceof Enderman) {
            cir.setReturnValue(Component.literal("Soul Coderotted Enderman"));
        }
        if (entity instanceof Ravager) {
            cir.setReturnValue(Component.literal("Dartback"));
        }
    }
}
