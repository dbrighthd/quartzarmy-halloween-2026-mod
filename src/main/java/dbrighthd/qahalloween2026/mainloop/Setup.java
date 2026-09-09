package dbrighthd.qahalloween2026.mainloop;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gamerules.GameRules;

public class Setup {
    public static void actOneSetup(MinecraftServer server)
    {
        ServerLevel level = server.overworld();
        level.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, level.getServer());
        level.getGameRules().set(GameRules.KEEP_INVENTORY, true, level.getServer());
        level.getGameRules().set(GameRules.ADVANCE_WEATHER, false  , level.getServer());

    }
}
