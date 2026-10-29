package dbrighthd.qahalloween2026.attachments;

import java.util.UUID;

public record TargetData(UUID target, SourceType sourceType) {
    public static final TargetData defaultState = new TargetData(new UUID(0,1), SourceType.NORMALMOB);

    public enum SourceType {
        PLAYER,
        SKELETON,
        NORMALMOB
    }
}
