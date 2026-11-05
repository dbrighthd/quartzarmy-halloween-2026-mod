package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TargetData;
import dbrighthd.qahalloween2026.dialogs.DeathDialog;
import dbrighthd.qahalloween2026.systems.*;
import dbrighthd.qahalloween2026.events.EventsStorage;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.level.GameType;
import java.util.ArrayList;
import java.util.List;
import static dbrighthd.qahalloween2026.mainloop.Setup.spawnDataCheck;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.getCurrentTide;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;

public class TickLoop {
    public static void init()
    {
        ServerTickEvents.END_SERVER_TICK.register(TickLoop::tick);
        ServerTickEvents.END_SERVER_TICK.register(EventsStorage::refreshAndDoStuff);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, _) -> {
            if (entity instanceof Skeleton
                    && source.getDirectEntity() instanceof Arrow arrow) {
                TargetData arrowData = arrow.getAttachedOrCreate(DataAttachments.TARGET_DATA);
                Entity target = arrow.level().getEntity(arrowData.target());
                if(target instanceof Player player)
                {
                    grantAdvancement((ServerPlayer)player,"challenges/arrowskeleton");
                }
            }
            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((_, newPlayer, _) -> {
            DeathDialog.show(newPlayer);
            newPlayer.setGameMode(GameType.SPECTATOR);
        });
    }


    public static void tick(MinecraftServer server)
    {
        GlobalVars.tide = getCurrentTide(server.overworld());
        if(spawnDataCheck(server) == Setup.StartPhase.IN_GAME)
        {
            for(ServerPlayer player : server.getPlayerList().getPlayers())
            {
                PlayerTick.doPlayerTick(player, player.level());
            }
            for (ServerLevel level : server.getAllLevels()) {
                List<Entity> snapshot = new ArrayList<>();
                level.getAllEntities().forEach(snapshot::add); // NO CONCURRENCY ISSUES PLEASE I WILL CRY
                for(Entity entity : snapshot)
                {
                    if(entity == null)
                    {
                        break;
                    }
                    if(entity.tickCount <= 1)
                    {
                        OnEntityLoad.onEntityLoad(entity,level);
                    }
                    EntityTick.entityTick(entity);
                }
            }
        }
        else
        {
            for(ServerPlayer player : server.getPlayerList().getPlayers())
            {
                PlayerTick.beforeTick(player);
            }
        }

    }

    @SuppressWarnings("unused")
    public static void hideEntityFromPlayer(ServerPlayer player, Entity targetEntity) {
        ClientboundRemoveEntitiesPacket destroyPacket = new ClientboundRemoveEntitiesPacket(targetEntity.getId());
        player.connection.send(destroyPacket);
    }

}
