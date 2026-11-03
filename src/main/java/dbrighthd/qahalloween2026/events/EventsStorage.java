package dbrighthd.qahalloween2026.events;

import dbrighthd.qahalloween2026.mainloop.Config;
import dbrighthd.qahalloween2026.systems.BindSystem;
import dbrighthd.qahalloween2026.systems.JumpAttackCountSystem;
import dbrighthd.qahalloween2026.systems.SheepSystem;
import dbrighthd.qahalloween2026.utils.GeneralUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.sheep.Sheep;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dbrighthd.qahalloween2026.mainloop.Config.stringPerDamage;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;

public class EventsStorage {

    public record AttackInfo(Entity entity, int hurtAmount){}
    public static Map<UUID,Integer> hurtThisTick = new HashMap<>();
    public static Map<UUID,AttackInfo> attackedThisTick = new HashMap<>();
    public static void refreshAndDoStuff(MinecraftServer server)
    {
        refreshHurtPlayers(server);
        refreshAttackedPlayers(server);
    }
    public static void refreshHurtPlayers(MinecraftServer server)
    {
        for(UUID uuid : hurtThisTick.keySet())
        {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if(player == null)
            {
                continue;
            }
            BindSystem.cancelBind(player);
        }
        hurtThisTick.clear();
    }

    public static void refreshAttackedPlayers(MinecraftServer server)
    {
        for(var pair : attackedThisTick.entrySet())
        {
            ServerPlayer player = server.getPlayerList().getPlayer(pair.getKey());
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
        if(entity instanceof Chicken chicken)
        {
            GeneralUtils.replaceEntity(player.level(),chicken, EntityTypes.PHANTOM,1);
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
            grantAdvancement(player,"main/jumpattack");
            int chain = JumpAttackCountSystem.addJumpAttack(player,1);
            amount *= 2;
            if(chain >=5)
            {
                amount = (int)(amount * 1.5);
                grantAdvancement(player,"challenges/jumpchain");
            }
        }
        BindSystem.giveString(player,amount* stringPerDamage);
    }
}
