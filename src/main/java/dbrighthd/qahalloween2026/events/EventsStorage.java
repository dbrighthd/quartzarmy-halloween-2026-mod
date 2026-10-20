package dbrighthd.qahalloween2026.events;

import dbrighthd.qahalloween2026.mainloop.Config;
import dbrighthd.qahalloween2026.systems.BindSystem;
import dbrighthd.qahalloween2026.systems.SheepSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dbrighthd.qahalloween2026.mainloop.Config.placeMissChance;
import static dbrighthd.qahalloween2026.mainloop.Config.stringPerDamage;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.playSoundAtEntity;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.triggerWindBurst;

public class EventsStorage {

    public record AttackInfo(Entity entity, int hurtAmount){};
    public static Map<UUID,Integer> hurtThisTick = new HashMap<>();
    public static Map<UUID,AttackInfo> attackedThisTick = new HashMap<>();
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
            ServerPlayer player = (ServerPlayer)serverLevel.getPlayerByUUID(pair.getKey());
            AttackInfo attackInfo = pair.getValue();
           handleAttack(player,attackInfo.hurtAmount());
           handleHurt(player,attackInfo);
        }
        attackedThisTick.clear();
    }

    public static void handleHurt(ServerPlayer player, AttackInfo attackInfo)
    {
        Entity entity = attackInfo.entity;
        if(player == null || entity == null)
        {
            return;
        }
        if(entity instanceof Sheep sheep)
        {
            SheepSystem.addSheepToPlayer(player);
            if(!sheep.isSheared())
            {
                sheep.setSheared(true);
                BindSystem.giveString(player, Config.stringPerSheep);
                playSoundAtEntity(entity, SoundEvents.SHEEP_SHEAR,100,1);
            }
        }
    }

    public static void handleAttack(ServerPlayer player, int amount)
    {
        if(player == null)
        {
            return;
        }
        if(!player.onGround())
        {
            triggerWindBurst(player,0.5);
            amount *= 2;
        }
        BindSystem.giveString(player,amount* stringPerDamage);
    }
}
