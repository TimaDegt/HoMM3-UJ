package io.github.heroes.model.state;

public record Position(int x, int y) {
    public Position[] neighbors() {
        if (y % 2 == 0) {
            return new Position[] {
                new Position(x + 1, y),
                new Position(x - 1, y),
                new Position(x, y + 1),
                new Position(x - 1, y + 1),
                new Position(x, y - 1),
                new Position(x - 1, y - 1)
            };
        }

        return new Position[] {
            new Position(x + 1, y),
            new Position(x - 1, y),
            new Position(x + 1, y + 1),
            new Position(x, y + 1),
            new Position(x + 1, y - 1),
            new Position(x, y - 1)
        };
    }
}
