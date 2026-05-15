package io.github.heroes.view;

public class BattleViewConfig {
    public static final float HEX_SIZE = 22f;
    public static final float HEX_WIDTH = HEX_SIZE * (float)Math.sqrt(3);
    public static final float HEX_HEIGHT = HEX_SIZE * 2f;
    public static final float FIELD_START_X = 50f;
    public static final float FIELD_START_Y = 100f;
    public static final float UNIT_RADIUS = HEX_SIZE * 0.45f;

    private BattleViewConfig() {
    }
}
