package dbrighthd.qahalloween2026.attachments;

public record GlowflyData(int ticksLeft) {
    public static final GlowflyData defaultState = new GlowflyData(0);
}
