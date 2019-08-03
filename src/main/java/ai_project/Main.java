package ai_project;

import ai_project.algorithms.Astar;
import ai_project.algorithms.GeneticAlgorithm;
import ai_project.fileUtils.FileReader;
import ai_project.fileUtils.FileWriter;
import ai_project.models.Individual;
import ai_project.models.Node;

import java.util.LinkedList;
import java.util.List;

public class Main {

    private final static int POPULATION = 10;
    private final static int MAX_GENERATIONS_COUNT = -1;
    private final static int MAX_GENERATIONS_WITH_NO_IMPROVEMENT = 5000000;
    private final static double CROSS_FACTOR = 0.8;
    private final static double MUTATE_FACTOR = 0.1;

    public static void main(String[] args) {
        FileReader fileReader = new FileReader();
        List<Node> nodes = fileReader.getNodes();

        int[][] connectionsMatrix = fileReader.getInputMatrix();

        for (int i = 0; i < nodes.size(); i++) {  // add children to every node
            for (int j = 0; j < nodes.size(); j++) {
                if (connectionsMatrix[i][j] != Integer.MAX_VALUE && connectionsMatrix[i][j] != 0) {
                    nodes.get(i).addChildren(nodes.get(j), connectionsMatrix[i][j]);
                }
            }
        }

        @SuppressWarnings("unchecked") LinkedList<Node>[][] paths = new LinkedList[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                LinkedList<Node> path = new LinkedList<>();
                if (i == j) {  // add node as 1-element path
                    path.add(new Node(nodes.get(i)));
                } else if (connectionsMatrix[i][j] < Integer.MAX_VALUE) {  // add nodes i, j as path between them
                    path.add(new Node(nodes.get(i)));
                    path.add(new Node(nodes.get(j)));
                } else {  // find path using A* algorithm, update distance in matrix
                    path = (LinkedList<Node>) (new Astar(nodes, nodes.get(i), nodes.get(j))).getPath();
                    connectionsMatrix[i][j] = path.get(path.size() - 1).getDistanceFromStart();
                    nodes.get(i).addChildren(nodes.get(j), connectionsMatrix[i][j]);
                }
                paths[i][j] = path;
            }
        }
        FileWriter.writeMatrixToFile(connectionsMatrix);
        FileWriter.writeNodeListToFile(nodes);

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                POPULATION,
                MAX_GENERATIONS_COUNT,
                MAX_GENERATIONS_WITH_NO_IMPROVEMENT,
                CROSS_FACTOR,
                MUTATE_FACTOR,
                connectionsMatrix
        );
        Individual path = geneticAlgorithm.start();
        List<Node> bestPath = new LinkedList<>();
        for (int i = 1; i < path.getChromosome().length; i++) {
            int node1id = path.getChromosome()[i - 1];
            int node2id = path.getChromosome()[i];
            bestPath.addAll(paths[node1id][node2id]);
            bestPath.remove(bestPath.size() - 1);
        }

        System.out.println("Best path found:");
        StringBuilder pathAsString = new StringBuilder();
        int pathLength = 0;
        Node previousNode = bestPath.get(0);
        Node temp;
        for (int i = 1; i < bestPath.size(); i++) {
            temp = bestPath.get(i);
            pathAsString.append(temp.getName())
                    .append(" -> ");
            pathLength += connectionsMatrix[previousNode.getId()][temp.getId()];
            previousNode = temp;
        }
        pathLength += connectionsMatrix[bestPath.get(bestPath.size() - 1).getId()][bestPath.get(0).getId()];
        pathAsString.append(bestPath.get(0).getName())
                .append("\n")
                .append("Path length = ")
                .append(pathLength / 60)
                .append(" h ")
                .append(pathLength % 60)
                .append(" min");
        System.out.println(pathAsString.toString());
        FileWriter.writePathToFile(pathAsString.toString());
    }

}