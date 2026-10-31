package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.SheepData;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.grantAdvancement;

public class SheepSystem {
    public static void sheepTick(ServerPlayer player)
    {
        if(player == null)
        {
            return;
        }
        SheepData data = player.getAttachedOrCreate(DataAttachments.SHEEP_DATA);
        int sheepCooldown = data.sheepCooldown();
        int sheepCount = data.sheepCount();
        int asleepTime = data.asleepTime();
        if(sheepCooldown > 0)
        {
            sheepCooldown--;
        }
        else
        {
            sheepCooldown = Config.sheepCooldownMax;
            sheepCount = 0;
        }
        if(sheepCount > Config.sheepUntilSleep)
        {
            player.addEffect( new MobEffectInstance(MobEffects.SLOWNESS, Config.sleepTime, 100));
            player.addEffect( new MobEffectInstance(MobEffects.WEAKNESS, Config.sleepTime, 100));
            asleepTime = Config.sleepTime;
            sheepCount = 0;
            grantAdvancement(player,"main/sheepsleep");

        }
        if(asleepTime > 0)
        {
            asleepTime--;
        }
        player.setAttached(DataAttachments.SHEEP_DATA,new SheepData(sheepCount, sheepCooldown,asleepTime));
    }

    public static void addSheepToPlayer(ServerPlayer player)
    {
        SheepData data = player.getAttachedOrCreate(DataAttachments.SHEEP_DATA);
        int sheepCount = data.sheepCount()+1;
        player.setAttached(DataAttachments.SHEEP_DATA,new SheepData(sheepCount, Config.sheepCooldownMax,data.asleepTime()));

        player.connection.send(
                new ClientboundSetSubtitleTextPacket(Component.empty())
        );
        player.connection.send(
                new ClientboundSetTitleTextPacket(Component.literal(sheepCount+""))
        );

    }

    public static boolean isPlayerSleepy(ServerPlayer player)
    {
        return player.getAttachedOrCreate(DataAttachments.SHEEP_DATA).asleepTime() > 0;
    }
}
