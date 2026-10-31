package dbrighthd.qahalloween2026.attachments;

public record ChainedActionData(int chainedAmount) {
    public static final ChainedActionData defaultState = new ChainedActionData(0);
}
