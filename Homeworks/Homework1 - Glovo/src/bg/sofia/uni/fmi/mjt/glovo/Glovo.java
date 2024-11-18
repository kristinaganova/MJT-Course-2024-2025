package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapWrapper;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;
import bg.sofia.uni.fmi.mjt.glovo.exception.PathNotFoundException;

import java.util.List;

public class Glovo implements GlovoApi {
    private final MapWrapper mapWrapper;

    public Glovo(char[][] mapLayout) {
        this.mapWrapper = new MapWrapper(mapLayout);
    }

    MapWrapper getMapWrapper() {
        return mapWrapper;
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

        List<MapEntity> deliveryGuys = mapWrapper.findDeliveryGuys();
        if (deliveryGuys.isEmpty()) {
            throw new NoAvailableDeliveryGuyException("No delivery guys available for the delivery");
        }

        return determineBestDelivery(client, restaurant, foodItem, method, maxPrice, maxTime, deliveryGuys);
    }

    private Delivery determineBestDelivery(MapEntity client, MapEntity restaurant, String foodItem,
                                           ShippingMethod method, Double maxPrice, Integer maxTime,
                                           List<MapEntity> deliveryGuys) throws NoAvailableDeliveryGuyException {
        Delivery bestDelivery = null;

        for (MapEntity deliveryGuy : deliveryGuys) {
            Delivery current = evaluateDelivery(deliveryGuy, restaurant, client, foodItem, method, maxPrice, maxTime);
            if (current != null && (bestDelivery == null || compareDeliveries(bestDelivery, current, method))) {
                bestDelivery = current;
            }
        }

        if (bestDelivery == null) {
            throw new NoAvailableDeliveryGuyException("No suitable delivery option found for the constraints provided");
        }

        return bestDelivery;
    }

    private Delivery evaluateDelivery(MapEntity deliveryGuy, MapEntity restaurant, MapEntity client, String foodItem,
                                      ShippingMethod method, Double maxPrice, Integer maxTime) {
        DeliveryInfo info;

        try {
            info = mapWrapper.calculateDeliveryInfo(deliveryGuy, restaurant, client, method);
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
            throw new InvalidOrderException("Client or restaurant location is invalid");
        }
        if (client.type() != MapEntityType.CLIENT || restaurant.type() != MapEntityType.RESTAURANT) {
            throw new InvalidOrderException("Invalid MapEntity types for client or restaurant");
        }
    }

    private boolean compareDeliveries(Delivery d1, Delivery d2, ShippingMethod method) {
        if (method == ShippingMethod.CHEAPEST) {
            return d2.getDeliveryCost().price() < d1.getDeliveryCost().price();
        }
        return d2.getDeliveryCost().estimatedTime() < d1.getDeliveryCost().estimatedTime();
    }
}
