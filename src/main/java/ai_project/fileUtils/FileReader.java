package ai_project.fileUtils;

import ai_project.models.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {

    private final static String MATRIX_FILE = "resources\\input_connection_matrix.txt";
    private final static String NODE_NAMES_FILE = "resources\\input_node_names.txt";

    private int[][] inputMatrix;
    private List<Node> nodes;

    public FileReader() {
        nodes = new ArrayList<>();
        readNodes();
        inputMatrix = matrixFileReader(nodes.size());
    }

    private void readNodes() {
        File file = null;
        Scanner reader;
        try {
            file = new File(NODE_NAMES_FILE);
            reader = new Scanner(file);
        } catch (IOException e) {
            System.out.println("File " + file.getName() + " couldn't be opened!");
            return;
        }
        reader.nextLine();  // header
        while (reader.hasNext()) {
            String nodeName = reader.nextLine();
            int nodeId = Integer.valueOf(reader.nextLine());
            int nodeXpos = Integer.valueOf(reader.nextLine());
            int nodeYpos = Integer.valueOf(reader.nextLine());
            nodes.add(new Node(nodeId, nodeName, nodeXpos, nodeYpos));
        }
        reader.close();
    }

    private int[][] matrixFileReader(int matrixSize) {
        File file = null;
        Scanner reader;
        try {
            file = new File(MATRIX_FILE);
            reader = new Scanner(file);
        } catch (IOException e) {
            System.out.println("File " + file.getName() + " couldn't be opened!");
            return new int[0][0];
        }
        int[][] input = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (i == j) {
                    input[i][j] = 0;
                } else {
                    input[i][j] = Integer.MAX_VALUE;
                }
            }
        }
        reader.nextLine();  // header
        while (reader.hasNext()) {
            int from = reader.nextInt();
            int to = reader.nextInt();
            int distance = reader.nextInt();
            input[from][to] = distance;
        }
        reader.close();
        return input;
    }

    public int[][] getInputMatrix() {
        return inputMatrix;
    }

    public List<Node> getNodes() {
        return nodes;
    }

}