package ai_project.algorithms;

import ai_project.models.Node;

import java.util.*;

public class Astar {

    private List<Node> allNodes;
    private Set<Node> nodesToVisit;
    private Node start;
    private Node goal;

    public Astar(List<Node> nodes, Node start, Node goal) {
        allNodes = nodes;
        this.start = start;
        this.goal = goal;
        nodesToVisit = new HashSet<>();
        for (Node node : allNodes) {
            node.calculateHeuristicDistance(goal);
        }
    }

    public List<Node> getPath() {
        Node currentNode = new Node(start);
        currentNode.setDistanceFromStart(0);
        addNewToVisitFromNodeNeighbors(currentNode);
        while (currentNode.getId() != goal.getId() && !nodesToVisit.isEmpty()) {
            nodesToVisit.remove(currentNode);
            addNewToVisitFromNodeNeighbors(currentNode);
            currentNode = new Node(findBestNodeToVisit());
        }
        return reconstructPath(currentNode);
    }

    private void addNewToVisitFromNodeNeighbors(Node node) {
        for (Map.Entry<Integer, Integer> child : node.getChildren().entrySet()) {
            Node nodeToAdd = new Node(allNodes.get(child.getKey()));
            nodeToAdd.setCameFrom(node);
            nodeToAdd.setDistanceFromStart(node.getDistanceFromStart() + child.getValue());
            addNodeToNodesToVisit(nodeToAdd);
        }
    }

    private void addNodeToNodesToVisit(Node nodeToAdd) {
        if (!nodesToVisit.contains(nodeToAdd)) {
            nodesToVisit.add(nodeToAdd);
        } else {
            Node nodeAlreadyAdded = null;
            for (Node n : nodesToVisit) {
                if (n.getId() == nodeToAdd.getId()) {
                    nodeAlreadyAdded = n;
                    break;
                }
            }
            assert nodeAlreadyAdded != null;
            if (nodeToAdd.getDistanceFromStart() < nodeAlreadyAdded.getDistanceFromStart()) {
                nodesToVisit.remove(nodeAlreadyAdded);
                nodesToVisit.add(nodeToAdd);
            }
        }
    }

    private Node findBestNodeToVisit() {
        int minValue = Integer.MAX_VALUE;
        Node bestNodeToVisit = null;
        for (Node node : nodesToVisit) {
            int pathHeuristicValue = node.getDistanceFromStart() + node.getHeuristicDistance();
            if (pathHeuristicValue < minValue) {
                minValue = pathHeuristicValue;
                bestNodeToVisit = node;
            }
        }
        return bestNodeToVisit;
    }

    private List<Node> reconstructPath(Node goal) {
        List<Node> path = new LinkedList<>();
        Node currentNode = goal;
        while (currentNode.getId() != start.getId()) {
            path.add(0, new Node(currentNode));
            currentNode = currentNode.getCameFrom();
        }
        path.add(0, new Node(currentNode));
        return path;
    }

}