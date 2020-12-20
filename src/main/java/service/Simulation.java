package service;

import mapper.DirectionToCoordinatesMapper;
import model.Animal;
import model.Coordinates;
import model.Genomes;
import model.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation {
    private boolean inProgress = false;
    private SimulationProgressListener simulationProgressListener;
    private final Random random = new Random();
    private final DirectionToCoordinatesMapper directionToCoordinatesMapper;

    private final int jungleWidth;
    private final int jungleHeight;
    private final Coordinates jungleStart;

    private final int mapWidth;
    private final int mapHeight;

    private final int animalsNumber;
    private final int animalsStartingEnergy;

    private final int plantEnergy;

    List<Animal> animalsList = new ArrayList<>();
    List<Plant> plantsList = new ArrayList<>();

    public Simulation(int mapWidth, int mapHeight, int jungleWidth, int jungleHeight, int animalsNumber, int animalsStartingEnergy, int plantEnergy) {
        this.jungleHeight = jungleHeight;
        this.jungleWidth = jungleWidth;
        jungleStart = new Coordinates((mapWidth - jungleWidth) / 2, (mapHeight - jungleHeight) / 2);

        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;

        this.animalsNumber = animalsNumber;
        this.animalsStartingEnergy = animalsStartingEnergy;

        this.plantEnergy = plantEnergy;

        directionToCoordinatesMapper = new DirectionToCoordinatesMapper();
    }

    public void init()
    {
        //początkowe umieszczenie zwierząt
        for (int i = 0; i < animalsNumber; i++)
        {
            animalsList.add(new Animal(coordinatesWithinMap(), animalsStartingEnergy, new Genomes()));
        }

        while (true)
        {
            if (inProgress)
            {
                //nowa roślina w dżungli
                plantsList.add(new Plant(coordinatesWithinJungle(), plantEnergy));
                //nowa roślina na mapie
                plantsList.add(new Plant(coordinatesWithinMap(), plantEnergy));

                //ruszanie zwierzętami
                for (Animal animal : animalsList)
                {
                    moveAnimal(animal);
                }

                //jedzenie roślin
                for (Plant plant : plantsList)
                {
                    eatPlant(plant);
                }

                //rozmnażanie
                breeding();

                //wywalanie zwierząt z 0 energią
                animalsList = animalsList.stream().filter(animal -> animal.getEnergy() > 0).collect(Collectors.toList());
                //wywalanie roślin z 0 energią
                plantsList = plantsList.stream().filter(plant -> plant.getEnergy() > 0).collect(Collectors.toList());

                simulationProgressListener.update(animalsList, plantsList);
            }

            try
            {
                Thread.sleep(100);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        inProgress = true;
    }

    public void stop() {
        inProgress = false;
    }

    public void addEventListener(SimulationProgressListener listener) {
        simulationProgressListener = listener;
    }

    private Coordinates coordinatesWithinJungle()
    {
        return new Coordinates(jungleStart.getX() + random.nextInt(jungleWidth), jungleStart.getY() + random.nextInt(jungleHeight));
    }

    private Coordinates coordinatesWithinMap()
    {
        return new Coordinates(random.nextInt(mapWidth), random.nextInt(mapHeight));
    }

    private void moveAnimal(Animal animal)
    {
        Coordinates moveCoordinates = directionToCoordinatesMapper.map(animal.getGenome().randomDirection());
        Coordinates newCoordinates = animal.getCoordinates().addCoordinates(moveCoordinates);

        if (newCoordinates.getX() >= 0 && newCoordinates.getY() >= 0 && newCoordinates.getX() < mapWidth && newCoordinates.getY() < mapHeight)
        {
            animal.setNewCoordinates(newCoordinates);
        }
        animal.setEnergy(animal.getEnergy() - 1);
    }

    private void eatPlant(Plant plant)
    {
        List<Animal> animalsAtPlace = animalsList.stream().filter(animal -> animal.getCoordinates().equals(plant.getCoordinates())).collect(Collectors.toList());
        int maxEnergy = -1;
        for (Animal animal : animalsAtPlace)
        {
            if (animal.getEnergy() > maxEnergy)
            {
                maxEnergy = animal.getEnergy();
            }
        }

        int finalMaxEnergy = maxEnergy;
        List<Animal> topEnergyAnimals = animalsAtPlace.stream().filter(animal -> animal.getEnergy() == finalMaxEnergy).collect(Collectors.toList());

        if (topEnergyAnimals.size() != 0)
        {
            int energyToAdd = plant.getEnergy() / topEnergyAnimals.size();
            for (Animal animal : topEnergyAnimals)
            {
                animal.setEnergy(animal.getEnergy() + energyToAdd);
            }

            plant.setEnergy(0);
        }
    }

    private void breeding()
    {

    }
}
