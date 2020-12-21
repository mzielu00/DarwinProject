package model;

import java.util.Objects;

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinates addCoordinates(Coordinates otherCoordinates)
    {
        return new Coordinates(getX() + otherCoordinates.getX(), getY() + otherCoordinates.getY());
    }

    public Coordinates convertIfWrongValues(int mapWidth, int mapHeight) {
        if (getX() < 0) {
            x = x + mapWidth;
        }

        if (getX() > mapWidth - 1) {
            x = x - mapWidth;
        }

        if (getY() < 0) {
            y = y + mapHeight;
        }

        if (getY() > mapHeight - 1) {
            y = y - mapHeight;
        }

        return this;
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
