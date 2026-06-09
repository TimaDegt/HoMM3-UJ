package io.github.heroes.combat.cursor;

public enum CursorType {
    DEFAULT("Default",0f,0f),
    ATTACKL("AttackLeft", 0f, +5f),
    ATTACKR("AttackRight", -32f, +5f),
    ATTACKLU("AttackUpLeft", 0f, 0f),
    ATTACKRU("AttackUpRight", -22f, 0f),
    ATTACKLD("AttackDownLeft", 0f, +22f),
    ATTACKRD("AttackDownRight", -22f, +22f),
    RANGER("Ranger", -12f,+12f),
    RANGERBROKEN("RangerBroken", -14f, +7f),
    QUESTION("Question", -8f, +13f),
    MOVE("Move",-10f,+13f),
    NOPE("Nope", -13f, +13f),
    NONE("",0f,0f);

    CursorType(String name, float offsetX, float offsetY) {
        this.name = name;
        this.offX = offsetX;
        this.offY = offsetY;
    }

    private String name;
    public final float offX;
    public final float offY;
    public String getName() {
        return name;
    }
}
