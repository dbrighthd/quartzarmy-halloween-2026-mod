package dbrighthd.qahalloween2026.attachments;

public record EndRespawnData(int ticksLeft) {
    public static final EndRespawnData defaultState = new EndRespawnData(0);
}

