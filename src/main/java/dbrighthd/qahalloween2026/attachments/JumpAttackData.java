package dbrighthd.qahalloween2026.attachments;

public record JumpAttackData(int attacksChained) {
    public static final JumpAttackData defaultState = new JumpAttackData(0);
}
