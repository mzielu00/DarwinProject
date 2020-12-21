package service;

import mapper.DirectionToCoordinatesMapper;
import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BreedManager {
    private final Random random = new Random();
    private final DirectionToCoordinatesMapper directionToCoordinatesMapper = new DirectionToCoordinatesMapper();

    public Animal childGenom(Animal parent1, Animal parent2, EmptyCoordinatesOnMap emptyCoordinatesOnMap)
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
        parent1.breed();
        parent2.breed();
        List<Coordinates> possibleBirthplaces = emptyCoordinatesOnMap.emptyAroundCoordinate(parent1.getCoordinates());
        Coordinates randomPlace;
        if (possibleBirthplaces.size() > 0) {
            randomPlace = possibleBirthplaces.get(random.nextInt(possibleBirthplaces.size()));
        } else {
            randomPlace = directionToCoordinatesMapper.map(parent1.getCoordinates(), parent1.getGenome().randomDirection());
        }
        return new Animal(randomPlace, newEnergy, new Genomes(childGenom));
    }
}
