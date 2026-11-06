package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TimerData;
import dbrighthd.qahalloween2026.systems.BindSystem;
import dbrighthd.qahalloween2026.systems.GlowflySystem;
import dbrighthd.qahalloween2026.systems.WetIchorSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.getSunPitch;

public class PlayerSpecificStuff {
    public static void tickPlayerSpecifics(ServerPlayer player) {
        Set<String> tags = player.entityTags();
        if(tags.isEmpty())
        {
            return;
        }
        boolean isEntraBit = false;
        for(String tag : tags)
        {
            if(tag.equals(MeanTag.BugPuncher.name()))
            {
                bugpuncherTick(player);
            }
            else if(tag.equals(MeanTag.EntraBit.name()))
            {
                isEntraBit = true;
                entraBitTick(player);
            }
            else if(tag.equals(MeanTag.Moth.name()))
            {
                mothTick(player);
            }
        }
        if(!isEntraBit)
        {
            unEntraBit(player);
        }
    }

    public static void mothTick(ServerPlayer moth)
    {
        int timer = moth.getAttachedOrCreate(DataAttachments.INPUT_TIMER_DATA).ticksLeft();
        Input input = moth.getLastClientInput();
        boolean moving = input.forward() || input.backward() || input.left() || input.right() || input.jump();
        if(moving)
        {
            timer = 80;
        }
        if(timer >= 0)
        {
            timer--;
        }
        else {
            long time = moth.level().getOverworldClockTime()%24000L;
            if(!(time > 12000))
            {
                float midYaw = time <= 6000 ? -90 : 90;
                float yawDiff = Mth.wrapDegrees(midYaw - moth.getYRot());
                float pitchDiff = getSunPitch(time) - moth.getXRot();

                float distance = Mth.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);

                if (distance > 0.001f) {
                    float step = Math.min(2f, distance) / distance;

                    float yawStep = yawDiff * step;
                    float pitchStep = pitchDiff * step;

                    moth.setYRot(moth.getYRot() + yawStep);
                    moth.setXRot(moth.getXRot() + pitchStep);

                    moth.connection.send(
                            new ClientboundPlayerRotationPacket(yawStep, true, pitchStep, true)
                    );
                }
            }
        }
        moth.setAttached(DataAttachments.INPUT_TIMER_DATA,new TimerData(timer));
    }

    public static void entraBitTick(ServerPlayer entraBit)
    {
        entraBit.addPostEffect(Identifier.parse("minecraft:creeper"));
    }

    public static void unEntraBit(ServerPlayer notEntraBit)
    {
        notEntraBit.removePostEffect(Identifier.parse("minecraft:creeper"));
    }
    public static void bugpuncherTick(ServerPlayer player)
    {
        double overshootAmount = 4;
        int round = 16;
        BlockPos playerAbovePos = player.getOnPos().above(16);
        Vec3 playerSpotlightPos = new Vec3(((int)(Math.round(playerAbovePos.getX()/(double)round)*round)),((int)(Math.round(playerAbovePos.getY()/(double)round)*round)),(int)(Math.round(playerAbovePos.getZ()/(double)round)*round));

        Vec3 targetPosSub = player.position().subtract(playerSpotlightPos);
        double dist = player.position().distanceTo(playerSpotlightPos);

        Vec3 targetPos = playerSpotlightPos.add(targetPosSub.normalize().scale(dist+overshootAmount));

        int color = -1;

        if(BindSystem.isInBind(player))
        {
            color = 1;
        }
        if (WetIchorSystem.isPlayerWet(player))
        {
            color = 12;
        }
        if(player.getHealth() <= 3)
        {
            color = (player.tickCount/10 % 2 == 0) ? 1 : 15;
        }
        if (GlowflySystem.isPlayerInfected(player))
        {
            color = (player.tickCount % 2 == 0) ? 15 : 14;
        }
        if(player.isDeadOrDying() || player.wasHurtRecently())
        {
            color = 15;
        }
        if (WetIchorSystem.isPlayerIchorized(player))
        {
            color = (player.tickCount % 2 == 0) ? 1 : 10;
        }
        if (player.isOnFire())
        {
            color = (player.tickCount % 2 == 0) ? 5 : 2;
        }
        if(color > -1)
        {
            SpotLight.ResolvedSpotlight spotlight = new SpotLight.ResolvedSpotlight(1,playerSpotlightPos,targetPos,color,15);
            SpotLight.spawnSpotlightParticles(player.level(),spotlight,100);
        }

    }

    public static boolean isTeighterInGame(ServerPlayer player)
    {
        MinecraftServer server = player.level().getServer();
        PlayerList playerList = server.getPlayerList();
        for(ServerPlayer playerInList : playerList.getPlayers())
        {
            if(playerInList.entityTags().contains(MeanTag.Teighter.name()))
            {
                return true;
            }
        }
        return false;
    }

    public static String getTeighterName(ServerPlayer player)
    {
        MinecraftServer server = player.level().getServer();
        PlayerList playerList = server.getPlayerList();
        for(ServerPlayer playerInList : playerList.getPlayers())
        {
            if(playerInList.entityTags().contains(MeanTag.Teighter.name()))
            {
                return playerInList.getName().getString();
            }
        }
        return "";
    }

    public static void killTeighter(MinecraftServer server)
    {
        PlayerList playerList = server.getPlayerList();
        for(ServerPlayer playerInList : playerList.getPlayers())
        {
            if(playerInList.entityTags().contains(MeanTag.Teighter.name()))
            {
                playerInList.kill(playerInList.level());
            }
        }
    }

    public enum MeanTag {
        BugPuncher,
        Teighter,
        EntraBit,
        Moth,
        Gold,
    }
}
