package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.utils.GeneralUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;

public class GlobalVars {
    public static boolean hasGlowFlies = false;
    public static GeneralUtils.Tide tide;
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
