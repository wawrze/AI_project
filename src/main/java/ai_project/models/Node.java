package ai_project.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Node {

    private int id;
    private String name;
    private Node cameFrom;
    private int distanceFromStart;
    private int xPos;
    private int yPos;
    private Map<Integer, Integer> children;
    private int heuristicDistance;

    public Node(int id, String name, int xPos, int yPos) {
        this.id = id;
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        children = new HashMap<>();
    }

    public Node(Node node) {
        this.id = node.id;
        this.name = node.name;
        this.cameFrom = node.cameFrom;
        this.distanceFromStart = node.distanceFromStart;
        this.xPos = node.xPos;
        this.yPos = node.yPos;
        this.heuristicDistance = node.heuristicDistance;
        this.children = new HashMap<>();
        for (Map.Entry<Integer, Integer> child : node.children.entrySet()) {
            this.children.put(child.getKey(), child.getValue());
        }
    }

    public int getId() {
        return id;
    }

    public Node getCameFrom() {
        return cameFrom;
    }

    public void setCameFrom(Node cameFrom) {
        this.cameFrom = cameFrom;
    }

    public void calculateHeuristicDistance(Node goal) {
        this.heuristicDistance = (int) Math.sqrt(Math.pow((goal.xPos - this.xPos), 2) + Math.pow((goal.yPos - this.yPos), 2));
    }

    public int getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(int distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public Map<Integer, Integer> getChildren() {
        return children;
    }

    public void addChildren(Node child, int distance) {
        children.put(child.id, distance);
    }

    public int getHeuristicDistance() {
        return heuristicDistance;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}