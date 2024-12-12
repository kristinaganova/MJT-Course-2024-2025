package bg.sofia.uni.fmi.mjt.glovo.delivery;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

public record DeliveryRoute(Location client, Location restaurant, Location deliveryGuy) { }
