package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.graph;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final Map<Location, List<Location>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(Location location) {
        adjacencyList.putIfAbsent(location, new ArrayList<>());
    }

    public void addEdge(Location from, Location to) {
        adjacencyList.get(from).add(to);
    }

    public List<Location> getNeighbors(Location location) {
        return adjacencyList.getOrDefault(location, Collections.emptyList());
    }

    public Map<Location, List<Location>> getAdjacencyList() {
        return adjacencyList;
    }
}