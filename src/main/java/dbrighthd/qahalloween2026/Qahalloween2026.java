package dbrighthd.qahalloween2026;

import dbrighthd.qahalloween2026.mainloop.GlobalVars;
import dbrighthd.qahalloween2026.mainloop.OnEntityLoad;
import dbrighthd.qahalloween2026.mainloop.Setup;
import dbrighthd.qahalloween2026.mainloop.TickLoop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.level.gamerules.GameRules;

public class Qahalloween2026 implements ModInitializer {

    public static final String MOD_ID = "qahalloween2026";
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(Setup::actOneSetup);
        OnEntityLoad.init();
        TickLoop.init();
        GlobalVars.init();
    }
}
