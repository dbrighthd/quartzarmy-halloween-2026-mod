package dbrighthd.qahalloween2026.systems;

import dbrighthd.qahalloween2026.attachments.BindData;
import dbrighthd.qahalloween2026.attachments.DataAttachments;
import dbrighthd.qahalloween2026.mainloop.Config;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import static dbrighthd.qahalloween2026.mainloop.Config.*;
import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.*;

public class BindSystem {

    public static void bindTick(ServerPlayer player)
    {
        if(player == null || !player.gameMode.isSurvival())
        {
            return;
        }
        BindData data = player.getAttachedOrCreate(DataAttachments.BIND_DATA);
        inBind(player,data);
    }

    public static void inBind(ServerPlayer player, BindData data)
    {

        int updatedState = data.bindState();
        int updatedCooldown = data.bindCooldown();
        if(updatedCooldown == 0 && data.bindState() == 0)
        {
            if(player.isShiftKeyDown() && canBind(player) && !(player.onGround() && player.getHealth() == 20))
            {
                ContainerHelper.clearOrCountMatchingItems(player.getInventory(), stack -> stack.is(Items.STRING), 64, false);
                updatedState = bindTimeTicks;
                player.sendOverlayMessage(Component.literal("Binding..."));
                particleAtEntity(player,new TrailParticleOption(player.position().add(0,1,0), 0xFFFFFF, bindTimeTicks), 1, 1, 1,100, 0);

                playSoundAtEntity(player, SoundEvents.WIND_CHARGE_BURST.value(),1.0f,0.5f);
            } else if (player.isShiftKeyDown()) {
                if (!canBind(player)) {
                    player.sendOverlayMessage(Component.literal("Cannot bind yet!"));
                }
                if ((player.onGround() && player.getHealth() == 20)) {
                    player.sendOverlayMessage(Component.literal("Must be in the air to bind while at full health!"));
                }
            }
        }
        if(updatedCooldown > 0)
        {
            updatedCooldown--;
        }
        if(updatedState > 0)
        {
            player.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), Vec3.ZERO));
            if(updatedState == 1)
            {
                player.setHealth(player.getHealth() + Config.healthPerBind);
                player.sendOverlayMessage(Component.literal("Binding Complete!"));
                grantAdvancement(player,"main/string");
                player.resetFallDistance();
                playSoundAtEntity(player, SoundEvents.BAT_TAKEOFF,1.0f,1f);
                updatedCooldown = bindCooldownTicks;
            }
            updatedState--;
        }
        player.setAttached(DataAttachments.BIND_DATA, new BindData(updatedState,updatedCooldown));
    }

    public static boolean canBind(ServerPlayer player)
    {
        return (player.getInventory().countItem(Items.STRING) >= stringRequiredForBind);
    }

    public static boolean isInBind(ServerPlayer player)
    {
        return player.getAttachedOrCreate(DataAttachments.BIND_DATA).bindState() > 0;
    }
    public static void cancelBind(ServerPlayer player)
    {
        BindData data = player.getAttachedOrCreate(DataAttachments.BIND_DATA);
        if(data.bindState() == 0)
        {
            return;
        }
        grantAdvancement(player,"main/failbind");
        player.sendOverlayMessage(Component.literal("Binding Cancelled!"));
        playSoundAtEntity(player, SoundEvents.ITEM_BREAK.value(),1.0f,1f);
        player.setAttached(DataAttachments.BIND_DATA, BindData.defaultState);
    }

    public static void giveString(ServerPlayer serverPlayer, int amount)
    {
        if(serverPlayer == null)
        {
            return;
        }
        serverPlayer.getInventory().add(new ItemStack(Items.STRING, amount));
    }

}
