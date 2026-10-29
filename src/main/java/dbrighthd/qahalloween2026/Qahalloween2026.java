package dbrighthd.qahalloween2026;

import dbrighthd.qahalloween2026.commands.EndFightCommand;
import dbrighthd.qahalloween2026.mainloop.GlobalVars;
import dbrighthd.qahalloween2026.mainloop.OnEntityLoad;
import dbrighthd.qahalloween2026.mainloop.Setup;
import dbrighthd.qahalloween2026.mainloop.TickLoop;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class Qahalloween2026 implements ModInitializer {

    public static final String MOD_ID = "qahalloween2026";
    public static final boolean DEV = true;
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(Setup::bootingSetup);
        OnEntityLoad.init();
        TickLoop.init();
        GlobalVars.init();
        EndFightCommand.register();
    }
}
