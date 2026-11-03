package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.Qahalloween2026;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.phys.Vec2;
import java.util.Objects;

public class Setup {

    public static void bootingSetup(MinecraftServer server)
    {
        ServerLevel level = server.overworld();
        if(Qahalloween2026.DEV)
        {
            Setup.start(server);
        }
        level.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, server);
        level.getGameRules().set(GameRules.KEEP_INVENTORY, true, server);
        level.getGameRules().set(GameRules.ADVANCE_WEATHER, false,server);
        level.getGameRules().set(GameRules.IMMEDIATE_RESPAWN, true,server);
        level.getGameRules().set(GameRules.SPAWN_PHANTOMS,false,server);
        level.getGameRules().set(GameRules.SPAWN_PATROLS,false,server);
        level.getGameRules().set(GameRules.SPAWN_WANDERING_TRADERS,false,server);
        level.getGameRules().set(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER,0,server);

    }

    public static boolean hasStarted(MinecraftServer server)
    {
        return spawnDataCheck(server) == StartPhase.IN_GAME;
    }
    public static void firstSetup(MinecraftServer server)
    {
        ServerLevel level = server.overworld();
        level.getGameRules().set(GameRules.ADVANCE_TIME, false, level.getServer());
        BlockPos spawnPos = server.getRespawnData().pos().atY(300);
        placeBedrockThingy(spawnPos,level);
    }
    public static void start(MinecraftServer server)
    {
        CommandStorage storage = server.getCommandStorage();
        Identifier id = Identifier.parse("dbright:global");
        CompoundTag data = storage.get(id);
        data.putString("start_stage","started");
        ServerLevel level = server.overworld();
        level.getGameRules().set(GameRules.ADVANCE_TIME, true, level.getServer());
        BlockPos spawnPos = server.getRespawnData().pos().atY(300);
        breakBedrockThingy(spawnPos,level);
        playerSetup(server);
    }

    public static void playerSetup(MinecraftServer server)
    {
        ServerLevel level = server.overworld();

        Vec2 position = new Vec2(level.getRespawnData().pos().getX(),level.getRespawnData().pos().getZ());
        try{
            SpreadPlayersCommand.spreadPlayers(server.createCommandSourceStack(),position,10,10,256,false,server.getPlayerList().getPlayers());
        }
        catch (Exception _)
        {
        }
        for(ServerPlayer player : server.getPlayerList().getPlayers())
        {
            player.removeAllEffects();
            player.setGameMode(GameType.SURVIVAL);
        }
    }


    public static void placeBedrockThingy(BlockPos pos, ServerLevel level)
    {
        BlockPos referencePos = pos.subtract(new Vec3i(4,0,4));
        Objects.requireNonNull(level.getServer()).getStructureTemplateManager().get(Identifier.fromNamespaceAndPath("dbright", "box")).ifPresent(template -> template.placeInWorld(level, referencePos, referencePos, new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_ALL));
    }

    public static void breakBedrockThingy(BlockPos pos, ServerLevel level)
    {
        BlockPos referencePos = pos.subtract(new Vec3i(4,0,4));
        Objects.requireNonNull(level.getServer()).getStructureTemplateManager().get(Identifier.fromNamespaceAndPath("dbright", "emptybox")).ifPresent(template -> template.placeInWorld(level, referencePos, referencePos, new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_ALL));
    }

    public static StartPhase spawnDataCheck(MinecraftServer server)
    {
        CommandStorage storage = server.getCommandStorage();
        Identifier id = Identifier.parse("dbright:global");

        CompoundTag data = storage.get(id);
        String stage = data.getStringOr("start_stage", "");
        if(stage.equals("started") || Qahalloween2026.DEV)
        {
            return StartPhase.IN_GAME;
        }
        if(stage.isEmpty())
        {
            firstSetup(server);
            data.putString("start_stage","in_box");
        }
        storage.set(id,data);
        return StartPhase.BOX;
    }

    public enum StartPhase {
        STARTING,
        BOX,
        IN_GAME
    }
}
