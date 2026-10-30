package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.systems.BindSystem;
import dbrighthd.qahalloween2026.systems.GlowflySystem;
import dbrighthd.qahalloween2026.systems.WetIchorSystem;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class PlayerSpecificStuff {
    public static void tickPlayerSpecifics(ServerPlayer player) {
        Set<String> tags = player.entityTags();
        if(tags.isEmpty())
        {
            return;
        }
        for(String tag : tags)
        {
            if(tag.equals(MeanTag.BUGPUNCHER.name()))
            {
                bugpuncherTick(player);
            }
        }
    }

    public static void bugpuncherTick(ServerPlayer player)
    {
        if(BindSystem.isInBind(player))
        {
            return;
        }
        double overshootAmount = 4;
        int round = 16;
        BlockPos playerAbovePos = player.getOnPos().above(16);
        Vec3 playerSpotlightPos = new Vec3(((int)(Math.round(playerAbovePos.getX()/(double)round)*round)),((int)(Math.round(playerAbovePos.getY()/(double)round)*round)),(int)(Math.round(playerAbovePos.getZ()/(double)round)*round));

        Vec3 targetPosSub = player.position().subtract(playerSpotlightPos);
        double dist = player.position().distanceTo(playerSpotlightPos);

        Vec3 targetPos = playerSpotlightPos.add(targetPosSub.normalize().scale(dist+overshootAmount));

        int color = 1;

        if (WetIchorSystem.isPlayerWet(player))
        {
            color = 12;
        }
        if(player.getHealth() <= 1)
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
        SpotLight.ResolvedSpotlight spotlight = new SpotLight.ResolvedSpotlight(1,playerSpotlightPos,targetPos,color,15);
        SpotLight.spawnSpotlightParticles(player.level(),spotlight,100);
    }

    public enum MeanTag {
        BUGPUNCHER,
    }
}
