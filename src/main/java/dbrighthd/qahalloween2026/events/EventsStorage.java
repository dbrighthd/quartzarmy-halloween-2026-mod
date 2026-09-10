package dbrighthd.qahalloween2026.events;

import dbrighthd.qahalloween2026.bind.BindSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dbrighthd.qahalloween2026.mainloop.Config.stringPerDamage;

public class EventsStorage {
    public static Map<UUID,Integer> hurtThisTick = new HashMap<>();
    public static Map<UUID,Integer> attackedThisTick = new HashMap<>();

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
           handleAttack((ServerPlayer)serverLevel.getPlayerByUUID(pair.getKey()),pair.getValue());
        }
        attackedThisTick.clear();
    }

    public static void handleAttack(ServerPlayer player, int amount)
    {
        BindSystem.giveString(player,amount* stringPerDamage);
    }
}
