package dbrighthd.qahalloween2026.attachments;

public record BindData(int bindState, int bindCooldown) {
    public static final BindData defaultState = new BindData(0,0);
}
