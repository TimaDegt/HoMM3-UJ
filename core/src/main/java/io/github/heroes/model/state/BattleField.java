package io.github.heroes.model.state;

public class BattleField {
    private final int width;
    private final int height;

    public BattleField(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive");
        }

        this.width = width;
        this.height = height;
    }

    public boolean isInside(Position position) {
        if (position == null) {
            return false;
        }

        return position.x() >= 0
            && position.x() < width
            && position.y() >= 0
            && position.y() < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
