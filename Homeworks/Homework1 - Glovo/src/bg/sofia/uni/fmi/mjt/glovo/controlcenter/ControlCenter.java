package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapWrapper;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapLayoutException;

import java.util.List;

public class ControlCenter implements ControlCenterApi {
    private final MapWrapper mapWrapper;

    public ControlCenter(char[][] mapLayout) {
        if (mapLayout == null || mapLayout.length == 0) {
            throw new InvalidMapLayoutException("Map layout cannot be null or empty.");
        }
        this.mapWrapper = new MapWrapper(mapLayout);
    }

    public MapWrapper getMap() {
        return mapWrapper;
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                               double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        List<MapEntity> deliveryGuys = mapWrapper.getDeliveryGuys();
        DeliveryInfo bestDelivery = null;

        for (MapEntity deliveryGuy : deliveryGuys) {
            DeliveryInfo currentDelivery = calculateDeliveryInfo(
                    deliveryGuy,
                    new MapEntity(restaurantLocation, MapEntityType.RESTAURANT),
                    new MapEntity(clientLocation, MapEntityType.CLIENT),
                    shippingMethod
            );

            if (currentDelivery != null && isWithinConstraints(currentDelivery, maxPrice, maxTime)) {
                if (bestDelivery == null || compareDeliveries(bestDelivery, currentDelivery, shippingMethod)) {
                    bestDelivery = currentDelivery;
                }
            }
        }

        return bestDelivery;
    }

    @Override
    public MapEntity[][] getLayout() {
        return mapWrapper.getEntityLayout();
    }

    public DeliveryInfo calculateDeliveryInfo(MapEntity deliveryGuy, MapEntity restaurant,
                                              MapEntity client, ShippingMethod method) {
        List<Location> pathToRestaurant = mapWrapper.getGraph().bfs(deliveryGuy.location(), restaurant.location());
        List<Location> pathToClient = mapWrapper.getGraph().bfs(restaurant.location(), client.location());

        double distanceToRestaurant = calculateDeliveryDistance(pathToRestaurant);
        double distanceToClient = calculateDeliveryDistance(pathToClient);
        double totalDistance = distanceToRestaurant + distanceToClient;

        DeliveryType deliveryType = mapEntityTypeToDeliveryType(deliveryGuy.type());

        double totalPrice = totalDistance * deliveryType.getPricePerKilometer();
        int totalTime = (int) (totalDistance * deliveryType.getTimePerKilometer());

        return new DeliveryInfo(deliveryGuy.location(), totalPrice, totalTime, deliveryType);
    }

    public double calculateDeliveryDistance(List<Location> path) {
        return mapWrapper.getGraph().calculatePathDistance(path);
    }

    public static DeliveryType mapEntityTypeToDeliveryType(MapEntityType type) {
        return switch (type) {
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            default -> throw new IllegalArgumentException("Invalid delivery guy type: " + type);
        };
    }

    private boolean isWithinConstraints(DeliveryInfo info, double maxPrice, int maxTime) {
        return (maxPrice == -1 || info.price() <= maxPrice) &&
                (maxTime == -1 || info.estimatedTime() <= maxTime);
    }

    private boolean compareDeliveries(DeliveryInfo d1, DeliveryInfo d2, ShippingMethod method) {
        if (method == ShippingMethod.CHEAPEST) {
            return d2.price() < d1.price();
        }
        return d2.estimatedTime() < d1.estimatedTime();
    }
}
