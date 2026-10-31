package dbrighthd.qahalloween2026.attachments;

public record ChainedActionData(int chainedAmount, int chainTime) {
    public static final ChainedActionData defaultState = new ChainedActionData(0,0);
}
