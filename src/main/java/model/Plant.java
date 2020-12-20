package model;

public class Plant {
    private final Coordinates coordinates;
    private int energy;

    public Plant(Coordinates coordinates, int energy)
    {
        this.coordinates = coordinates;
        this.energy = energy;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy)
    {
        this.energy = energy;
    }
}
