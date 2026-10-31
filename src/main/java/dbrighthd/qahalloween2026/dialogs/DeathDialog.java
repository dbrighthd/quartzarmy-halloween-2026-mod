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

import java.util.List;
import java.util.Optional;

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
        if(EndFight.soulified)
        {
            dialog = new MultiActionDialog(
                    common,
                    List.of(
                            button("Random Player", "random_player"),
                            button("Spawnpoint", "spawnpoint"),
                            button("Continue Boss Fight", "continue_fight")
                    ),
                    Optional.empty(),
                    1
            );
        }
        else
        {
           dialog = new MultiActionDialog(
                    common,
                    List.of(
                            button("Random Player", "random_player"),
                            button("Spawnpoint", "spawnpoint")
                    ),
                    Optional.empty(),
                    1
            );
        }


        player.openDialog(Holder.direct(dialog));
    }
}
