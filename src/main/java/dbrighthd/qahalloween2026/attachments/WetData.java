package dbrighthd.qahalloween2026.attachments;

public record WetData(int wetState) {
    public static final WetData defaultState = new WetData(0);
}

