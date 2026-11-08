package dbrighthd.qahalloween2026.mainloop;

import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.attachments.TimerData;
import dbrighthd.qahalloween2026.systems.BindSystem;
import dbrighthd.qahalloween2026.systems.GlowflySystem;
import dbrighthd.qahalloween2026.systems.WetIchorSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerRotationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static dbrighthd.qahalloween2026.mainloop.TickLoop.hideEntityFromPlayer;
import static dbrighthd.qahalloween2026.utils.GeneralUtils.changeAttribute;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.getSunPitch;

public class PlayerSpecificStuff {


    public static float qeuedCherryDamage = 0;
    public static  boolean mirrorDamage = false;
    public static DamageSource queuedSource;
    public static void tickPlayerSpecifics(ServerPlayer player) {
        Set<String> tags = player.entityTags();
        if(tags.isEmpty())
        {
            return;
        }
        GlobalVars.tudad = null;
        boolean isEntraBit = false;
        for(String tag : tags)
        {
            if(tag.equals(MeanTag.BugPuncher.name()))
            {
                bugpuncherTick(player);
            }
            else if(tag.equals(MeanTag.EntraBit.name()))
            {
                isEntraBit = true;
                entraBitTick(player);
            }
            else if(tag.equals(MeanTag.Moth.name()))
            {
                mothTick(player);
            }
            else if(tag.equals(MeanTag.Tudad.name()))
            {
                tudadTick(player);
            }
            else if(tag.equals(MeanTag.CherryBee.name()))
            {
                cherryTick(player);
            }
        }
        if(!isEntraBit)
        {
            unEntraBit(player);
        }
    }

