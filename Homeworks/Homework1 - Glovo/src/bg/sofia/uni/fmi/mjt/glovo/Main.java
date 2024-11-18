package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;

public class Main {
    public static void main(String[] args) {
        char[][] mapLayout = {
                {'#', '#', '#', '#', '#'},
                {'#', 'R', '.', 'C', '#'},
                {'#', '.', '#', '.', '#'},
                {'#', 'A', '.', 'B', '#'},
                {'#', '#', '#', '#', '#'}
        };

        ControlCenter controlCenter = new ControlCenter(mapLayout);

        System.out.println("Map Layout:");
        printMap(controlCenter);

        Location restaurantLocation = new Location(1, 1); // R
        Location clientLocation = new Location(1, 3);     // C

        System.out.println("\nFinding the cheapest delivery...");
        DeliveryInfo cheapestDelivery = controlCenter.findOptimalDeliveryGuy(
                restaurantLocation, clientLocation, -1, -1, ShippingMethod.CHEAPEST);

        if (cheapestDelivery != null) {
            System.out.println("Cheapest delivery found: " + cheapestDelivery);
        } else {
            System.out.println("No delivery options available.");
        }

        System.out.println("\nFinding the fastest delivery under 10 minutes...");
        DeliveryInfo fastestDelivery = controlCenter.findOptimalDeliveryGuy(
                restaurantLocation, clientLocation, -1, 10, ShippingMethod.FASTEST);

        if (fastestDelivery != null) {
            System.out.println("Fastest delivery found: " + fastestDelivery);
        } else {
            System.out.println("No delivery options available.");
        }
    }

    private static void printMap(ControlCenter controlCenter) {
        var layout = controlCenter.getLayout();
        for (var row : layout) {
            for (var entity : row) {
                if (entity != null) {
                    System.out.print(entity.toSymbol());
                } else {
                    System.out.print(' ');
                }
            }
            System.out.println();
        }
    }
}
