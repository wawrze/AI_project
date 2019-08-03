package ai_project.algorithms;

import ai_project.models.Individual;

import java.util.*;

public class GeneticAlgorithm {

    public static final Random GENERATOR = new Random();

    private final int population;
    private final int maxGenerationsCount;
    private final double crossFactor;
    private final double mutateFactor;

    private final List<List<Individual>> generations;
    private final Set<Integer> nodes;
    private int firstGenerationOfBestFit = 1;
    private int[][] connectionsMatrix;
    private int maxGenerationsWithNoImprovement;

    private Individual bestFit;
    private int generationsWithNoImprovement = 0;

    public GeneticAlgorithm(
            int population,
            int maxGenerationsCount,
            int maxGenerationsWithNoImprovement,
            double crossFactor,
            double mutateFactor,
            int[][] connectionsMatrix
    ) {
        this.population = population;
        this.maxGenerationsCount = maxGenerationsCount;
        this.crossFactor = crossFactor;
        this.mutateFactor = mutateFactor;
        this.generations = new ArrayList<>();
        this.connectionsMatrix = connectionsMatrix;
        this.maxGenerationsWithNoImprovement = maxGenerationsWithNoImprovement;
        nodes = new HashSet<>();
        for (int i = 0; i < connectionsMatrix.length; i++) {
            nodes.add(i);
        }
    }

    public Individual start() {
        bestFit = setFirstGeneration();
        printGeneration(1, bestFit);
        while (
                (generations.size() < maxGenerationsCount || maxGenerationsCount == -1)
                        && (generationsWithNoImprovement <= maxGenerationsWithNoImprovement ||
                        maxGenerationsWithNoImprovement == -1)
        ) {
            try {
                Individual bestFitInGeneration = makeNewGeneration();
                printGeneration(generations.size(), bestFitInGeneration);
                if (bestFitInGeneration.compareTo(bestFit) > 0) {
                    generationsWithNoImprovement = 0;
                    bestFit = bestFitInGeneration;
                    firstGenerationOfBestFit = generations.size();
                } else {
                    generationsWithNoImprovement++;
                }
            } catch (Throwable e) {
                return bestFit;
            }
        }
        System.out.println("--------------------------------------");
        System.out.println("> BEST FIT FOUND IN GENERATION " + firstGenerationOfBestFit);
        System.out.println("> " + bestFit);
        System.out.println("> FITNESS = " + bestFit.getFitness() / 60 + " h " + bestFit.getFitness() % 60);
        System.out.println("--------------------------------------\n");
        return bestFit;
    }

    private Individual makeNewGeneration() {
        List<Individual> newGeneration = new ArrayList<>();
        Individual bestFitInGeneration = null;
        List<Individual> sortedLastGeneration = sortGeneration(generations.size() - 1);

        int generationFitnessSum = sortedLastGeneration.stream()
                .map(Individual::getFitness)
                .reduce(Integer::sum)
                .orElse(0);

        int oldGenerationMinFitness = sortedLastGeneration.get(sortedLastGeneration.size() - 1).getFitness();
        generationFitnessSum -= (oldGenerationMinFitness * population);
        int[] oldGenerationIndividualsFitness = new int[population];
        int partialSum = 0;
        for (int i = 0; i < population; i++) {
            if (generationFitnessSum > 0) {
                int fitness = 100000 - (100000 * (sortedLastGeneration.get(i).getFitness() - oldGenerationMinFitness) / generationFitnessSum);
                oldGenerationIndividualsFitness[i] = partialSum + fitness;
                partialSum += fitness;
            } else {
                oldGenerationIndividualsFitness[i] = 100000 * (i + 1) / population;
            }
        }
        for (int i = 0; i < population; i++) {
            int random = GENERATOR.nextInt(100000000);
            int newIndividualNumber = 0;
            for (int j = 0; j < population; j++) {
                if (random < oldGenerationIndividualsFitness[j]) {
                    break;
                } else {
                    newIndividualNumber = j;
                }
            }
            newGeneration.add(sortedLastGeneration.get(newIndividualNumber));
            if (i % 2 == 1 && GENERATOR.nextDouble() < crossFactor) {
                Individual child1 = newGeneration.get(i - 1).cross(newGeneration.get(i)).mutate();
                Individual child2 = newGeneration.get(i).cross(newGeneration.get(i - 1)).mutate();
                newGeneration.set(i - 1, child1);
                newGeneration.set(i, child2);
            }
            if (bestFitInGeneration == null || newGeneration.get(i).compareTo(bestFitInGeneration) > 0) {
                bestFitInGeneration = newGeneration.get(i);
            }
        }
        generations.add(new ArrayList<>(newGeneration));
        generations.add(generations.size() - 2, new ArrayList<>());
        return bestFitInGeneration;
    }

    private List<Individual> sortGeneration(int generationNumber) {
        List<Individual> list = new ArrayList<>(generations.get(generationNumber));
        list.sort(Individual::compareTo);
        return list;
    }

    private Individual setFirstGeneration() {
        Set<Individual> firstGeneration = new HashSet<>();
        Individual bestFitInGeneration = null;
        for (int i = 0; i < population; i++) {
            Individual individual = new Individual(mutateFactor, nodes.size(), connectionsMatrix);
            if (bestFitInGeneration == null || individual.compareTo(bestFitInGeneration) > 0) {
                bestFitInGeneration = individual;
            }
            firstGeneration.add(individual);
        }
        generations.add(new ArrayList<>(firstGeneration));
        return bestFitInGeneration;
    }

    private void printGeneration(int generationNumber, Individual bestFitInGeneration) {
        System.out.println("--------------------------------------");
        System.out.println("> GENERATION " + generationNumber);
        System.out.println("--------------------------------------");
        System.out.println("> BEST FIT IN THIS GENERATION:");
        System.out.println("> " + bestFitInGeneration);
        System.out.println("> FITNESS = " + bestFitInGeneration.getFitness() / 60 + " h " + bestFitInGeneration.getFitness() % 60);
        System.out.println("> BEST FITNESS TILL NOW = " + bestFit.getFitness() / 60 + " h " + bestFit.getFitness() % 60 + " (GENERATION " + firstGenerationOfBestFit + ")");
        System.out.println("--------------------------------------\n");
    }

}
