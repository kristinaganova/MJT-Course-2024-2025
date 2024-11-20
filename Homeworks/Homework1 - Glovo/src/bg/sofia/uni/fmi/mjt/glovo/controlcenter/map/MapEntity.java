package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;

public record MapEntity(MapEntityType type, Location location, DeliveryType deliveryType) {

    public static MapEntity parseFromSymbol(char symbol, int x, int y) {
        Location location = new Location(x, y);
        MapEntityType type = MapEntityType.fromSymbol(symbol);

        DeliveryType deliveryType = switch (type) {
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            default -> null;
        };

        return new MapEntity(type, location, deliveryType);
    }

    public char toSymbol() {
        return type.getSymbol();
    }
}