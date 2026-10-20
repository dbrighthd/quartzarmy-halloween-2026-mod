package dbrighthd.qahalloween2026.attachments;

public record SheepData(int sheepCount, int sheepCooldown) {
    public static final SheepData defaultState = new SheepData(0,0);}
