package dbrighthd.qahalloween2026.mainloop;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class GlobalVars {

    public static PlayerTeam monsterTeam;
    public static void init()
    {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            ServerScoreboard scoreboard = minecraftServer.getScoreboard();

            monsterTeam = scoreboard.getPlayerTeam("monsterTeam");

            if (monsterTeam == null) {
                monsterTeam = scoreboard.addPlayerTeam("monsterTeam");
                monsterTeam.setAllowFriendlyFire(false);
            }
        });

    }
}
