package dbrighthd.qahalloween2026.mainloop;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class Config {
    public static double creeperStartDistance = 12;
    public static double creeperCancelDistance = 16;
    public static double creeperSuckForce = 0.1;

    public static long endIslandRadius = 8192L;

    public static int endRespawnSeconds = 4;

    public static int spikeCount = 30;
    public static double spikeDistance = 100;

    public static final int stringPerDamage = 2;

    public static int tripleJumpGrace = 10;
    public static double tripleJumpBoost = 1.7;

    public final static int bindCooldownTicks = 0;
    public final static int bindTimeTicks = 27;
    public final static int stringRequiredForBind = 64;
    public final static int healthPerBind = 10;

    public static double breakMissChance = 0.2;
    public static double placeMissChance = 0.25;

    public static boolean useHardcodedSeed = true;

    public static int maxWet = 200;
    public static int maxIchor = 100;
    public static double maxSlipperyFriction = 0;
    public static double maxAirSlipperyFriction = 0.4;

    public static double arrowSpeed = 0.1;
    public static double arrowBurstSpeed = 0.6;
    public static int arrowTrackTime = 80;
    public static int arrowLifeTime = 160;
    public static int arrowBurst = 5;

    public static int maxAxolotlsNearby = 16;

    public static int sheepCooldownMax = 360;
    public static int sheepUntilSleep = 4;
    public static int sleepTime = 240;
    public static int stringPerSheep = 32;

    public static int stringPerBlockBreak = 1;

    public static int stackAmounts = 3;

    public static int glowFlyTicks = 600;

    public static int siftParticleAmount = 3;
    public static int siftParticleHorDistance = 24;
    public static int siftParticleVertDistance = 12;
    public static int siftParticleTickMod = 1;

    public static int ichorDamageAmount = 2;
    public  static  int ichorTickMod = 30;
    public  static  int ichorTickModInIchor = 10;

    public static int arrowInterval = 15;

    public static SoundEvent missSound = SoundEvents.PLAYER_BREATH;
}
