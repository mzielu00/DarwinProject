package ui;

import model.Animal;
import model.Coordinates;
import model.Plant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ui.MainGui.MAP_SIZE;

public class MapComponent extends JComponent
{
    private final int fieldSize;

    private static final Color PLANT_COLOR = new Color(35, 161, 20);
    private static final Color ANIMAL_COLOR = new Color(85, 18, 18);
    private static final Color OUTSIDE_COLOR = new Color(172, 145, 35);
    private static final Color JUNGLE_COLOR = new Color(187, 93, 9);

    private final int mapWidth;
    private final int mapHeight;
    private final int jungleWidth;
    private final int jungleHeight;

    private List<Animal> animals = new ArrayList<>();
    private List<Plant> plants = new ArrayList<>();

    public MapComponent(int mapWidth, int mapHeight, int jungleWidth, int jungleHeight)
    {
        setSize(MAP_SIZE, MAP_SIZE);
        fieldSize = MAP_SIZE/ Math.min(mapWidth, mapHeight);

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.jungleHeight = jungleHeight;
        this.jungleWidth = jungleWidth;

        if (mapWidth < jungleWidth)
        {
            throw new IllegalArgumentException("Jungle width cannot be bigger than map width");
        }

        if (mapHeight < jungleHeight)
        {
            throw new IllegalArgumentException("Jungle height cannot be bigger than map height");
        }
    }

    @Override
    public void paint(Graphics g)
    {

        g.setColor(OUTSIDE_COLOR);
        g.fillRect(0, 0, mapWidth * fieldSize, mapHeight * fieldSize);

        Coordinates jungleStartCoordinates = calculateJungleStartCoordinates();
        g.setColor(JUNGLE_COLOR);
        g.fillRect(jungleStartCoordinates.getX() * fieldSize, jungleStartCoordinates.getY() * fieldSize, jungleWidth * fieldSize, jungleHeight * fieldSize);

        g.setColor(ANIMAL_COLOR);
        for (Animal animal : animals)
        {
            g.fillRoundRect(
                    animal.getCoordinates().getX() * fieldSize,
                    animal.getCoordinates().getY() * fieldSize,
                    fieldSize,
                    fieldSize,
                    100,
                    100
            );
        }

        g.setColor(PLANT_COLOR);
        for (Plant plant : plants)
        {
            g.fillRoundRect(
                    plant.getCoordinates().getX() * fieldSize,
                    plant.getCoordinates().getY() * fieldSize,
                    fieldSize,
                    fieldSize,
                    100,
                    100
            );
        }
    }

    public void refresh(List<Animal> animals, List<Plant> plants)
    {
        this.animals = animals;
        this.plants = plants;
        repaint();
    }

    private Coordinates calculateJungleStartCoordinates()
    {
        int startX = (mapWidth - jungleWidth) / 2;
        int startY = (mapHeight - jungleHeight) / 2;

        return new Coordinates(startX, startY);
    }
}
