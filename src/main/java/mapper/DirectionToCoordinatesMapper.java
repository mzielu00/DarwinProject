package mapper;

import model.Coordinates;
import model.Direction;

public class DirectionToCoordinatesMapper
{
    public Coordinates map(Direction direction)
    {
        return switch (direction)
                {
            case NORTH -> new Coordinates(0, 1);
            case NORTHWEST -> new Coordinates(1, 1);
            case WEST -> new Coordinates(1, 0);
            case SOUTHWEST -> new Coordinates(1, -1);
            case SOUTH -> new Coordinates(0, -1);
            case SOUTHEAST -> new Coordinates(-1, -1);
            case EAST -> new Coordinates(-1, 0);
            case NORTHEAST -> new Coordinates(-1, 1);
        };
    }
    public Coordinates map(Coordinates coordinates, Direction direction) {
        return coordinates.addCoordinates(map(direction));
    }
}
