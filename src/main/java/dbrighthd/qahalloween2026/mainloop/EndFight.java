package dbrighthd.qahalloween2026.mainloop;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PositionPath;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.particleAtEntity;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.particleAtEntityOffset;
import static net.minecraft.world.level.dimension.end.EnderDragonFight.getPodiumLocation;

public class EndFight {
    public static EndFightPhase endFightPhase = EndFightPhase.BROODING;
    public static int phaseDuration = 0;

    public static void spawnBoss(ServerLevel endLevel) {
        Enderman soulEnderman = EntityTypes.ENDERMAN.create(endLevel, EntitySpawnReason.EVENT);

        soulEnderman.moveOrInterpolateTo(PositionPath.of(Vec3.atCenterOf(endLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,new BlockPos(0,0,0)))));
        soulEnderman.setPersistenceRequired();
        soulEnderman.setCustomName(Component.literal("Soul Corrupted Enderman"));
        soulEnderman.setCustomNameVisible(false);
        soulEnderman.addTag("soulEnderman");
        changeAttribute(soulEnderman, Attributes.FALL_DAMAGE_MULTIPLIER,0);
        endLevel.addFreshEntity(soulEnderman);
    }

    private static ServerBossEvent bossBar;

    public static void endFightTick(Enderman soulEnderman) {
        if(!soulEnderman.entityTags().contains("soulEnderman"))
        {
            return;
        }
        bossBarTick(soulEnderman);
        particleAtEntityOffset(soulEnderman, new Vec3(0,1.5,0), ParticleTypes.SCULK_SOUL,0.25f,1f,0.25f,1,0);

        switch(endFightPhase)
        {
            case TELEPORTING -> {teleportingPhase(soulEnderman);}
            case BROODING -> {broodingPhase(soulEnderman);}
            case SALMON -> {salmonPhase(soulEnderman);}

        }
        phaseDuration++;
    }

    public static void bossBarTick(Enderman soulEnderman)
    {
        if (!(soulEnderman.level() instanceof ServerLevel level)) return;
        if (bossBar == null) {
            bossBar = new ServerBossEvent(
                    UUID.randomUUID(),
                    Component.literal("Soul Corrupted Enderman"),
                    BossEvent.BossBarColor.BLUE,
                    BossEvent.BossBarOverlay.PROGRESS
            );
        }

        if (!soulEnderman.isAlive()) {
            bossBar.removeAllPlayers();
            bossBar = null;
            return;
        }
        bossBar.setProgress(Mth.clamp(
                soulEnderman.getHealth() / soulEnderman.getMaxHealth(),
                0.0F, 1.0F
        ));
        for (ServerPlayer player : level.players()) {
            bossBar.addPlayer(player);
        }
        for (ServerPlayer player : List.copyOf(bossBar.getPlayers())) {
            if (player.level() != level) {
                bossBar.removePlayer(player);
            }
        }
    }

    public static void teleportingPhase(Enderman soulEnderman)
    {
        soulEnderman.setNoGravity(false);
        if(soulEnderman.tickCount % 10 == 0)
        {
            randomTeleport(soulEnderman,20);
        }
    }

    public static void broodingPhase(Enderman soulEnderman)
    {
        soulEnderman.setPos(0,80,0);
        soulEnderman.setNoGravity(true);
    }
    public static void salmonPhase(Enderman soulEnderman)
    {
        soulEnderman.setPos(0,80,0);
        soulEnderman.setNoGravity(true);

        if(phaseDuration < 60)
        {
            particleAtEntity(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0xFFFFAA, 10), 10, 10, 10,10, 0);
        }
    }
    public static void setPhase(EndFightPhase phase)
    {
        phaseDuration = 0;
        endFightPhase = phase;
    }

    public static void randomTeleport(Enderman soulEnderman, int radius)
    {
        soulEnderman.randomTeleport(soulEnderman.getRandom().nextIntBetweenInclusive(-radius,radius),90, soulEnderman.getRandom().nextIntBetweenInclusive(-radius,radius), true, BlockTags.ENDERMAN_DOES_NOT_TELEPORT_TO);
    }



    public enum EndFightPhase {
        START,
        TELEPORTING,
        SALMON,
        ATTACKING,
        END,
        BROODING,
    }

}
