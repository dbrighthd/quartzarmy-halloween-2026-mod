package dbrighthd.qahalloween2026.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dbrighthd.qahalloween2026.utils.PlayerMethodUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {

    @Shadow
    @Final
    protected ServerLevel level;

    @Inject(method = "createPortal", at = @At("RETURN"), cancellable = true)
    private void placeCustomPortal(BlockPos origin, Direction.Axis portalAxis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir)
    {
        Optional<BlockUtil.FoundRectangle> result = cir.getReturnValue();

        if (result.isEmpty() || level.dimension() != ServerLevel.NETHER) {
            return;
        }

        BlockPos portalPos = result.get().minCorner.above(2);

        BlockPos referencePos = portalPos.subtract(new Vec3i(4,2,2));
        StructureTemplate template = Objects.requireNonNull(level.getServer()).getStructureTemplateManager().get(Identifier.fromNamespaceAndPath("fishysift","sift_portal")).orElse(null);

        Rotation rotation = Rotation.NONE;
        if(portalAxis != Direction.Axis.X)
        {
            rotation = Rotation.CLOCKWISE_90;
        }
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation);
        template.placeInWorld(level,referencePos,referencePos,settings, level.getRandom(), Block.UPDATE_ALL);
    }
}
