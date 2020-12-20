package service;

import mapper.DirectionToCoordinatesMapper;
import model.*;
import ui.MapComponent;

import java.util.*;
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

    public Simulation(int mapWidth, int mapHeight, int jungleWidth, int jungleHeight, int animalsNumber, int animalsStartingEnergy, int plantEnergy)
    {
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

        while (!animalsList.isEmpty())
        {
            if (inProgress)
            {
                EmptyCoordinates emptyCoordinates =
                        new EmptyCoordinates(mapWidth, mapHeight, jungleWidth, jungleHeight,
                                animalsList.stream().map(Animal::getCoordinates).collect(Collectors.toList()),
                                plantsList.stream().map(Plant::getCoordinates).collect(Collectors.toList()));

                //nowa roślina w dżungli
                int junglePossiblePlants = emptyCoordinates.getJungleCoordinates().size();
                if (junglePossiblePlants > 0) {
                    Coordinates newCoordinates = emptyCoordinates.getJungleCoordinates().get(random.nextInt(junglePossiblePlants));
                    plantsList.add(new Plant(newCoordinates, plantEnergy));
                    emptyCoordinates.removeFromEmpty(newCoordinates);
                }

                //nowa roślina na mapie
                int nonJunglePossiblePlants = emptyCoordinates.getNotJungleCoordinates().size();
                if (nonJunglePossiblePlants > 0) {
                    Coordinates newCoordinates = emptyCoordinates.getNotJungleCoordinates().get(random.nextInt(nonJunglePossiblePlants));
                    plantsList.add(new Plant(newCoordinates, plantEnergy));
                    emptyCoordinates.removeFromEmpty(newCoordinates);
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

                //rozmnażanie po kazdym miejscu biore top2 animalsow i potem sort
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

    private Coordinates coordinatesWithinJungle()
    {
        int Xcoord = jungleStart.getX() + random.nextInt(jungleWidth);
        int Ycoord = jungleStart.getY() + random.nextInt(jungleHeight);

        while(!isOccupied(new Coordinates(Xcoord, Ycoord)))
        {
            Xcoord = jungleStart.getX() + random.nextInt(jungleWidth);
            Ycoord = jungleStart.getY() + random.nextInt(jungleHeight);
        }
        return new Coordinates(Xcoord, Ycoord);
    }

    private Coordinates coordinatesWithinMap()
    {
        int Xcoord = random.nextInt(50);
        boolean stepXcoord = false;
        int Ycoord = random.nextInt(50);
        boolean stepYcoord = false;
        while (!(stepXcoord && stepYcoord && isOccupied(new Coordinates(Xcoord, Ycoord))))
        {
            if (Xcoord >= jungleStart.getX() && Xcoord < (jungleStart.getX() + jungleWidth +1) && Ycoord >= jungleStart.getY() && Ycoord < (jungleStart.getX() + jungleHeight +1))
            {
                stepXcoord = false;
                stepYcoord = false;
                Xcoord = random.nextInt(50);
                Ycoord = random.nextInt(50);
            }
            else
            {
                stepXcoord = true;
                stepYcoord = true;
            }
        }
        return new Coordinates(random.nextInt(mapWidth), random.nextInt(mapHeight));
    }
    public boolean isOccupied(Coordinates position)
    {
        for (Plant i : plantsList)
        {
            if (position.equals(i.getCoordinates()))
            {
                return false;
            }
        }
        for (Animal i : animalsList)
        {
            if (position.equals(i.getCoordinates()))
            {
                return false;
            }
        }
        return true;
    }

    private void moveAnimal(Animal animal)
    {
        Coordinates moveCoordinates = directionToCoordinatesMapper.map(animal.getGenome().randomDirection());
        Coordinates newCoordinates = animal.getCoordinates().addCoordinates(moveCoordinates);
        int newXCoord = newCoordinates.getX()%mapWidth;
        int newYCoord = newCoordinates.getY()%mapHeight;
        if(newXCoord < 0)
        {
            newXCoord += mapWidth;
        }
        if(newYCoord < 0)
        {
            newYCoord += mapHeight;
        }
        Coordinates finalCoordinates = new Coordinates(newXCoord, newYCoord);
        animal.setNewCoordinates(finalCoordinates);
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

        if (topEnergyAnimals.size() != 0) {
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
            List<Animal> animals = entry.getValue();
            if (animals.size() >= 2) {
                animals.sort((Animal a1, Animal a2) -> a2.getEnergy() - a1.getEnergy());
                childGenom(animals.get(0), animals.get(1));
            }
        }
    }
    private void childGenom(Animal parent1, Animal parent2)
    {
        int idx1 = random.nextInt(32);
        int idx2 = random.nextInt(32);
        while(idx1 == idx2)
        {
            idx1 = random.nextInt(32);
            idx2 = random.nextInt(32);
        }
        List<Integer> idxs = new ArrayList<>();
        idxs.add(idx1);
        idxs.add(idx2);
        Collections.sort(idxs);
        List<Integer> parent1FirstPart = parent1.getGenome().getGenes().subList(0, idxs.get(0)+1);
        List<Integer> parent1SecondPart = parent1.getGenome().getGenes().subList(idxs.get(0)+1, idxs.get(1)+1);
        List<Integer> parent1ThirdPart = parent1.getGenome().getGenes().subList(idxs.get(1)+1, parent1.getGenome().getGenes().size());

        List<Integer> parent2FirstPart = parent2.getGenome().getGenes().subList(0, idxs.get(0)+1);
        List<Integer> parent2SecondPart = parent2.getGenome().getGenes().subList(idxs.get(0)+1, idxs.get(1)+1);
        List<Integer> parent2ThirdPart = parent2.getGenome().getGenes().subList(idxs.get(1)+1, parent2.getGenome().getGenes().size());
        random.nextBoolean();
        List<Integer> childGenom = new ArrayList<>();
        if(random.nextBoolean())
        {
            childGenom.addAll(parent1FirstPart);
            if(random.nextBoolean())
            {
                childGenom.addAll(parent1SecondPart);
                childGenom.addAll(parent2ThirdPart);
            }
            else
                {
                    childGenom.addAll(parent2SecondPart);
                    childGenom.addAll(parent1ThirdPart);
                }
        }
        else
        {
            childGenom.addAll(parent2FirstPart);
            if(random.nextBoolean())
            {
                childGenom.addAll(parent1SecondPart);
                childGenom.addAll(parent2ThirdPart);
            }
            else
            {
                childGenom.addAll(parent2SecondPart);
                childGenom.addAll(parent1ThirdPart);
            }
        }
        int newEnergy = (parent1.getEnergy() + parent2.getEnergy()) / 4;
        parent1.dropEnergy();
        parent2.dropEnergy();
        Genomes childGenomes = new Genomes(childGenom);
        Coordinates childMoveCoordinates = directionToCoordinatesMapper.map(childGenomes.randomDirection());
        Coordinates finalChildCoordinates = parent1.getCoordinates().addCoordinates(childMoveCoordinates);
        fixGenom(childGenomes);

        Animal child = new Animal(finalChildCoordinates, newEnergy, childGenomes);
        animalsList.add(child);
    }
    private void fixGenom(Genomes childGenomes)
    {

    }
}
