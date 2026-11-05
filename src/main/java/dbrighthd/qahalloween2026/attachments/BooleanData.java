package dbrighthd.qahalloween2026.attachments;

public record BooleanData(boolean value) {
    public static final BooleanData defaultState = new BooleanData(false);
}

