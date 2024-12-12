package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidMapLayoutException;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;
import bg.sofia.uni.fmi.mjt.glovo.exception.PathNotFoundException;
import bg.sofia.uni.fmi.mjt.glovo.comparators.CheapestDeliveryComparator;
import bg.sofia.uni.fmi.mjt.glovo.comparators.FastestDeliveryComparator;

import java.util.Comparator;
import java.util.List;

public class Glovo implements GlovoApi {
    private final ControlCenter controlCenter;

    public Glovo(char[][] mapLayout) {
        this.controlCenter = new ControlCenter(mapLayout);
        validateMap();
    }

    private void validateMap() {
        List<MapEntity> restaurants = controlCenter.getMap().getRestaurants();
        List<MapEntity> clients = controlCenter.getMap().getClients();
        List<MapEntity> deliveryGuys = controlCenter.getMap().getDeliveryGuys();

        if (restaurants.size() < 1) {
            throw new InvalidMapLayoutException("The map must contain at least one restaurant ");
        }

        if (clients.size() < 1) {
            throw new InvalidMapLayoutException("The map must contain at least one client");
        }

        if (deliveryGuys.isEmpty()) {
            throw new InvalidMapLayoutException("The map must contain at least one delivery guy.");
        }
    }

    @Override
    public Delivery getCheapestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {
        return findDeliveryOption(client, restaurant, foodItem, ShippingMethod.CHEAPEST, null, null);
    }

    @Override
    public Delivery getFastestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {
        return findDeliveryOption(client, restaurant, foodItem, ShippingMethod.FASTEST, null, null);
    }

    @Override
    public Delivery getFastestDeliveryUnderPrice(MapEntity client, MapEntity restaurant,
                                                 String foodItem, double maxPrice)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {
        return findDeliveryOption(client, restaurant, foodItem, ShippingMethod.FASTEST, maxPrice, null);
    }

    @Override
    public Delivery getCheapestDeliveryWithinTimeLimit(MapEntity client, MapEntity restaurant,
                                                       String foodItem, int maxTime)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {
        return findDeliveryOption(client, restaurant, foodItem, ShippingMethod.CHEAPEST, null, maxTime);
    }

    private Delivery findDeliveryOption(MapEntity client, MapEntity restaurant, String foodItem,
                                        ShippingMethod method, Double maxPrice, Integer maxTime)
            throws NoAvailableDeliveryGuyException, InvalidOrderException {
        validateOrder(client, restaurant);

        List<MapEntity> deliveryGuys = controlCenter.getMap().getDeliveryGuys();
        if (deliveryGuys.isEmpty()) {
            throw new NoAvailableDeliveryGuyException("No delivery guys available for the delivery");
        }

        return determineBestDelivery(client, restaurant, foodItem, method, maxPrice, maxTime, deliveryGuys);
    }

    private Delivery determineBestDelivery(MapEntity client, MapEntity restaurant, String foodItem,
                                           ShippingMethod method, Double maxPrice, Integer maxTime,
                                           List<MapEntity> deliveryGuys) {
        Delivery bestDelivery = null;

        Comparator<Delivery> comparator = (method == ShippingMethod.CHEAPEST)
                ? new CheapestDeliveryComparator()
                : new FastestDeliveryComparator();

        for (MapEntity deliveryGuy : deliveryGuys) {
            Delivery currentDelivery = createDeliveryIfValid(deliveryGuy, restaurant, client, foodItem,
                                                             method, maxPrice, maxTime);

            if (currentDelivery != null) {
                if (bestDelivery == null || comparator.compare(currentDelivery, bestDelivery) < 0) {
                    bestDelivery = currentDelivery;
                }
            }
        }

        if (bestDelivery == null) {
            throw new PathNotFoundException("No suitable delivery option found for the constraints provided");
        }

        return bestDelivery;
    }

    private Delivery createDeliveryIfValid(MapEntity deliveryGuy, MapEntity restaurant, MapEntity client,
                                           String foodItem, ShippingMethod method, Double maxPrice, Integer maxTime) {
        DeliveryInfo info;

        try {
            info = controlCenter.calculateDeliveryInfo(deliveryGuy, restaurant, client, method);
        } catch (PathNotFoundException e) {
            return null;
        }

        if (!isWithinConstraints(info, maxPrice, maxTime)) {
            return null;
        }

        return new Delivery(client.location(), restaurant.location(), deliveryGuy.location(),
                foodItem, info.price(), info.estimatedTime());
    }

    private boolean isWithinConstraints(DeliveryInfo info, Double maxPrice, Integer maxTime) {
        return (maxPrice == null || info.price() <= maxPrice) &&
                (maxTime == null || info.estimatedTime() <= maxTime);
    }

    private void validateOrder(MapEntity client, MapEntity restaurant) throws InvalidOrderException {
        if (client == null || restaurant == null) {
            throw new InvalidOrderException("Client or restaurant location is invalid.");
        }
        if (client.type() != MapEntityType.CLIENT || restaurant.type() != MapEntityType.RESTAURANT) {
            throw new InvalidOrderException("Invalid MapEntity types for client or restaurant.");
        }
    }
}