package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;

public record MapEntity(MapEntityType type, Location location, DeliveryType deliveryType) {

    public static MapEntity fromSymbol(char symbol, int x, int y) {
        Location location = new Location(x, y);
        switch (symbol) {
            case '#':
                return new MapEntity(MapEntityType.WALL, location, null);
            case '.':
                return new MapEntity(MapEntityType.ROAD, location, null);
            case 'R':
                return new MapEntity(MapEntityType.RESTAURANT, location, null);
            case 'C':
                return new MapEntity(MapEntityType.CLIENT, location, null);
            case 'A':
                return new MapEntity(MapEntityType.DELIVERY_GUY_CAR, location, DeliveryType.CAR);
            case 'B':
                return new MapEntity(MapEntityType.DELIVERY_GUY_BIKE, location, DeliveryType.BIKE);
            default:
                throw new IllegalArgumentException("Unknown map symbol: " + symbol);
        }
    }

    public char toSymbol() {
        switch (type) {
            case WALL:
                return '#';
            case ROAD:
                return '.';
            case RESTAURANT:
                return 'R';
            case CLIENT:
                return 'C';
            case DELIVERY_GUY_CAR:
                return 'A';
            case DELIVERY_GUY_BIKE:
                return 'B';
            default:
                throw new IllegalArgumentException("Unknown MapEntityType: " + type);
        }
    }

}