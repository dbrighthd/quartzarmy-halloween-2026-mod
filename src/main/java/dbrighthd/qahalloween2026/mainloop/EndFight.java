package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.systems.GlowflySystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;

public class EndFight {
    public static EndFightPhase endFightPhase = EndFightPhase.BROODING;

    public static int phaseNum;
    public static boolean begun;
    public static Vec3 startPos = new Vec3(0,80,0);
    public static Vec3 broodPos = new Vec3(0,80,0);
    public static int TeleportingPhaseLength = 120;
    public static int AttackPhaseLength = 380;
    public static int endermanMaxHealth = 350;
    public static int endermanTargetSwitchTime = 60;
    public static boolean phaseSetUp = false;
    public static int phaseDuration = 0;
    public static boolean soulified = false;
    public static boolean killed = false;

    public static void spawnBoss(ServerLevel endLevel) {
        soulified = false;
        Enderman soulEnderman = EntityTypes.ENDERMAN.create(endLevel, EntitySpawnReason.EVENT);
        endFightPhase = EndFightPhase.DORMANT;
        if(soulEnderman == null)
        {
            return;
        }
        soulEnderman.moveOrInterpolateTo(PositionPath.of(Vec3.atBottomCenterOf(endLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,new BlockPos(0,0,0)))));
        startPos = Vec3.atBottomCenterOf(endLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,new BlockPos(0,0,0)));
        broodPos = Vec3.atCenterOf(endLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,new BlockPos(0,0,0)).above(15));
        soulEnderman.setPersistenceRequired();
        soulEnderman.setGlowingTag(true);
        soulEnderman.setNoAi(true);
        soulEnderman.addTag("soulEnderman");
        changeAttribute(soulEnderman, Attributes.FALL_DAMAGE_MULTIPLIER,0);
        changeAttribute(soulEnderman, Attributes.MAX_HEALTH,endermanMaxHealth);
        soulEnderman.setHealth(1);
        soulEnderman.setPermanentlyInvulnerable(true);
        endLevel.addFreshEntity(soulEnderman);
        endLevel.setBlockAndUpdate(endLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING,new BlockPos(0,0,0)).below(), Blocks.AIR.defaultBlockState());
    }

    private static ServerBossEvent bossBar;

    public static void endFightTick(Enderman soulEnderman) {
        if(!soulEnderman.entityTags().contains("soulEnderman"))
        {
            return;
        }
        if(soulEnderman.isDeadOrDying() && !killed)
        {
            MinecraftServer server = soulEnderman.level().getServer();
            if(server != null)
            {
                for(ServerPlayer player : server.getPlayerList().getPlayers())
                {
                    grantAdvancement(player,"main/finished");
                }
                if(soulEnderman.level() instanceof  ServerLevel serverLevel)
                {
                    BlockPos referencePos = BlockPos.containing(startPos).subtract(new Vec3i(4,2,4));
                    Objects.requireNonNull(server).getStructureTemplateManager().get(Identifier.fromNamespaceAndPath("dbright", "cactus")).ifPresent(template -> template.placeInWorld(serverLevel, referencePos, referencePos, new StructurePlaceSettings(), soulEnderman.getRandom(), Block.UPDATE_ALL));
                }
                killed=true;
            }
        }
        bossBarTick(soulEnderman);
        if(soulified)
        {
            particleAtEntityOffsetToAllPlayers(soulEnderman, new Vec3(0,1.5,0), ParticleTypes.SCULK_SOUL,0.25f,1f,0.25f,1,0);
        }

        switch(endFightPhase)
        {
            case DORMANT -> dormantPhase(soulEnderman);
            case START -> startPhase(soulEnderman);
            case TELEPORTING -> teleportingPhase(soulEnderman);
            case BROODING -> broodingPhase(soulEnderman);
            case SALMON -> salmonPhase(soulEnderman);
            case SIFTERS -> sifterPhase(soulEnderman);
            case ZOMBIE -> zombiePhase(soulEnderman);
            case ATTACKING -> attackingPhase(soulEnderman);
            case CREEPER -> creeperPhase(soulEnderman);

        }
        phaseDuration++;
    }

    public static void bossBarTick(Enderman soulEnderman)
    {
        if (!(soulEnderman.level() instanceof ServerLevel level)) return;
        if (bossBar == null) {
            bossBar = new ServerBossEvent(
                    UUID.randomUUID(),
                    Component.literal("Enderman"),
                    BossEvent.BossBarColor.PURPLE,
                    BossEvent.BossBarOverlay.PROGRESS
            );
        }

        if(soulified)
        {
            bossBar.setColor(BossEvent.BossBarColor.BLUE);
            bossBar.setName(Component.literal("Soul Coderotted Enderman"));
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
        soulEnderman.setNoAi(false);
        soulEnderman.setPermanentlyInvulnerable(false);
        soulEnderman.setNoGravity(false);
        if(phaseDuration <= 1)
        {
            for(Player player : soulEnderman.level().players())
            {
                player.addEffect( new MobEffectInstance(MobEffects.BLINDNESS, 50, 100,false,false));
            }
        }
        if(soulEnderman.tickCount % 10 == 0)
        {
            playSoundAtEntity(soulEnderman,SoundEvents.ENDERMAN_TELEPORT,100,1);
            randomTeleport(soulEnderman,20);
        }

        if(phaseDuration > TeleportingPhaseLength)
        {
            setPhase(EndFightPhase.ATTACKING);
        }
    }

    public static void attackingPhase(Enderman soulEnderman)
    {
        soulEnderman.setNoAi(false);
        soulEnderman.setPermanentlyInvulnerable(false);
        if(phaseDuration % endermanTargetSwitchTime == 0 || !phaseSetUp)
        {
            teleportToVictim(soulEnderman);
        }
        phaseSetUp = true;
        if(phaseDuration > AttackPhaseLength)
        {
            soulEnderman.setTarget(null);
            setPhase(getBroodPhase());
        }
    }

    public static void dormantPhase(Enderman soulEnderman)
    {
        MinecraftServer server = soulEnderman.level().getServer();
        if(server == null)
        {
            return;
        }
        PlayerList playerTabList = server.getPlayerList();
        List<ServerPlayer> playerList = playerTabList.getPlayers();
        if(!playerList.isEmpty() && playerList.stream().allMatch(player -> player.distanceToSqr(soulEnderman) <= 100) && !begun)
        {
            soulEnderman.setGlowingTag(false);
            playSoundAtEntity(soulEnderman, SoundEvents.ENDERMAN_STARE, 100, 1);
            setPhase(EndFightPhase.BROODING);
        }
    }

    public static void teleportToVictim(Enderman soulEnderman)
    {
        playSoundAtEntity(soulEnderman,SoundEvents.ENDERMAN_TELEPORT,100,1);
        soulEnderman.setNoGravity(false);
        Player player = getRandomVictim((ServerLevel)soulEnderman.level());
        if(player != null)
        {
            soulEnderman.teleport(player.getX()+2,100,player.getZ()+2);
            soulEnderman.setTarget(player);
        }
        else
        {
            soulEnderman.teleport(0,100,0);
        }
    }

    public static Player getRandomVictim(ServerLevel level)
    {
        List<ServerPlayer> players = level.players().stream().filter(p -> p.position().length() < 100).toList();
        return players.isEmpty() ? null : players.get(level.getRandom().nextInt(players.size()));

    }

    public static void broodingPhase(Enderman soulEnderman)
    {
        int broodTime = 300;
        double t = Math.clamp(((double)phaseDuration/(double)broodTime),0,1);
        double yLerp = Mth.lerp(t,startPos.y(),broodPos.y());
        soulEnderman.setPos(0.5,yLerp,0.5);
        soulEnderman.setNoGravity(true);
        if(phaseDuration > broodTime)
        {
            setPhase(EndFightPhase.START);
        }
    }

    public static void startPhase(Enderman soulEnderman)
    {
        int particleTime = 120;
        int startTime = 240;
        soulEnderman.setNoGravity(true);
        soulEnderman.setPos(broodPos);
        soulEnderman.setHealth((int)Math.clamp(endermanMaxHealth*((double)phaseDuration/(double)particleTime),1,endermanMaxHealth));
        if(phaseDuration < particleTime) {
            soulified = false;
            particleAtEntityToAllPlayers(soulEnderman, new TrailParticleOption(soulEnderman.position().add(0, 1, 0), 0x00FFFF, 10), 20, 20, 20, 30, 0);
            if(phaseDuration % 5 == 0)
            {
                playSoundAtEntity(soulEnderman, SoundEvents.SOUL_ESCAPE.value(), 100, 1);
            }
        }
        if(phaseDuration == particleTime)
        {
            particleAtEntityToAllPlayers(soulEnderman, ParticleTypes.SONIC_BOOM, 8, 8, 8, 30, 0);
        }
        if(phaseDuration == particleTime+10)
        {
            soulEnderman.setGlowingTag(false);
            playSoundAtEntity(soulEnderman, SoundEvents.WARDEN_SONIC_BOOM, 100, 1);
            playSoundAtEntity(soulEnderman, SoundEvents.ENDERMAN_STARE, 100, 1);
            soulified = true;
        }
        if(phaseDuration > particleTime && !soulified)
        {
            particleAtEntityOffsetToAllPlayers(soulEnderman, new Vec3(0,1.5,0), ParticleTypes.SCULK_SOUL,0.25f,1f,0.25f,1,0);
        }
        if(phaseDuration > startTime)
        {
            for(Player player : soulEnderman.level().players())
            {
                player.addEffect( new MobEffectInstance(MobEffects.BLINDNESS, 100, 100));
            }
            setPhase(EndFightPhase.TELEPORTING);
        }

    }
    public static void salmonPhase(Enderman soulEnderman)
    {
        soulEnderman.setNoAi(true);
        soulEnderman.setPermanentlyInvulnerable(true);
        int axolotlSummonTime = 200;
        int fireflyTime = 120;
        int axolotlAirTime = 400;
        if(!phaseSetUp)
        {
            playSoundAtEntity(soulEnderman,SoundEvents.ENDERMAN_TELEPORT,100,1);
            phaseNum++;
            phaseSetUp = true;
        }
        soulEnderman.setPos(broodPos);
        soulEnderman.setNoGravity(true);
        if(phaseDuration < fireflyTime)
        {
            particleAtEntityToAllPlayers(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0xFFFFAA, 10), 10, 10, 10,10, 0);
            playSoundAtEntity(soulEnderman, SoundEvents.FIREFLY_BUSH_IDLE,100,1);
            if(phaseDuration > 40)
            {
                particleAtEntityOffsetToAllPlayers(soulEnderman, new Vec3(0,-15,0),ParticleTypes.FIREFLY, 50,10,50,100,0);
            }
            if(phaseDuration > 100)
            {
                for (Player player : soulEnderman.level().players())
                {
                    GlowflySystem.setInfected(player,(axolotlSummonTime - fireflyTime) + (axolotlAirTime + 140));
                }
            }
        }
        else if(phaseDuration < axolotlSummonTime-10)
        {
            if(phaseDuration % 5 == 0)
            {
                playSoundAtEntity(soulEnderman, SoundEvents.SOUL_ESCAPE.value(), 100, 1);
            }
            particleAtEntityToAllPlayers(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0xFF0000, 10), 10, 10, 10,5, 0);
            particleAtEntityToAllPlayers(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0x00FF00, 10), 10, 10, 10,5, 0);

        }
        if(phaseDuration == axolotlSummonTime)
        {
            spawnCircle(soulEnderman, List.of(EntityTypes.AXOLOTL),40,1);

        }
        if(phaseDuration > axolotlSummonTime + axolotlAirTime + 140)
        {
            for(Axolotl axolotl : ((ServerLevel)soulEnderman.level()).getEntities(EntityTypes.AXOLOTL,(_)-> true))
            {
                axolotl.kill((ServerLevel)axolotl.level());
            }
            setPhase(EndFightPhase.TELEPORTING);
        }
    }

    public static void zombiePhase(Enderman soulEnderman)
    {
        soulEnderman.setNoAi(true);
        soulEnderman.setPermanentlyInvulnerable(true);
        int zombieSummonTime = 40;
        int zombieKillTime = 300;
        if(!phaseSetUp)
        {
            playSoundAtEntity(soulEnderman,SoundEvents.ENDERMAN_TELEPORT,100,1);
            phaseNum++;
            phaseSetUp = true;
        }
        soulEnderman.setPos(broodPos);
        soulEnderman.setNoGravity(true);
        if(phaseDuration < zombieSummonTime-10)
        {
            if(phaseDuration % 5 == 0)
            {
                playSoundAtEntity(soulEnderman, SoundEvents.SOUL_ESCAPE.value(), 100, 1);
            }
            particleAtEntityToAllPlayers(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0x00AA00, 10), 15, 15, 15,10, 0);
        }
        if(phaseDuration == zombieSummonTime)
        {
            spawnCircle(soulEnderman, List.of(EntityTypes.ZOMBIE,EntityTypes.PHANTOM),10,1);

        }
        for(Zombie zombie : ((ServerLevel)soulEnderman.level()).getEntities(EntityTypes.ZOMBIE,(_)-> true))
        {
            particleAtEntityOffsetToAllPlayers(zombie,new Vec3(0,1,0),new TrailParticleOption(soulEnderman.position().add(0,1,0), 0x00FFFF, 10), 0.1f, 0.5f, 0.1f,10, 0);
        }
        if(phaseDuration > zombieKillTime)
        {
            for(Zombie zombie : ((ServerLevel)soulEnderman.level()).getEntities(EntityTypes.ZOMBIE,(_)-> true))
            {
                zombie.discard();
            }
            setPhase(EndFightPhase.TELEPORTING);
        }
    }
    public static void creeperPhase(Enderman soulEnderman)
    {
        soulEnderman.setNoAi(true);
        soulEnderman.setPermanentlyInvulnerable(true);
        int creeperSummonTime = 40;
        if(!phaseSetUp)
        {
            playSoundAtEntity(soulEnderman,SoundEvents.ENDERMAN_TELEPORT,100,1);
            phaseNum++;
            phaseSetUp = true;
        }
        soulEnderman.setPos(broodPos);
        soulEnderman.setNoGravity(true);
        if(phaseDuration < creeperSummonTime-10)
        {
            if(phaseDuration % 5 == 0)
            {
                playSoundAtEntity(soulEnderman, SoundEvents.SOUL_ESCAPE.value(), 100, 1);
            }
            particleAtEntityToAllPlayers(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0x00FF00, 10), 15, 15, 15,5, 0);
            particleAtEntityToAllPlayers(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0xFFFFFF, 10), 15, 15, 15,5, 0);

        }
        if(phaseDuration == creeperSummonTime)
        {
            spawnCircle(soulEnderman, List.of(EntityTypes.CREEPER,EntityTypes.SKELETON),10,1);
        }

        boolean hasCreeper = false;
        for(Creeper creeper : ((ServerLevel)soulEnderman.level()).getEntities(EntityTypes.CREEPER,(_)-> true))
        {
            hasCreeper = true;
            particleAtEntityOffsetToAllPlayers(creeper,new Vec3(0,1,0),new TrailParticleOption(soulEnderman.position().add(0,1,0), 0x00FFFF, 10), 0.1f, 0.5f, 0.1f,10, 0);
        }

        if(phaseDuration > creeperSummonTime + 10)
        {
            if(!hasCreeper)
            {
                for(Skeleton skeleton : ((ServerLevel)soulEnderman.level()).getEntities(EntityTypes.SKELETON,(_)-> true))
                {
                    skeleton.discard();
                }
                setPhase(EndFightPhase.TELEPORTING);
            }
        }
    }


    public static void sifterPhase(Enderman soulEnderman)
    {
        soulEnderman.setNoAi(true);
        soulEnderman.setPermanentlyInvulnerable(true);
        int creeperSummonTime = 40;
        if(!phaseSetUp)
        {
            playSoundAtEntity(soulEnderman,SoundEvents.ENDERMAN_TELEPORT,100,1);
            phaseNum++;
            phaseSetUp = true;
        }
        soulEnderman.setPos(broodPos);
        soulEnderman.setNoGravity(true);
        if(phaseDuration < creeperSummonTime-10)
        {
            if(phaseDuration % 5 == 0)
            {
                playSoundAtEntity(soulEnderman, SoundEvents.SOUL_ESCAPE.value(), 100, 1);
            }
            particleAtEntityToAllPlayers(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0x00FFFF, 10), 15, 15, 15,5, 0);
            particleAtEntityToAllPlayers(soulEnderman,new TrailParticleOption(soulEnderman.position().add(0,1,0), 0xFFFF00, 10), 15, 15, 15,5, 0);

        }
        if(phaseDuration == creeperSummonTime)
        {
            spawnCircle(soulEnderman, List.of(EntityTypes.SILVERFISH,EntityTypes.BREEZE),16,1);
        }

        boolean hasSentinel = false;
        for(Breeze breeze : ((ServerLevel)soulEnderman.level()).getEntities(EntityTypes.BREEZE,(_)-> true))
        {
            hasSentinel = true;
            particleAtEntityOffsetToAllPlayers(breeze,new Vec3(0,0.275,0),new TrailParticleOption(soulEnderman.position().add(0,1,0), 0x00FFFF, 10), 0.1f, 0.1f, 0.1f,10, 0);
        }

        if(phaseDuration > creeperSummonTime + 10)
        {
            if(!hasSentinel)
            {
                for(Silverfish silverfish : ((ServerLevel)soulEnderman.level()).getEntities(EntityTypes.SILVERFISH,(_)-> true))
                {
                    silverfish.discard();
                }
                setPhase(EndFightPhase.TELEPORTING);
            }
        }
    }





    public static <T extends EntityType<?>> void spawnCircle(Entity sourceEntity, List<T> entityTypes, int amount, double speed)
    {
        for(int i = 0; i < amount; i++)
        {
            LivingEntity entity;
            entity = (LivingEntity)entityTypes.get(i%(entityTypes.size())).create(sourceEntity.level(),EntitySpawnReason.COMMAND);
            if(entity == null)
            {
                break;
            }
            entity.setPos(sourceEntity.position());
            double angle = i*(Math.PI*2)/(amount);
            entity.setDeltaMovement(speed * Math.cos(angle), 0, speed * Math.sin(angle));
            changeAttribute(entity, Attributes.SAFE_FALL_DISTANCE,100);
            sourceEntity.level().addFreshEntity(entity);
        }
    }

    public static void setPhase(EndFightPhase phase)
    {
        phaseDuration = 0;
        endFightPhase = phase;
        phaseSetUp = false;
    }

    public static EndFightPhase getBroodPhase()
    {
        EndFightPhase phase;
        switch (phaseNum % 4)
        {
            case 0 -> phase = EndFightPhase.ZOMBIE;
            case 1 -> phase = EndFightPhase.CREEPER;
            case 2 -> phase = EndFightPhase.SALMON;
            default -> phase = EndFightPhase.SIFTERS;
        }
        return phase;
    }

    public static void randomTeleport(Enderman soulEnderman, int radius)
    {
        soulEnderman.randomTeleport(soulEnderman.getRandom().nextIntBetweenInclusive(-radius,radius),90, soulEnderman.getRandom().nextIntBetweenInclusive(-radius,radius), true, BlockTags.ENDERMAN_DOES_NOT_TELEPORT_TO);
    }

    public enum EndFightPhase {
        DORMANT,
        START,
        TELEPORTING,
        SALMON,
        ZOMBIE,
        ATTACKING,
        END,
        SIFTERS,
        CREEPER,
        BROODING,
    }

}
