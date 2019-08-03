package ai_project.models;

import java.util.*;

import static ai_project.algorithms.GeneticAlgorithm.GENERATOR;

public class Individual implements Comparable<Individual> {

    private final double mutateFactor;
    private final int[][] connectionMatrix;

    private int[] chromosome;
    private int fitness;

    public Individual(double mutateFactor, int genesCount, int[][] connectionMatrix) {
        List<Integer> genesToUse = new ArrayList<>();
        for (int i = 0; i < genesCount; i++) {
            genesToUse.add(i);
        }
        chromosome = new int[genesCount];
        for (int i = 0; i < genesCount; i++) {
            int index = GENERATOR.nextInt(genesToUse.size());
            chromosome[i] = genesToUse.get(index);
            genesToUse.remove(index);
        }
        this.mutateFactor = mutateFactor;
        this.connectionMatrix = connectionMatrix;
        this.fitness = calculateFitness(chromosome);
    }

    private Individual(double mutateFactor, int[][] connectionMatrix, int[] chromosome) {
        this.chromosome = chromosome;
        this.mutateFactor = mutateFactor;
        this.connectionMatrix = connectionMatrix;
        this.fitness = calculateFitness(chromosome);
    }

    public Individual cross(Individual pair) {
        int[] childChromosome = new int[pair.chromosome.length];

        // EX operator (edge-3, Whitley, 2000):

        // List: position = node id
        //      Map: key - node id, value: true - edge at both parent chromosomes, false - edge at one parent chromosome
        List<Map<Integer, Boolean>> edges = new ArrayList<>();
        for (int i = 0; i < chromosome.length; i++) {
            edges.add(new HashMap<>());
        }
        // fill edge list:
        int parent1gene = this.chromosome[0];
        int parent1neighbour1 = this.chromosome[chromosome.length - 1];
        int parent1neighbour2 = this.chromosome[1];
        mapEdges(edges, parent1gene, parent1neighbour1, parent1neighbour2);
        int parent2gene = pair.chromosome[0];
        int parent2neighbour1 = pair.chromosome[chromosome.length - 1];
        int parent2neighbour2 = pair.chromosome[1];
        mapEdges(edges, parent2gene, parent2neighbour1, parent2neighbour2);
        for (int i = 1; i < chromosome.length - 1; i++) {
            parent1gene = this.chromosome[i];
            parent1neighbour1 = this.chromosome[i - 1];
            parent1neighbour2 = this.chromosome[i + 1];
            mapEdges(edges, parent1gene, parent1neighbour1, parent1neighbour2);
            parent2gene = pair.chromosome[i];
            parent2neighbour1 = pair.chromosome[i - 1];
            parent2neighbour2 = pair.chromosome[i + 1];
            mapEdges(edges, parent2gene, parent2neighbour1, parent2neighbour2);
        }
        parent1gene = this.chromosome[chromosome.length - 1];
        parent1neighbour1 = this.chromosome[chromosome.length - 2];
        parent1neighbour2 = this.chromosome[0];
        mapEdges(edges, parent1gene, parent1neighbour1, parent1neighbour2);
        parent2gene = pair.chromosome[chromosome.length - 1];
        parent2neighbour1 = pair.chromosome[chromosome.length - 2];
        parent2neighbour2 = pair.chromosome[0];
        mapEdges(edges, parent2gene, parent2neighbour1, parent2neighbour2);

        Set<Integer> genesToUse = new HashSet<>();
        for (int i = 0; i < chromosome.length; i++) {
            genesToUse.add(i);
        }

        int index = 0;  // index of new chromosome
        int v = getRandomGeneFromSet(genesToUse);  // first gene is random
        childChromosome[index] = v;
        for (Map<Integer, Boolean> integerBooleanMap : edges) { // remove edges from this gene
            integerBooleanMap.remove(v);
        }
        while (!genesToUse.isEmpty()) {
            index++;
            if (index == childChromosome.length) {
                break;
            }
            int nextEdge = -1;
            for (Map.Entry<Integer, Boolean> edge : edges.get(v).entrySet()) { // checking edges for last gene
                if (edge.getValue()) {
                    nextEdge = edge.getKey();  // if edge is common for both parents - we're using it
                }
            }
            if (nextEdge < 0) { // if new gene is not set yet
                int minEdges = Integer.MAX_VALUE;
                for (Map.Entry<Integer, Boolean> edge : edges.get(v).entrySet()) {  // looking for edge with minimum count of own edges
                    if (edges.get(edge.getKey()).size() < minEdges && !edges.get(edge.getKey()).isEmpty()) {
                        minEdges = edges.get(edge.getKey()).size();
                        nextEdge = edge.getKey();
                    }
                }
            }
            if (nextEdge < 0) { // if new gene is not set yet - getting random one
                nextEdge = getRandomGeneFromSet(genesToUse);
            }
            v = nextEdge;
            for (Map<Integer, Boolean> integerBooleanMap : edges) {
                integerBooleanMap.remove(v); // remove edges from this gene
            }
            genesToUse.remove(v);
            childChromosome[index] = v;
        }
        return new Individual(mutateFactor, connectionMatrix, childChromosome);
    }

