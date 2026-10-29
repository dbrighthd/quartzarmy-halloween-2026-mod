package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.Qahalloween2026;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.Objects;

public class Setup {

    public static void bootingSetup(MinecraftServer server)
    {
        ServerLevel level = server.overworld();
        if(Qahalloween2026.DEV)
        {
            Setup.start(server);
        }
        level.getGameRules().set(GameRules.NATURAL_HEALTH_REGENERATION, false, level.getServer());
        level.getGameRules().set(GameRules.KEEP_INVENTORY, true, level.getServer());
        level.getGameRules().set(GameRules.ADVANCE_WEATHER, false  , level.getServer());
        level.getGameRules().set(GameRules.IMMEDIATE_RESPAWN, true, level.getServer());
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
        catch (Exception e)
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
        StructureTemplate template = Objects.requireNonNull(level.getServer()).getStructureTemplateManager().get(Identifier.fromNamespaceAndPath("dbright","box")).orElse(null);
        template.placeInWorld(level,referencePos,referencePos,new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_ALL);
    }

    public static void breakBedrockThingy(BlockPos pos, ServerLevel level)
    {
        BlockPos referencePos = pos.subtract(new Vec3i(4,0,4));
        StructureTemplate template = Objects.requireNonNull(level.getServer()).getStructureTemplateManager().get(Identifier.fromNamespaceAndPath("dbright","emptybox")).orElse(null);
        template.placeInWorld(level,referencePos,referencePos,new StructurePlaceSettings(), level.getRandom(), Block.UPDATE_ALL);
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
