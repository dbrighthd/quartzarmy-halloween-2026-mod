package dbrighthd.qahalloween2026.mixin;

import dbrighthd.qahalloween2026.mainloop.Setup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
@Mixin(ButtonBlock.class)
public abstract class ButtonMixin {
    @Inject(method = "press(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private void tryStart(BlockState state, Level level, BlockPos pos, Player player, CallbackInfo ci){
        if(state.is(Blocks.OAK_BUTTON) && player.getName().contains(Component.literal("dbright")) && pos.getY() > 300)
        {
            if(player.level().getServer() != null)
            {
                Setup.start(Objects.requireNonNull(player.level().getServer()));
            }
        }
    }
}