    public static void mothTick(ServerPlayer moth)
    {
        int timer = moth.getAttachedOrCreate(DataAttachments.INPUT_TIMER_DATA).ticksLeft();
        Input input = moth.getLastClientInput();
        boolean moving = input.forward() || input.backward() || input.left() || input.right() || input.jump();
        if(moving)
        {
            timer = 80;
        }
        if(timer >= 0)
        {
            timer--;
        }
        else {
            long time = moth.level().getOverworldClockTime()%24000L;
            if(!(time > 12000))
            {
                float midYaw = time <= 6000 ? -90 : 90;
                float yawDiff = Mth.wrapDegrees(midYaw - moth.getYRot());
                float pitchDiff = getSunPitch(time) - moth.getXRot();

                float distance = Mth.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);

                if (distance > 0.001f) {
                    float step = Math.min(2f, distance) / distance;

                    float yawStep = yawDiff * step;
                    float pitchStep = pitchDiff * step;

                    moth.setYRot(moth.getYRot() + yawStep);
                    moth.setXRot(moth.getXRot() + pitchStep);

                    moth.connection.send(
                            new ClientboundPlayerRotationPacket(yawStep, true, pitchStep, true)
                    );
                }
            }
        }
        moth.setAttached(DataAttachments.INPUT_TIMER_DATA,new TimerData(timer));
    }

    public static void entraBitTick(ServerPlayer entraBit)
    {
        entraBit.addPostEffect(Identifier.parse("minecraft:creeper"));
    }

    public static void unEntraBit(ServerPlayer notEntraBit)
    {
        notEntraBit.removePostEffect(Identifier.parse("minecraft:creeper"));
    }
    public static void bugpuncherTick(ServerPlayer player)
    {
        double overshootAmount = 4;
        int round = 16;
        BlockPos playerAbovePos = player.getOnPos().above(16);
        Vec3 playerSpotlightPos = new Vec3(((int)(Math.round(playerAbovePos.getX()/(double)round)*round)),((int)(Math.round(playerAbovePos.getY()/(double)round)*round)),(int)(Math.round(playerAbovePos.getZ()/(double)round)*round));

        Vec3 targetPosSub = player.position().subtract(playerSpotlightPos);
        double dist = player.position().distanceTo(playerSpotlightPos);

        Vec3 targetPos = playerSpotlightPos.add(targetPosSub.normalize().scale(dist+overshootAmount));

        int color = -1;

        if(BindSystem.isInBind(player))
        {
            color = 1;
        }
        if (WetIchorSystem.isPlayerWet(player))
        {
            color = 12;
        }
        if(player.getHealth() <= 3)
        {
            color = (player.tickCount/10 % 2 == 0) ? 1 : 15;
        }
        if (GlowflySystem.isPlayerInfected(player))
        {
            color = (player.tickCount % 2 == 0) ? 15 : 14;
        }
        if(player.isDeadOrDying() || player.wasHurtRecently())
        {
            color = 15;
        }
        if (WetIchorSystem.isPlayerIchorized(player))
        {
            color = (player.tickCount % 2 == 0) ? 1 : 10;
        }
        if (player.isOnFire())
        {
            color = (player.tickCount % 2 == 0) ? 5 : 2;
        }
        if(color > -1)
        {
            SpotLight.ResolvedSpotlight spotlight = new SpotLight.ResolvedSpotlight(1,playerSpotlightPos,targetPos,color,15);
            SpotLight.spawnSpotlightParticles(player.level(),spotlight,100);
        }

    }

    public static boolean isTeighterInGame(ServerPlayer player)
    {
        MinecraftServer server = player.level().getServer();
        PlayerList playerList = server.getPlayerList();
        for(ServerPlayer playerInList : playerList.getPlayers())
        {
            if(playerInList.entityTags().contains(MeanTag.Teighter.name()))
            {
                return true;
            }
        }
        return false;
    }

    public static String getTeighterName(ServerPlayer player)
    {
        MinecraftServer server = player.level().getServer();
        PlayerList playerList = server.getPlayerList();
        for(ServerPlayer playerInList : playerList.getPlayers())
        {
            if(playerInList.entityTags().contains(MeanTag.Teighter.name()))
            {
                return playerInList.getName().getString();
            }
        }
        return "";
    }

    public static void killTeighter(MinecraftServer server)
    {
        PlayerList playerList = server.getPlayerList();
        for(ServerPlayer playerInList : playerList.getPlayers())
        {
            if(playerInList.entityTags().contains(MeanTag.Teighter.name()))
            {
                playerInList.kill(playerInList.level());
            }
        }
    }

    public static void tudadTick(ServerPlayer player)
    {
        GlobalVars.tudad = player;
    }

    public static void cherryTick(ServerPlayer player)
    {
        List<Mannequin> cherries = getCherry(player.level().getServer());
        if (cherries.size() > 1) {
            for (int i = 1; i < cherries.size(); i++) {
                cherries.get(i).discard();
            }
        }
        if(cherries.isEmpty())
        {
            cherries.add((Mannequin)spawnCherry(player,player.level()));
            Mannequin cherryCloneTemp = cherries.getFirst();
            changeAttribute(cherryCloneTemp, Attributes.MAX_HEALTH,100);
            cherryCloneTemp.setHealth(100);
        }
        Mannequin cherryClone = cherries.getFirst();
        teleportCherryCloneToPlayer(player,cherryClone);
        cherryClone.setPose(player.getPose());
        int whileCount = 0;
        if(player.onGround() && !player.level().getBlockState(BlockPos.containing(cherryClone.position().subtract(0,0.06,0))).is(BlockTags.BLOCKS_MOTION) && !player.level().getBlockState(cherryClone.blockPosition()).is(BlockTags.BLOCKS_MOTION))
        {
            while (canEntityFit(player.getPose(),cherryClone) && whileCount < 32)
            {
                cherryClone.teleportTo(cherryClone.getX(),cherryClone.getY()-0.0625,cherryClone.getZ());
                whileCount++;
            }
        }
        while (!canEntityFit(player.getPose(),cherryClone)&& whileCount < 32)
        {
            cherryClone.teleportTo(cherryClone.getX(),cherryClone.getY()+0.0625,cherryClone.getZ());
            whileCount++;
        }
        if(!canEntityFit(player.getPose(),cherryClone))
        {
            cherryClone.setPermanentlyInvulnerable(true);
        }
        else
        {
            cherryClone.setPermanentlyInvulnerable(false);
        }
        syncItemSlot(cherryClone,player,EquipmentSlot.MAINHAND);
        syncItemSlot(cherryClone,player,EquipmentSlot.OFFHAND);
        syncItemSlot(cherryClone,player,EquipmentSlot.HEAD);
        syncItemSlot(cherryClone,player,EquipmentSlot.CHEST);
        syncItemSlot(cherryClone,player,EquipmentSlot.LEGS);
        syncItemSlot(cherryClone,player,EquipmentSlot.FEET);
        syncItemSlot(cherryClone,player,EquipmentSlot.SADDLE);
        cherryClone.setMainArm(player.getMainArm());
        changeAttribute(cherryClone, Attributes.MAX_HEALTH,100);
        cherryClone.setHealth(100);

        if (player.isSwinging() && !cherryClone.isSwinging())
        {
            cherryClone.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT,true);
        }
        if(mirrorDamage)
        {
            if(player.wasHurtRecently() && !cherryClone.wasHurtRecently())
            {
                cherryClone.hurtServer(player.level(),player.level().damageSources().generic(),0);
            }
        }
        if(qeuedCherryDamage != 0 && queuedSource != null)
        {
            player.hurtServer(player.level(),queuedSource,qeuedCherryDamage);
        }
        mirrorDamage = false;
        qeuedCherryDamage = 0;
    }

    public static void mirrorCherryDamage()
    {
        mirrorDamage = true;
    }

    public static void syncItemSlot(Mannequin mannequin, ServerPlayer player, EquipmentSlot slot)
    {
        mannequin.setItemSlot(slot,player.getItemBySlot(slot));
    }

    public static boolean canEntityFit(final Pose newPose, LivingEntity entity) {
        return entity.level().noCollision(entity, entity.getDimensions(newPose).makeBoundingBox(entity.position()).deflate(1.0E-7));
    }

    public static void teleportCherryCloneToPlayer(ServerPlayer player, Mannequin cherryClone)
    {
        cherryClone.teleportTo(player.level(),player.getX()+2,player.getY(),player.getZ(),Set.of(),player.getYRot(),player.getXRot(),false);
    }

    public static Entity spawnCherry(ServerPlayer player, ServerLevel level)
    {
        CompoundTag nbt = new CompoundTag();

        CompoundTag profile = new CompoundTag();
        profile.putString("name", "cherrybeeisme");
        nbt.put("profile", profile);
        Entity mannequin = EntityType.loadEntityRecursive(
                EntityTypes.MANNEQUIN,
                nbt, level, EntitySpawnReason.COMMAND,
                entity -> {
                    entity.teleportTo(player.getX() + 2, player.getY(), player.getZ());
                    entity.addTag("cherryClone");
                    return entity;
                }
        );
        level.addFreshEntity(mannequin);
        return mannequin;
    }
    public static List<Mannequin> getCherry(MinecraftServer server)
    {
        List<Mannequin> cherries = new ArrayList<>();
        for(ServerLevel level : server.getAllLevels())
        {
            for (Mannequin mannequin : level.getEntities(
                    EntityTypeTest.forClass(Mannequin.class),
                    entity -> true
            ))
            {
                if(mannequin.entityTags().contains("cherryClone"))
                {
                    cherries.add(mannequin);
                }
            }
        }
        return cherries;
    }

    public enum MeanTag {
        BugPuncher,
        Teighter,
        EntraBit,
        Moth,
        Gold,
        Tudad,
        Ruby,
        CherryBee,
        Byte,
    }


}
