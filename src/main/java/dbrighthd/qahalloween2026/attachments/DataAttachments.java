package dbrighthd.qahalloween2026.attachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import static dbrighthd.qahalloween2026.Qahalloween2026.MOD_ID;

public class DataAttachments {
    public static final AttachmentType<DashData> DASH_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "dash_data"),
                    () -> DashData.defaultState
            );

    public static final AttachmentType<BindData> BIND_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "bind_data"),
                    () -> BindData.defaultState
            );
}
