package dbrighthd.qahalloween2026.bind;

import dbrighthd.qahalloween2026.attachments.BindData;
import dbrighthd.qahalloween2026.attachments.DataAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.Server;

import static dbrighthd.qahalloween2026.utils.PlayerMethodUtils.playSoundAtPlayer;

public class BindSystem {
    public final static int BIND_COOLDOWN = 0;
    public final static int BIND_TIME = 27;
    public final static int STRINGS_PER_BIND = 64;
    public static void bindTick(ServerPlayer player)
    {
        BindData data = player.getAttachedOrCreate(DataAttachments.BIND_DATA);
        inBind(player,data);
    }

    public static void inBind(ServerPlayer player, BindData data)
    {

        int updatedState = data.bindState();
        int updatedCooldown = data.bindCooldown();
        if(updatedCooldown == 0 && data.bindState() == 0)
        {
            if(player.isShiftKeyDown() && canBind(player))
            {
                ContainerHelper.clearOrCountMatchingItems(player.getInventory(), stack -> stack.is(Items.STRING), 64, false);
                updatedState = BIND_TIME;
                player.sendOverlayMessage(Component.literal("Binding..."));
                playSoundAtPlayer(player, SoundEvents.WIND_CHARGE_BURST.value(),1.0f,0.5f);
            } else if (player.isShiftKeyDown()) {
                player.sendOverlayMessage(Component.literal("Cannot bind yet!"));
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
                player.setHealth(player.getHealth() + 6);
                player.sendOverlayMessage(Component.literal("Binding Complete!"));

                player.resetFallDistance();
                playSoundAtPlayer(player, SoundEvents.BAT_TAKEOFF,1.0f,1f);
                updatedCooldown = BIND_COOLDOWN;
            }
            updatedState--;
        }
        player.setAttached(DataAttachments.BIND_DATA, new BindData(updatedState,updatedCooldown));
    }

    public static boolean canBind(ServerPlayer player)
    {
        return (player.getInventory().countItem(Items.STRING) >= 64);
    }

    public static void cancelBind(ServerPlayer player)
    {
        BindData data = player.getAttachedOrCreate(DataAttachments.BIND_DATA);
        if(data.bindState() == 0)
        {
            return;
        }
        player.sendOverlayMessage(Component.literal("Binding Cancelled!"));
        playSoundAtPlayer(player, SoundEvents.ITEM_BREAK.value(),1.0f,1f);
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
