package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapLayoutException;
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

public class MapWrapper {
    private final Map<Location, List<Location>> adjacencyList;
    private final Map<Location, MapEntity> locationEntityMap;
    private final List<MapEntity> restaurants;
    private final List<MapEntity> clients;
    private final List<MapEntity> deliveryGuys;

    public MapWrapper(char[][] mapLayout) {
        this.adjacencyList = new HashMap<>();
        this.locationEntityMap = new HashMap<>();
        this.restaurants = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.deliveryGuys = new ArrayList<>();
        buildGraph(mapLayout);
        parseMapEntities();
    }

    private void parseMapEntities() {
        for (MapEntity entity : locationEntityMap.values()) {
            switch (entity.type()) {
                case RESTAURANT -> restaurants.add(entity);
                case CLIENT -> clients.add(entity);
                case DELIVERY_GUY_CAR, DELIVERY_GUY_BIKE -> deliveryGuys.add(entity);
                default -> { }
            }
        }
    }

    public List<MapEntity> getRestaurants() {
        return restaurants;
    }

    public List<MapEntity> getClients() {
        return clients;
    }

    public List<MapEntity> getDeliveryGuys() {
        return deliveryGuys;
    }

    public MapEntity[][] getEntityLayout() {
        int maxRow = 0;
        int maxCol = 0;

        for (Location loc : locationEntityMap.keySet()) {
            if (loc.x() > maxRow) {
                maxRow = loc.x();
            }
            if (loc.y() > maxCol) {
                maxCol = loc.y();
            }
        }

        int rows = maxRow + 1;
        int cols = maxCol + 1;

        MapEntity[][] layout = new MapEntity[rows][cols];

        for (Map.Entry<Location, MapEntity> entry : locationEntityMap.entrySet()) {
            Location loc = entry.getKey();
            layout[loc.x()][loc.y()] = entry.getValue();
        }

        return layout;
    }

    private void buildGraph(char[][] mapLayout) {
        int rows = mapLayout.length;
        int cols = mapLayout[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                processCell(mapLayout, i, j, rows, cols);
            }
        }
    }

    private void processCell(char[][] mapLayout, int i, int j, int rows, int cols) {
        char symbol = mapLayout[i][j];
        Location currentLocation = new Location(i, j);
        MapEntity entity;

        try {
            entity = MapEntity.fromSymbol(symbol, i, j);
        } catch (IllegalArgumentException e) {
            throw new InvalidMapLayoutException("Invalid symbol in map layout at position (" + i + ", " + j + ")", e);
        }

        locationEntityMap.put(currentLocation, entity);

        if (symbol != '#') {
            adjacencyList.put(currentLocation, findNeighbors(mapLayout, i, j, rows, cols));
        }
    }

    private List<Location> findNeighbors(char[][] mapLayout, int i, int j, int rows, int cols) {
        List<Location> neighbors = new ArrayList<>();

        if (i > 0 && mapLayout[i - 1][j] != '#') {
            neighbors.add(new Location(i - 1, j));
        }
        if (i < rows - 1 && mapLayout[i + 1][j] != '#') {
            neighbors.add(new Location(i + 1, j));
        }
        if (j > 0 && mapLayout[i][j - 1] != '#') {
            neighbors.add(new Location(i, j - 1));
        }
        if (j < cols - 1 && mapLayout[i][j + 1] != '#') {
            neighbors.add(new Location(i, j + 1));
        }

        return neighbors;
    }

    public List<Location> bfs(Location start, Location goal) {
        if (start.equals(goal)) {
            return Collections.singletonList(start);
        }

        Queue<Location> queue = new LinkedList<>();
        Map<Location, Location> parentMap = new HashMap<>();
        Set<Location> visited = new HashSet<>();

        initializeBFS(queue, visited, start);

        while (!queue.isEmpty()) {
            Location current = queue.poll();

            if (current.equals(goal)) {
                return reconstructPath(goal, parentMap);
            }

            visitNeighbors(current, queue, visited, parentMap);
        }

        throw new PathNotFoundException("No path found between " + start + " and " + goal);
    }

    private void initializeBFS(Queue<Location> queue, Set<Location> visited, Location start) {
        queue.add(start);
        visited.add(start);
    }

    private void visitNeighbors(Location current, Queue<Location> queue, Set<Location> visited,
                                Map<Location, Location> parentMap) {
        for (Location neighbor : adjacencyList.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                parentMap.put(neighbor, current);
                queue.add(neighbor);
            }
        }
    }

    private List<Location> reconstructPath(Location goal, Map<Location, Location> parentMap) {
        List<Location> path = new ArrayList<>();
        for (Location at = goal; at != null; at = parentMap.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public List<Location> getNeighbors(Location location) {
        return adjacencyList.getOrDefault(location, Collections.emptyList());
    }

    public List<MapEntity> findDeliveryGuys() {
        List<MapEntity> deliveryGuys = new ArrayList<>();

        for (MapEntity entity : locationEntityMap.values()) {
            if (entity.type() == MapEntityType.DELIVERY_GUY_CAR
                    || entity.type() == MapEntityType.DELIVERY_GUY_BIKE) {
                deliveryGuys.add(entity);
            }
        }

        return deliveryGuys;
    }

    public DeliveryInfo calculateDeliveryInfo(MapEntity deliveryGuy, MapEntity restaurant,
                                              MapEntity client, ShippingMethod method) {
        if (deliveryGuy == null || restaurant == null || client == null) {
            throw new IllegalArgumentException("DeliveryGuy or Restaurant or client is null");
        }

        List<Location> pathToRestaurant = bfs(deliveryGuy.location(), restaurant.location());
        List<Location> pathToClient = bfs(restaurant.location(), client.location());

        if (pathToRestaurant.isEmpty() || pathToClient.isEmpty()) {
            throw new PathNotFoundException("No path found.");
        }

        double distanceToRestaurant = calculatePathDistance(pathToRestaurant);
        double distanceToClient = calculatePathDistance(pathToClient);
        double totalDistance = distanceToRestaurant + distanceToClient;

        DeliveryType deliveryType = deliveryGuy.deliveryType();
        if (deliveryType == null) {
            throw new IllegalArgumentException("DeliveryGuy type is null");
        }

        double totalPrice = totalDistance * deliveryType.getPricePerKilometer();
        int totalTime = (int) (totalDistance * deliveryType.getTimePerKilometer());

        return new DeliveryInfo(deliveryGuy.location(), totalPrice, totalTime, deliveryType);
    }

    private double calculatePathDistance(List<Location> path) {
        double distance = 0.0;

        for (int i = 1; i < path.size(); i++) {
            distance += path.get(i - 1).getDistance(path.get(i));
        }

        return distance;
    }

    public void printMap() {
        int rows = adjacencyList.size();
        int cols = adjacencyList.values().stream().findFirst().orElse(Collections.emptyList()).size();

        for (int i = 0; i < rows; i++) {
            StringBuilder line = new StringBuilder();

            for (int j = 0; j < cols; j++) {
                Location location = new Location(i, j);
                MapEntity entity = locationEntityMap.get(location);

                if (entity != null) {
                    line.append(entity.toSymbol());
                } else {
                    line.append(' ');
                }
            }

            System.out.println(line.toString());
        }
    }

}