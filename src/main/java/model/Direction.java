package model;

public enum Direction {
    NORTH(0),
    NORTHWEST(1),
    WEST(2),
    SOUTHWEST(3),
    SOUTH(4),
    SOUTHEAST(5),
    EAST(6),
    NORTHEAST(7);

    public final int id;

    Direction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
