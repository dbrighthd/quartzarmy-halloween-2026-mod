package dbrighthd.qahalloween2026.attachments;

public record TimerData(int ticksLeft) {
    public static final TimerData defaultState = new TimerData(0);
}

