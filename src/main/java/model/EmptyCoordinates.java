package model;

import java.util.ArrayList;
import java.util.List;

public class EmptyCoordinates
{
    private List<Coordinates> jungleCoordinates = new ArrayList<>();
    private List<Coordinates> notJungleCoordinates = new ArrayList<>();

    private final int jungleWidth;
    private final int jungleHeight;
    private final Coordinates jungleStart;
    private final int mapWidth;
    private final int mapHeight;
    private final List<Coordinates> animalCoordinates;
    private final List<Coordinates> plantCoordinates;

    public EmptyCoordinates(int mapWidth, int mapHeight, int jungleWidth, int jungleHeight, List<Coordinates> animalCoordinates, List<Coordinates> plantCoordinates)
    {
        this.jungleHeight = jungleHeight;
        this.jungleWidth = jungleWidth;
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.animalCoordinates = animalCoordinates;
        this.plantCoordinates = plantCoordinates;

        jungleStart = new Coordinates((mapWidth - jungleWidth) / 2, (mapHeight - jungleHeight) / 2);

        init();
    }

    private boolean isInJungle(Coordinates coordinates)
    {
        int x = coordinates.getX();
        int y = coordinates.getY();

        if (x >= jungleStart.getX() && x < jungleStart.getX() + jungleWidth)
        {
            if (y >= jungleStart.getY() && y < jungleStart.getY() + jungleHeight)
            {
                return true;
            }
        }

        return false;
    }

    private void init()
    {
        for (int i = 0; i < mapWidth; i++)
        {
            for (int j = 0; j < mapHeight; j++)
            {
                Coordinates newCoordinates = new Coordinates(i, j);
                if (!isOccupied(newCoordinates))
                {
                    if (isInJungle(newCoordinates))
                    {
                        jungleCoordinates.add(newCoordinates);
                    }
                    else
                    {
                        notJungleCoordinates.add(newCoordinates);
                    }
                }
            }
        }
    }

    private boolean isOccupied(Coordinates coordinates)
    {
        if (animalCoordinates.contains(coordinates))
        {
            return true;
        }
        if (plantCoordinates.contains(coordinates))
        {
            return true;
        }
        return false;
    }

    public void addAsEmpty(Coordinates coordinates)
    {
        if (isInJungle(coordinates))
        {
            jungleCoordinates.add(coordinates);
        }
        else
        {
            notJungleCoordinates.add(coordinates);
        }
    }

    public void removeFromEmpty(Coordinates coordinates)
    {
        if (isInJungle(coordinates))
        {
            jungleCoordinates.remove(coordinates);
        }
        else
        {
            notJungleCoordinates.remove(coordinates);
        }
    }

    public List<Coordinates> getJungleCoordinates() {
        return jungleCoordinates;
    }

    public List<Coordinates> getNotJungleCoordinates() {
        return notJungleCoordinates;
    }
}
