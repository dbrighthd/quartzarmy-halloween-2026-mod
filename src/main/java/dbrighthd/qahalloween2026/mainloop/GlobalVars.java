package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.utils.GeneralUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.ObjectContents;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

import java.util.Optional;

public class GlobalVars {
    public static boolean hasGlowFlies = false;
    public static GeneralUtils.Tide tide;
    public static PlayerTeam monsterTeam;
    public static PlayerTeam goldTeam;
    public static ServerPlayer tudad;
    public static void init()
    {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            ServerScoreboard scoreboard = minecraftServer.getScoreboard();

            monsterTeam = scoreboard.getPlayerTeam("monsterTeam");

            if (monsterTeam == null) {
                monsterTeam = scoreboard.addPlayerTeam("monsterTeam");
                monsterTeam.setAllowFriendlyFire(false);
            }

            goldTeam = scoreboard.getPlayerTeam("goldTeam");
            if(goldTeam == null)
            {
                goldTeam = scoreboard.addPlayerTeam("goldTeam");
                goldTeam.setPlayerPrefix(Component.object(new ObjectContents(new AtlasSprite(AtlasIds.ITEMS, Identifier.parse("minecraft:item/gold_ingot")), Optional.of(Component.literal(""))).contents()));
            }
        });

    }
}