    private int getRandomGeneFromSet(Set<Integer> genesToUse) {
        int randomIndex = GENERATOR.nextInt(genesToUse.size());
        int i = 0;
        for (Integer gene : genesToUse) {
            if (i == randomIndex) {
                genesToUse.remove(gene);
                return gene;
            }
            i++;
        }
        return -1;
    }

    private void mapEdges(
            List<Map<Integer, Boolean>> edges,
            int parentGene,
            int parentGeneNeighbour1,
            int parentGeneNeighbour2
    ) {
        if (edges.get(parentGene).containsKey(parentGeneNeighbour1)) {
            edges.get(parentGene).remove(parentGeneNeighbour1);
            edges.get(parentGene).put(parentGeneNeighbour1, true);
        } else {
            edges.get(parentGene).put(parentGeneNeighbour1, false);
        }
        if (edges.get(parentGene).containsKey(parentGeneNeighbour2)) {
            edges.get(parentGene).remove(parentGeneNeighbour2);
            edges.get(parentGene).put(parentGeneNeighbour2, true);
        } else {
            edges.get(parentGene).put(parentGeneNeighbour2, false);
        }
    }

    public Individual mutate() {
        for (int i = 1; i < chromosome.length; i++) {
            if (GENERATOR.nextDouble() < mutateFactor) {
                int temp = chromosome[i];
                chromosome[i] = chromosome[i - 1];
                chromosome[i - 1] = temp;
            }
        }
        fitness = calculateFitness(chromosome);
        return this;
    }

    private int calculateFitness(int[] chromosome) {
        int fitness = 0;
        for (int i = 1; i < chromosome.length; i++) {
            fitness += connectionMatrix[chromosome[i - 1]][chromosome[i]];
        }
        fitness += connectionMatrix[
                chromosome[chromosome.length - 1]
                ][
                chromosome[0]
                ];
        return fitness;
    }

    public int getFitness() {
        return fitness;
    }

    @Override
    public int compareTo(Individual o) {
        return o.fitness - this.fitness;
    }

    public int[] getChromosome() {
        return chromosome;
    }

    @Override
    public String toString() {
        StringBuilder stringFromChromosome = new StringBuilder();
        for (int gene : chromosome) {
            stringFromChromosome.append(gene);
            stringFromChromosome.append("->");
        }
        stringFromChromosome.append("(path length: ");
        stringFromChromosome.append(fitness / 60);
        stringFromChromosome.append(" h ");
        stringFromChromosome.append(fitness % 60);
        stringFromChromosome.append(" min");
        stringFromChromosome.append(")");
        return stringFromChromosome.toString();
    }
}