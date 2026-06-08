package io.github.heroes.view;

public class BattleViewConfig {
    public static final float SCREEN_WIDTH_MARGIN = 0.95f;
    public static final float SCREEN_HEIGHT_MARGIN = 0.75f;
    public static final int ACTION_PANEL_QUEUE_BUTTON_COUNT = 15;
    public static final float UNIT_COUNT_BADGE_WIDTH = 34f;
    public static final float UNIT_COUNT_BADGE_HEIGHT = 16f;
    public static final float ACTION_PANEL_BASE_HEIGHT = 42f;
    public static final float ACTION_PANEL_BASE_WIDTH = 800f;
    public static float ACTION_PANEL_SCALE;
    public static float ANIMATION_SPEED = 0.08f;
    public static int MOVEMENT_FRAMES = 12;
    public static float MOVEMENT_SPEED = 1f/(MOVEMENT_FRAMES*1f);
    public static final float CURSOR_SCALE = 2.0f;

    public static final int UNIT_SPRITESHEET_SIZE = 1000;

    public static final float SPELLBOOK_WIDTH = 620f;
    public static final float SPELLBOOK_HEIGHT = 595f;
    public static final float MAGISCHOOL_FRAME_WIDTH = 73f;
    public static final float MAGISCHOOL_FRAME_HEIGHT = 55f;
    public static final float MAGISCHOOL_BANNER_WIDTH = 160f;
    public static final float MAGISCHOOL_BANNER_HEIGHT = 68f;
    public static final float INSIDEBOOK_WIDTH = 200f;
    public static final float INSIDEBOOK_HEIGHT = 325f;
    public static final float INSIDEBOOK_MARGIN_TOP = 25f;
    public static final float INSIDEBOOK_ROW_HEIGHT = 100f;
    public static final float INSIDEBOOK_MARGIN_SIDEWAYS = 18f;

    public static float HEX_SIZE;
    public static float HEX_WIDTH;
    public static float HEX_HEIGHT;
    public static float FIELD_START_X;
    public static float FIELD_START_Y;

    private BattleViewConfig() {
    }

    public static void updateDimensions(int screenWidth, int screenHeight, int gridCols, int gridRows) {
        float totalGridWidthUnits = gridCols + 0.5f;
        float totalGridHeightUnits = 1f + (gridRows - 1) * 0.75f;

        float maxHexWidth = (screenWidth * SCREEN_WIDTH_MARGIN) / totalGridWidthUnits;
        float maxHexHeight = (screenHeight * SCREEN_HEIGHT_MARGIN) / totalGridHeightUnits;

        float maxRadiusByWidth = maxHexWidth / (float) Math.sqrt(3);
        float maxRadiusByHeight = maxHexHeight / 2f;
        HEX_SIZE = Math.min(maxRadiusByWidth, maxRadiusByHeight);

        HEX_WIDTH = HEX_SIZE * (float) Math.sqrt(3);
        HEX_HEIGHT = HEX_SIZE * 2f;

        float totalPixelWidth = totalGridWidthUnits * HEX_WIDTH;
        float totalPixelHeight = totalGridHeightUnits * HEX_HEIGHT;

        ACTION_PANEL_SCALE = screenWidth / ACTION_PANEL_BASE_WIDTH;

        FIELD_START_X = (screenWidth - totalPixelWidth) / 2f;
        FIELD_START_Y = ACTION_PANEL_BASE_HEIGHT * ACTION_PANEL_SCALE + HEX_HEIGHT * 0.5f;//(screenHeight - totalPixelHeight) / 2f + (screenHeight * UI_BOTTOM_MARGIN);
    }
}
