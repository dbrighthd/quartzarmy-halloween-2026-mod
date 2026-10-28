package dbrighthd.qahalloween2026.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.monster.Enderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enderman.class)
public class BeingStaredAtMixin
{
    @ModifyReturnValue(
            method = "isBeingStaredBy(Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("RETURN")
    )
    private static boolean changeScanForWater(boolean original)
    {
        return false;
    }

}

