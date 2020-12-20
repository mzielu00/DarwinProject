package model;

import java.util.Arrays;
import java.util.Random;

public class Genomes {
    private final int geneOptions = Direction.values().length;
    private final int geneSize = geneOptions * 4;

    private final int [] gene;
    private final Random random = new Random();

    //ten konstruktor używamy jak chcemy randomowe geny
    public Genomes ()
    {
        gene = new int[geneSize];
        initGenomes();
    }

    //tego konstruktora użyjemy jak bedziemy rodzić dziecko z już konkretnymi
    public Genomes (int[] newGene)
    {
        gene = newGene;
        Arrays.sort(gene);
    }

    void initGenomes()
    {
        for (int i = 0; i < geneSize; i++)
        {
            int rand = random.nextInt(geneOptions);
            gene[i] = rand;
        }
        Arrays.sort(gene);
    }

    public Direction randomDirection()
    {
        return Direction.values()[gene[random.nextInt(geneSize)]];
    }
}