package bg.sofia.uni.fmi.mjt.glovo.delivery;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

public class Delivery {
    private final DeliveryRoute route;
    private final String foodItem;
    private final DeliveryCost deliveryCost;

    public Delivery(Location client, Location restaurant, Location deliveryGuy,
                    String foodItem, double price, int estimatedTime) {
        this.route = new DeliveryRoute(client, restaurant, deliveryGuy);
        this.foodItem = foodItem;
        this.deliveryCost = new DeliveryCost(price, estimatedTime);
    }

    public DeliveryRoute getRoute() {
        return route;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public DeliveryCost getDeliveryCost() {
        return deliveryCost;
    }

    @Override
    public String toString() {
        return String.format("Delivery of %s: %s, Price: %.2f, Estimated time: %d minutes, Route: %s",
                foodItem,
                deliveryCost,
                deliveryCost.price(),
                deliveryCost.estimatedTime(),
                route);
    }
}