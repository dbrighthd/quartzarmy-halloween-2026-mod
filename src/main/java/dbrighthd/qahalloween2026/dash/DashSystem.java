package dbrighthd.qahalloween2026.dash;

import dbrighthd.qahalloween2026.attachments.DashData;
import dbrighthd.qahalloween2026.attachments.DataAttachments;
import net.minecraft.server.level.ServerPlayer;

public class DashSystem {
    public static void dashTick(ServerPlayer player)
    {
        DashData data = player.getAttachedOrCreate(DataAttachments.DASH_DATA);

    }
}
