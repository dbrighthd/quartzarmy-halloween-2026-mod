package dbrighthd.qahalloween2026.dialogs;


import dbrighthd.qahalloween2026.mainloop.EndFight;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dbrighthd.qahalloween2026.mainloop.PlayerSpecificStuff.getTeighterName;
import static dbrighthd.qahalloween2026.mainloop.PlayerSpecificStuff.isTeighterInGame;

public class DeathDialog {

    private static ActionButton button(String label, String id) {
        return new ActionButton(
                new CommonButtonData(Component.literal(label), 150),
                Optional.of(new StaticAction(
                        new ClickEvent.Custom(
                                Identifier.parse("dbright:" + id),
                                Optional.empty()
                        )
                ))
        );
    }

    public static void show(ServerPlayer player) {
        CommonDialogData common = new CommonDialogData(
                Component.literal("Dead"),
                Optional.empty(),
                false,
                false,
                DialogAction.NONE,
                List.of(new PlainMessage(Component.literal("You died!!! what do you want to do"),
                        200)),
                List.of()
        );

        Dialog dialog;
        List<ActionButton> buttons = new ArrayList<>();
        buttons.addLast(button("Teleport to Random Player", "random_player"));
        buttons.addLast(button("Stay at Spawnpoint", "spawnpoint"));
        if(EndFight.soulified)
        {
            buttons.addLast(button("Continue Boss Fight", "continue_fight"));
        }
        if(isTeighterInGame(player))
        {
            buttons.addLast(button("Kill " + getTeighterName(player), "kill_teighter"));
        }
        dialog = new MultiActionDialog(common, buttons, Optional.empty(), 1);

        player.openDialog(Holder.direct(dialog));
    }
}
