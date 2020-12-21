package service;

import mapper.DirectionToCoordinatesMapper;
import model.*;
import ui.MapComponent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Simulation {
    private boolean inProgress = false;
    private SimulationProgressListener simulationProgressListener;
    private final Random random = new Random();
    private final DirectionToCoordinatesMapper directionToCoordinatesMapper;
    private final BreedManager breedManager;

    private final int jungleWidth;
    private final int jungleHeight;


    private final int mapWidth;
    private final int mapHeight;

    private final int animalsNumber;
    private final int animalsStartingEnergy;

    private final int plantEnergy;
    private final int dayEnergyCost;


    private List<Animal> animalsList = new ArrayList<>();
    private List<Plant> plantsList = new ArrayList<>();

    public Simulation(int mapWidth, int mapHeight, int jungleWidth, int jungleHeight, int animalsNumber, int animalsStartingEnergy, int plantEnergy, int dayEnergyCost)
    {
        this.jungleHeight = jungleHeight;
        this.jungleWidth = jungleWidth;

        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;

        this.animalsNumber = animalsNumber;
        this.animalsStartingEnergy = animalsStartingEnergy;

        this.plantEnergy = plantEnergy;
        this.dayEnergyCost = dayEnergyCost;

        directionToCoordinatesMapper = new DirectionToCoordinatesMapper();
        breedManager = new BreedManager();
    }

    public void init()
    {
        //początkowe umieszczenie zwierząt
        for (int i = 0; i < animalsNumber; i++)
        {
            animalsList.add(new Animal(coordinatesWithinMap(), animalsStartingEnergy, new Genomes()));
        }
        int day = 1;
        while (!animalsList.isEmpty())
        {
            if (inProgress)
            {
                EmptyCoordinatesOnMap emptyCoordinatesOnMap =
                        new EmptyCoordinatesOnMap(mapWidth, mapHeight, jungleWidth, jungleHeight,
                                animalsList.stream().map(Animal::getCoordinates).collect(Collectors.toList()),
                                plantsList.stream().map(Plant::getCoordinates).collect(Collectors.toList()));

                //nowa roślina w dżungli
                int junglePossiblePlants = emptyCoordinatesOnMap.getJungleCoordinates().size();
                if (junglePossiblePlants > 0) {
                    Coordinates newCoordinates = emptyCoordinatesOnMap.getJungleCoordinates().get(random.nextInt(junglePossiblePlants));
                    plantsList.add(new Plant(newCoordinates, plantEnergy));
                    emptyCoordinatesOnMap.removeFromEmpty(newCoordinates);
                }

                //nowa roślina na mapie
                int nonJunglePossiblePlants = emptyCoordinatesOnMap.getNotJungleCoordinates().size();
                if (nonJunglePossiblePlants > 0) {
                    Coordinates newCoordinates = emptyCoordinatesOnMap.getNotJungleCoordinates().get(random.nextInt(nonJunglePossiblePlants));
                    plantsList.add(new Plant(newCoordinates, plantEnergy));
                    emptyCoordinatesOnMap.removeFromEmpty(newCoordinates);
                }

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

                breed(emptyCoordinatesOnMap);

                //usuniecie zwierząt z 0 energią
                animalsList = animalsList.stream().filter(animal -> animal.getEnergy() > 0).collect(Collectors.toList());
                //usuniecie zjedzonych roślin (z 0 energią)
                plantsList = plantsList.stream().filter(plant -> plant.getEnergy() > 0).collect(Collectors.toList());
                simulationProgressListener.update(animalsList, plantsList, day );
                day++;
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

    public void start()
    {
        inProgress = true;
    }

    public void stop()
    {
        inProgress = false;
    }

    public void addEventListener(SimulationProgressListener listener)
    {
        simulationProgressListener = listener;
    }


    private Coordinates coordinatesWithinMap()
    {
        return new Coordinates(random.nextInt(mapWidth), random.nextInt(mapHeight));
    }


    private void moveAnimal(Animal animal)
    {
        Coordinates newPlace = directionToCoordinatesMapper.map(animal.getCoordinates(), animal.getGenome().randomDirection()).convertIfWrongValues(mapWidth, mapHeight);;
        if (newPlace.getX() >= 0 && newPlace.getY() >= 0 && newPlace.getX() < mapWidth && newPlace.getY() < mapHeight) {
            animal.setNewCoordinates(newPlace);
        }
        animal.setEnergy(animal.getEnergy() - dayEnergyCost);
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

        if (topEnergyAnimals.size() != 0) {
            int energyToAdd = plant.getEnergy() / topEnergyAnimals.size();
            for (Animal animal : topEnergyAnimals)
            {
                animal.setEnergy(animal.getEnergy() + energyToAdd);
            }
            plant.setEnergy(0);
        }
    }

    private void breed(EmptyCoordinatesOnMap emptyCoordinatesOnMap)
    {
        HashMap<Coordinates, List<Animal>> map = new HashMap<>();

        for (Animal animal : animalsList) {
            if (map.containsKey(animal.getCoordinates())) {
                List<Animal> animals = map.get(animal.getCoordinates());
                animals.add(animal);
            } else {
                List<Animal> animals = new ArrayList<>();
                animals.add(animal);
                map.put(animal.getCoordinates(), animals);
            }
        }

        for (Map.Entry<Coordinates, List<Animal>> entry : map.entrySet()) {
            List<Animal> animals = entry.getValue().stream().filter(animal -> animal.getEnergy() > animalsStartingEnergy / 2).collect(Collectors.toList());
            if (animals.size() >= 2) {
                animals.sort((Animal a1, Animal a2) -> a2.getEnergy() - a1.getEnergy());
                Animal child = breedManager.childGenom(animals.get(0), animals.get(1), emptyCoordinatesOnMap);
                animalsList.add(child);
            }
        }
    }
}
