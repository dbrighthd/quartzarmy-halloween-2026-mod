package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TimerData;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.respawnEffects;

public class EndRespawnSystem {
    public static void endRespawnTick(ServerPlayer player)
    {
        int ticks = player.getAttachedOrCreate(DataAttachments.ENDRESPAWN_DATA).ticksLeft();

        ServerLevel end = player.level().getServer().getLevel(Level.END);
        Vec3 freezePos = Vec3.atBottomCenterOf(end.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,new BlockPos(20,0,0)));
        if(ticks > 1)
        {
            if(ticks > Config.endRespawnSeconds*20 - 1)
            {
                player.teleportTo(end, freezePos.x(), freezePos.y(), freezePos.z(), Set.of(),player.getYRot(),player.getXRot(),false);
            }
            if(ticks%20 == 0)
            {

                player.connection.send(
                        new ClientboundSetSubtitleTextPacket(Component.literal("Respawning, " + (ticks/20)+ " Seconds left..."))
                );
                player.connection.send(
                        new ClientboundSetTitleTextPacket(Component.empty())
                );
            }
            player.setCamera(player);
            player.setGameMode(GameType.SPECTATOR);
        }
        if(ticks == 1)
        {
            player.teleportTo(end, freezePos.x(), freezePos.y(), freezePos.z(), Set.of(),player.getYRot(),player.getXRot(),false);
            player.connection.send(new ClientboundSetSubtitleTextPacket(Component.empty()));
            player.connection.send(new ClientboundSetTitleTextPacket(Component.empty()));
            respawnEffects(player);
            player.setGameMode(GameType.DEFAULT_MODE);
        }
        if(ticks > 0)
        {
            ticks--;
        }
        player.setAttached(DataAttachments.ENDRESPAWN_DATA,new TimerData(ticks));
    }

    public static void setRespawnTicks(ServerPlayer player, int ticks)
    {
        player.setAttached(DataAttachments.ENDRESPAWN_DATA,new TimerData(ticks));

    }
}
