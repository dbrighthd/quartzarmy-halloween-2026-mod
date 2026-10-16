package dbrighthd.qahalloween2026.events;

import dbrighthd.qahalloween2026.systems.BindSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dbrighthd.qahalloween2026.mainloop.Config.stringPerDamage;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.triggerWindBurst;

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
            ServerPlayer player = (ServerPlayer)serverLevel.getPlayerByUUID(uuid);
            if(player == null)
            {
                continue;
            }
            BindSystem.cancelBind(player);
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
        if(!player.onGround())
        {
            triggerWindBurst(player,0.5);
            amount *= 2;
        }
        BindSystem.giveString(player,amount* stringPerDamage);
    }
}
