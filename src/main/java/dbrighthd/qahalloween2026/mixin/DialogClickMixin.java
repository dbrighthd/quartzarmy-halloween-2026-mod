package dbrighthd.qahalloween2026.mixin;

import com.mojang.authlib.GameProfile;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static dbrighthd.qahalloween2026.mainloop.PlayerSpecificStuff.killTeighter;
import static dbrighthd.qahalloween2026.mainloop.PlayerTick.stringBar;
import static dbrighthd.qahalloween2026.systems.EndRespawnSystem.setRespawnTicks;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.respawnEffects;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.getRandomPlayer;

// is there a better way to do this?? it feels weird to need to do a mixin to handle dialogs
@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class DialogClickMixin {

    @Shadow @Final
    protected MinecraftServer server;

    @Shadow
    public abstract GameProfile getOwner();

    @Inject(method = "handleCustomClickAction", at = @At("TAIL"))
    private void onDialogClick(ServerboundCustomClickActionPacket packet, CallbackInfo ci)
    {
        Identifier id = packet.id();
        if (!id.getNamespace().equals("dbright")) return; //shouldnt happen I dont think? idk if minecraft has any custom ones
        ServerPlayer player = server.getPlayerList().getPlayer(getOwner().id());
        if (player == null) return;
        switch (id.toString()) {
            case "dbright:random_player" -> {
                ServerPlayer toteleport = getRandomPlayer(player);
                if(toteleport != null)
                {
                    player.canRandomlyTeleportTo(toteleport.getX(), toteleport.getY(), toteleport.getZ());
                    player.teleportTo(toteleport.level(),toteleport.getX(),toteleport.getY(),toteleport.getZ(), Set.of(),player.getYRot(),player.getXRot(),false);
                    player.setGameMode(GameType.DEFAULT_MODE);
                    respawnEffects(player);
                    player.connection.send(ClientboundClearDialogPacket.INSTANCE);
                }
            }
            case "dbright:continue_fight" -> {
                setRespawnTicks(player, Config.endRespawnSeconds*20);
                player.connection.send(ClientboundClearDialogPacket.INSTANCE);

            }
            case "dbright:kill_teighter" -> killTeighter(player.level().getServer());

            default -> {
                player.setGameMode(GameType.DEFAULT_MODE);
                respawnEffects(player);
                player.connection.send(ClientboundClearDialogPacket.INSTANCE);}
        }
        stringBar(player);
    }
}
