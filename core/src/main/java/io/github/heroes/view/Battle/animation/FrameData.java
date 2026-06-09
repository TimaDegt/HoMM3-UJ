package io.github.heroes.view.Battle.animation;

public class FrameData {
    private int x;
    private int y;
    private int size;
    private float dx;
    private float dy;
    private boolean flipX = false;
    private boolean isMoving = false;

    public FrameData(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.dx = 0;
        this.dy = 0;
    }

    public FrameData(int x, int y, int size, float dx, float dy, boolean flipX, boolean isMoving) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.dx = dx;
        this.dy = dy;
        this.flipX = flipX;
        this.isMoving = isMoving;
    }

    public int getX() {return x;}
    public int getY() {return y;}
    public int getSize() {return size;}
    public float getDx() {return dx;}
    public float getDy() {return dy;}
    public boolean isFlipX() {return flipX;}
    public boolean isMoving() {return isMoving;}
}
