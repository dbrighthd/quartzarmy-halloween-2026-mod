package dbrighthd.qahalloween2026.mainloop;

import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public class SpotLight {

    public static boolean oldWay = true;
    public record ResolvedSpotlight(int index, Vec3 spotlightPos, Vec3 targetPos, int color, double spreadAngle){}

    public static void spawnSpotlightParticles(ServerLevel world, ResolvedSpotlight spotlight, int amount)
    {
        Vec3 start = spotlight.spotlightPos();
        Vec3 end = spotlight.targetPos();

        Vec3 axis = end.subtract(start);
        double length = axis.length();
        Vec3 forward = axis.scale(1.0 / length);

        Vec3 up = Math.abs(forward.y) < 0.99 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
        Vec3 right = forward.cross(up).normalize();
        Vec3 realUp = right.cross(forward).normalize();

        double halfAngleRadians = Math.toRadians(spotlight.spreadAngle() / 2.0);
        if(spotlight.color() == -1)//fishy wanted this for flashing spotlights
        {
            return;
        }
        int rgb = getLegacyWoolRgb(spotlight.color());
        int lifetime = 5;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        if(oldWay)
        {
            for (int i = 0; i < amount; i++) {
                double forwardDistance = random.nextDouble();
                forwardDistance *= length;
                double maxRadius = Math.tan(halfAngleRadians) * forwardDistance;
                double radius = maxRadius;
                double theta = random.nextDouble(Math.PI * 2.0);
                double sideA = Math.cos(theta) * radius;
                double sideB = Math.sin(theta) * radius;
                Vec3 center = start.add(forward.scale(forwardDistance));
                Vec3 radial = right.scale(sideA).add(realUp.scale(sideB));
                Vec3 point = center.add(radial);
                spawnEmissiveParticleOldWay(world, point, rgb, lifetime);
            }
        }
        else //i wanted to see if I could make a less intensive version but it looks bad. might keep this tho
        {
            for (int i = 0; i < amount; i++) {
                double forwardDistance1 = length * random.nextDouble();
                double forwardDistance2 = length * random.nextDouble();
                double maxRadius = Math.tan(halfAngleRadians) * forwardDistance1;
                double radius = maxRadius;
                double theta = random.nextDouble(Math.PI * 2.0);
                double sideA = Math.cos(theta) * radius;
                double sideB = Math.sin(theta) * radius;
                Vec3 center = start.add(forward.scale(forwardDistance1));
                Vec3 radial = right.scale(sideA).add(realUp.scale(sideB));
                Vec3 point = center.add(radial);

                double maxRadius2 = Math.tan(halfAngleRadians) * forwardDistance2;
                double sideA2 = Math.cos(theta) * maxRadius2;
                double sideB2 = Math.sin(theta) * maxRadius2;
                Vec3 center2 = start.add(forward.scale(forwardDistance2));
                Vec3 radial2 = right.scale(sideA2).add(realUp.scale(sideB2));
                Vec3 point2 = center2.add(radial2);
                spawnEmissiveParticleNewWay(world, point2, point, rgb, random.nextInt(5));
            }
        }
    }

    private static void spawnEmissiveParticleOldWay(ServerLevel world, Vec3 position, int rgb, int lifetimeTicks)
    {
        TrailParticleOption trail = new TrailParticleOption(position, rgb, lifetimeTicks);
        world.sendParticles(trail, position.x, position.y, position.z, 1, 0.0, 0.0, 0.0, 0.0);
    }
    private static void spawnEmissiveParticleNewWay(ServerLevel world, Vec3 spotlightPos, Vec3 lightPos, int rgb, int lifetimeTicks)
    {
        TrailParticleOption trail = new TrailParticleOption(lightPos, rgb, lifetimeTicks);
        world.sendParticles(trail, spotlightPos.x, spotlightPos.y, spotlightPos.z, 1, 0.0, 0.0, 0.0, 0.0);
    }
    private static int getLegacyWoolRgb(int color) {
        return switch (color) {
            case 1  -> 0xE9ECEC;
            case 2  -> 0xF07613;
            case 3  -> 0xBD44B3;
            case 4  -> 0x3AAFD9;
            case 5  -> 0xF8C627;
            case 6  -> 0x70B919;
            case 7  -> 0xED8DAC;
            case 8  -> 0x3E4447;
            case 9  -> 0x8E8E86;
            case 10 -> 0x158991;
            case 11 -> 0x792AAC;
            case 12 -> 0x35399D;
            case 13 -> 0x724728;
            case 14 -> 0x546D1B;
            case 15 -> 0xA12722;
            case 16 -> 0x141519;
            default -> 0x000000;
        };
    }
}
