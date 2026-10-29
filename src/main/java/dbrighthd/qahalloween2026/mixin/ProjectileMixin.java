package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.events.EventsStorage;
import dbrighthd.qahalloween2026.utils.GeneralUtils;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Projectile.class)
public abstract class ProjectileMixin {


    @Inject(
            method = "Lnet/minecraft/world/entity/projectile/Projectile;onHit(Lnet/minecraft/world/phys/HitResult;)V",
            at = @At("HEAD")
    )
    private void onHit(
            HitResult hitResult, CallbackInfo ci
    ) {
        if ((Projectile) (Object) this instanceof Snowball snowball)
        {
            if(snowball.getItem().get(DataComponents.ITEM_NAME).getString().contains("Ender"))
            {
                if(snowball.level().getServer() != null && snowball.level().dimension() != Level.END)
                {
                    PlayerMethodUtils.playSoundAtEntity(snowball, SoundEvents.END_PORTAL_SPAWN,100,1);
                    BlockPos referencePos = snowball.blockPosition().subtract(new Vec3i(2,0,2));
                    StructureTemplate template = Objects.requireNonNull(snowball.level().getServer()).getStructureTemplateManager().get(Identifier.fromNamespaceAndPath("dbright","end_portal")).orElse(null);
                    template.placeInWorld((ServerLevel)snowball.level(),referencePos,referencePos,new StructurePlaceSettings(), snowball.level().getRandom(), Block.UPDATE_ALL);
                }

            }
        }
    }
}