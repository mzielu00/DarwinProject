package service;

import model.Animal;
import model.Plant;

import java.util.List;

public interface SimulationProgressListener
{
    void update(List<Animal> animals, List<Plant> plants, int day);
}
