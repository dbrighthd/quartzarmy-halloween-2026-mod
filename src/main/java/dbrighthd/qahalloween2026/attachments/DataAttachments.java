package dbrighthd.qahalloween2026.attachments;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import static dbrighthd.qahalloween2026.Qahalloween2026.MOD_ID;

public class DataAttachments {
    public static final AttachmentType<IchorData> ICHOR_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "ichor_data"),
                    () -> IchorData.defaultState
            );
    public static final AttachmentType<JumpAttackData> JUMP_ATTACK_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "jumpattack_data"),
                    () -> JumpAttackData.defaultState
            );
    public static final AttachmentType<BindData> BIND_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "bind_data"),
                    () -> BindData.defaultState
            );
    public static final AttachmentType<WetData> WET_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "wet_data"),
                    () -> WetData.defaultState
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
    public static final AttachmentType<GlowflyData> GLOWFLY_DATA =
            AttachmentRegistry.createDefaulted(
                    Identifier.fromNamespaceAndPath(MOD_ID, "glowfly_data"),
                    () -> GlowflyData.defaultState
            );
}
