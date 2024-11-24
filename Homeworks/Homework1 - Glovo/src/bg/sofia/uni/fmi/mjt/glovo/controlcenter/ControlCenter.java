package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapWrapper;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;

import java.util.List;

public class ControlCenter implements ControlCenterApi {

    private final MapWrapper mapWrapper;

    public ControlCenter(char[][] mapLayout) {
        this.mapWrapper = new MapWrapper(mapLayout);
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                               double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        List<MapEntity> deliveryGuys = mapWrapper.getDeliveryGuys();
        DeliveryInfo bestDelivery = null;

        for (MapEntity deliveryGuy : deliveryGuys) {
            DeliveryInfo currentDelivery = mapWrapper.calculateDeliveryInfo(deliveryGuy,
                    new MapEntity(restaurantLocation, MapEntityType.RESTAURANT),
                    new MapEntity(clientLocation, MapEntityType.CLIENT),
                    shippingMethod);

            if (currentDelivery == null || !isWithinConstraints(currentDelivery, maxPrice, maxTime)) {
                continue;
            }

            if (bestDelivery == null || compareDeliveries(bestDelivery, currentDelivery, shippingMethod)) {
                bestDelivery = currentDelivery;
            }
        }

        return bestDelivery;
    }

    @Override
    public MapEntity[][] getLayout() {
        return mapWrapper.getEntityLayout();
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