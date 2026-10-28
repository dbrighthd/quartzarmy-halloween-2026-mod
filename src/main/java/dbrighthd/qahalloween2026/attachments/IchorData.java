package dbrighthd.qahalloween2026.attachments;

public record IchorData(int ichorState) {
    public static final IchorData defaultState = new IchorData(0);
}
