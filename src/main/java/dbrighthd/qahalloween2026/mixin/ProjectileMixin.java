package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {


    @Inject(
            method = "onHit(Lnet/minecraft/world/phys/HitResult;)V",
            at = @At("HEAD")
    )
    private void onHit(
            HitResult hitResult, CallbackInfo ci
    ) {
        if ((Projectile) (Object) this instanceof Snowball snowball)
        {
            Component itemName = snowball.getItem().get(DataComponents.ITEM_NAME);
            if(itemName != null && itemName.getString().contains("Ender"))
            {
                if(snowball.level().getServer() != null && snowball.level().dimension() != Level.END)
                {
                    PlayerMethodUtils.playSoundAtEntity(snowball, SoundEvents.END_PORTAL_SPAWN,100,1);
                    BlockPos referencePos = snowball.blockPosition().subtract(new Vec3i(2,0,2));
                    Objects.requireNonNull(snowball.level().getServer()).getStructureTemplateManager().get(Identifier.fromNamespaceAndPath("dbright", "end_portal")).ifPresent(template -> template.placeInWorld((ServerLevel) snowball.level(), referencePos, referencePos, new StructurePlaceSettings(), snowball.level().getRandom(), Block.UPDATE_ALL));
                }

            }
        }
    }
}