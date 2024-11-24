package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.graph.Graph;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.graph.GraphAlgorithms;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapLayoutException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapWrapper {
    private final Graph graph;
    private final Map<Location, MapEntity> locationEntityMap;
    private final List<MapEntity> restaurants;
    private final List<MapEntity> clients;
    private final List<MapEntity> deliveryGuys;

    public MapWrapper(char[][] mapLayout) {
        this.graph = new Graph();
        this.locationEntityMap = new HashMap<>();
        this.restaurants = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.deliveryGuys = new ArrayList<>();
        buildGraphAndParseEntities(mapLayout);
    }

    private void buildGraphAndParseEntities(char[][] mapLayout) {
        int rows = mapLayout.length;
        int cols = mapLayout[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char symbol = mapLayout[i][j];
                Location currentLocation = new Location(i, j);

                MapEntity entity = parseEntity(symbol, i, j);
                locationEntityMap.put(currentLocation, entity);

                if (symbol != '#') {
                    graph.addNode(currentLocation);
                    for (Location neighbor : findNeighbors(mapLayout, i, j, rows, cols)) {
                        graph.addEdge(currentLocation, neighbor);
                    }
                }

                if (entity != null) {
                    categorizeEntity(entity);
                }
            }
        }
    }

    private MapEntity parseEntity(char symbol, int x, int y) {
        try {
            return MapEntity.parseFromSymbol(symbol, x, y);
        } catch (IllegalArgumentException e) {
            throw new InvalidMapLayoutException("Invalid symbol in map layout at position (" + x + ", " + y + ")", e);
        }
    }

    private void categorizeEntity(MapEntity entity) {
        switch (entity.type()) {
            case RESTAURANT -> restaurants.add(entity);
            case CLIENT -> clients.add(entity);
            case DELIVERY_GUY_CAR, DELIVERY_GUY_BIKE -> deliveryGuys.add(entity);
        }
    }

    private List<Location> findNeighbors(char[][] mapLayout, int i, int j, int rows, int cols) {
        List<Location> neighbors = new ArrayList<>();

        if (i > 0 && mapLayout[i - 1][j] != '#') neighbors.add(new Location(i - 1, j));
        if (i < rows - 1 && mapLayout[i + 1][j] != '#') neighbors.add(new Location(i + 1, j));
        if (j > 0 && mapLayout[i][j - 1] != '#') neighbors.add(new Location(i, j - 1));
        if (j < cols - 1 && mapLayout[i][j + 1] != '#') neighbors.add(new Location(i, j + 1));

        return neighbors;
    }

    public List<Location> bfs(Location start, Location goal) {
        return GraphAlgorithms.bfs(graph, start, goal);
    }

    public double calculateDeliveryDistance(List<Location> path) {
        return GraphAlgorithms.calculatePathDistance(path);
    }

    public DeliveryInfo calculateDeliveryInfo(MapEntity deliveryGuy, MapEntity restaurant,
                                              MapEntity client, ShippingMethod method) {
        List<Location> pathToRestaurant = bfs(deliveryGuy.location(), restaurant.location());
        List<Location> pathToClient = bfs(restaurant.location(), client.location());

        double distanceToRestaurant = calculateDeliveryDistance(pathToRestaurant);
        double distanceToClient = calculateDeliveryDistance(pathToClient);
        double totalDistance = distanceToRestaurant + distanceToClient;

        DeliveryType deliveryType = mapEntityTypeToDeliveryType(deliveryGuy.type());

        double totalPrice = totalDistance * deliveryType.getPricePerKilometer();
        int totalTime = (int) (totalDistance * deliveryType.getTimePerKilometer());

        return new DeliveryInfo(deliveryGuy.location(), totalPrice, totalTime, deliveryType);
    }

    public static DeliveryType mapEntityTypeToDeliveryType(MapEntityType type) {
        return switch (type) {
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            default -> throw new IllegalArgumentException("Invalid delivery guy type: " + type);
        };
    }

    public MapEntity[][] getEntityLayout() {
        int maxRow = locationEntityMap.keySet().stream().mapToInt(Location::x).max().orElse(0);
        int maxCol = locationEntityMap.keySet().stream().mapToInt(Location::y).max().orElse(0);

        MapEntity[][] layout = new MapEntity[maxRow + 1][maxCol + 1];

        for (Map.Entry<Location, MapEntity> entry : locationEntityMap.entrySet()) {
            Location loc = entry.getKey();
            layout[loc.x()][loc.y()] = entry.getValue();
        }

        return layout;
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
}