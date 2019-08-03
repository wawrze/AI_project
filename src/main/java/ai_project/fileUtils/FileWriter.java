package ai_project.fileUtils;

import ai_project.models.Node;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"ResultOfMethodCallIgnored", "Duplicates"})
public class FileWriter {

    private final static String MATRIX_OUTPUT_FILE = "full_connection_matrix.txt";
    private final static String NODE_LIST_FILE = "full_node_list.txt";
    private final static String BEST_PATH = "best_path.txt";
    private static PrintWriter writer;

    public static void writePathToFile(String path) {
        File file = new File(BEST_PATH);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }
        try {
            writer = new PrintWriter(file.getName());
        } catch (IOException ignored) {
        }
        writer.print(path);
        writer.close();
    }

    public static void writeNodeListToFile(final List<Node> nodes) {
        String nodesToWrite = nodesToString(nodes);
        File file = new File(NODE_LIST_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }
        try {
            writer = new PrintWriter(file.getName());
        } catch (IOException ignored) {
        }
        writer.print(nodesToWrite);
        writer.close();
    }

    private static String nodesToString(final List<Node> nodes) {
        StringBuilder nodesAsString = new StringBuilder();
        for (Node node : nodes) {
            nodesAsString.append(node.getId());
            nodesAsString.append(" (");
            nodesAsString.append(node.getName());
            nodesAsString.append(") (");
            nodesAsString.append(node.getxPos());
            nodesAsString.append(", ");
            nodesAsString.append(node.getyPos());
            nodesAsString.append("), edges to: ");
            for (Map.Entry<Integer, Integer> child : node.getChildren().entrySet()) {
                nodesAsString.append(child.getKey());
                nodesAsString.append(" (");
                nodesAsString.append(child.getValue());
                nodesAsString.append("), ");
            }
            nodesAsString.append("\n");
        }
        return nodesAsString.toString();
    }

    public static void writeMatrixToFile(final int[][] connectionsMatrix) {
        String matrixToWrite = matrixToString(connectionsMatrix);
        File file = new File(MATRIX_OUTPUT_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }
        try {
            writer = new PrintWriter(file.getName());
        } catch (IOException ignored) {
        }
        writer.print(matrixToWrite);
        writer.close();
    }

    private static String matrixToString(final int[][] connectionsMatrix) {
        StringBuilder matrix = new StringBuilder();
        matrix.append("\t");
        for (int i = 0; i < connectionsMatrix.length; i++) {
            matrix.append(i);
            matrix.append("\t");
        }
        for (int i = 0; i < connectionsMatrix.length; i++) {
            matrix.append("\n");
            matrix.append(i);
            for (int j = 0; j < connectionsMatrix.length; j++) {
                matrix.append("\t");
                matrix.append(connectionsMatrix[i][j]);
            }
        }
        return matrix.toString();
    }


}