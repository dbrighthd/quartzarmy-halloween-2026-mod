package dbrighthd.qahalloween2026.events;

import dbrighthd.qahalloween2026.bind.BindSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventsStorage {
    public static Map<UUID,Integer> hurtThisTick = new HashMap<>();
    public static Map<UUID,Integer> attackedThisTick = new HashMap<>();
    public static final int stringAmount = 2;

    public static void refreshAndDoStuff(ServerLevel serverLevel)
    {
        refreshHurtPlayers(serverLevel);
        refreshAttackedPlayers(serverLevel);
    }

    public static void refreshHurtPlayers(ServerLevel serverLevel)
    {
        for(UUID uuid : hurtThisTick.keySet())
        {
            BindSystem.cancelBind((ServerPlayer)serverLevel.getPlayerByUUID(uuid));
        }
        hurtThisTick.clear();
    }

    public static void refreshAttackedPlayers(ServerLevel serverLevel)
    {
        for(var pair : attackedThisTick.entrySet())
        {
            BindSystem.giveString((ServerPlayer)serverLevel.getPlayerByUUID(pair.getKey()),pair.getValue()*stringAmount);
        }
        attackedThisTick.clear();
    }
}
