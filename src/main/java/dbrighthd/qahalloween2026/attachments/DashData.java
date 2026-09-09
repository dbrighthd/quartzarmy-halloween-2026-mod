package dbrighthd.qahalloween2026.attachments;

public record DashData(int dashState, int dashCooldown) {
    public static final DashData defaultState = new DashData(0,0);
}
