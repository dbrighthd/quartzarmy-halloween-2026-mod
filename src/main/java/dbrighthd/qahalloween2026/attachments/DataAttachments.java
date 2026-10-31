package dbrighthd.qahalloween2026.attachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import static dbrighthd.qahalloween2026.Qahalloween2026.MOD_ID;

public class DataAttachments {
    public static final AttachmentType<TimerData> ICHOR_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "ichor_data"),
                    () -> TimerData.defaultState
            );
    public static final AttachmentType<ChainedActionData> JUMP_ATTACK_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "jumpattack_data"),
                    () -> ChainedActionData.defaultState
            );
    public static final AttachmentType<BindData> BIND_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "bind_data"),
                    () -> BindData.defaultState
            );
    public static final AttachmentType<TimerData> WET_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "wet_data"),
                    () -> TimerData.defaultState
            );
    public static final AttachmentType<TargetData> TARGET_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "target_data"),
                    () -> TargetData.defaultState
            );
    public static final AttachmentType<SheepData> SHEEP_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "sheep_data"),
                    () -> SheepData.defaultState
            );
    public static final AttachmentType<TimerData> GLOWFLY_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "glowfly_data"),
                    () -> TimerData.defaultState
            );
    public static final AttachmentType<TimerData> ENDRESPAWN_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "endrespawn_data"),
                    () -> TimerData.defaultState
            );
}
