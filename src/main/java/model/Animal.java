package model;

public class Animal {
    private Coordinates coordinates;
    private int energy;
    private final Genomes genome;

    public Animal(Coordinates coordinates, int energy, Genomes genome) {
        this.coordinates = coordinates;
        this.energy = energy;
        this.genome = genome;
    }

    public Coordinates getCoordinates()
    {
        return coordinates;
    }

    public void setNewCoordinates(Coordinates newCoordinates)
    {
        coordinates = newCoordinates;
    }

    public int getEnergy()
    {
        return energy;
    }

    public void setEnergy(int newEnergy)
    {
        energy = newEnergy;
    }

    public Genomes getGenome()
    {
        return genome;
    }
    public void dropEnergy() {
        energy = (int) (energy * 0.75);
    }
}
