package model;

import java.util.*;

public class Genomes {
    private final int geneOptions = Direction.values().length;
    private final int geneSize = geneOptions * 4;

    private final List<Integer> gene;
    private final Random random = new Random();

    public Genomes ()
    {
        gene = new ArrayList<>();
        initGenomes();
    }

    public Genomes (List<Integer> newGene)
    {
        this.gene = newGene;
        Collections.sort(this.gene);
    }

    void initGenomes()
    {
        for (int i = 0; i < geneSize; i++)
        {
            int rand = random.nextInt(geneOptions);
            gene.add(rand);
        }
        Collections.sort(gene);
    }
    public List<Integer> getGenes()
    {
        return gene;
    }

    public Direction randomDirection()
    {
        return Direction.values()[gene.get(random.nextInt(geneSize))];
    }
}