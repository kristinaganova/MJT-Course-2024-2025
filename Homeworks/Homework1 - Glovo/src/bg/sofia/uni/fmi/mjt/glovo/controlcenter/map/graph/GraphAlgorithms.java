package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.graph;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.exception.PathNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GraphAlgorithms {

    public static List<Location> bfs(Graph graph, Location start, Location goal) {
        if (start.equals(goal)) {
            return List.of(start);
        }

        Queue<Location> queue = new LinkedList<>();
        Map<Location, Location> parentMap = new HashMap<>();
        Set<Location> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Location current = queue.poll();

            if (current.equals(goal)) {
                return reconstructPath(goal, parentMap);
            }

            for (Location neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        throw new PathNotFoundException("No path found between " + start + " and " + goal);
    }

    private static List<Location> reconstructPath(Location goal, Map<Location, Location> parentMap) {
        List<Location> path = new ArrayList<>();
        for (Location at = goal; at != null; at = parentMap.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public static double calculatePathDistance(List<Location> path) {
        double distance = 0.0;

        for (int i = 1; i < path.size(); i++) {
            distance += path.get(i - 1).getDistance(path.get(i));
        }

        return distance;
    }
}