package bg.sofia.uni.fmi.mjt.glovo.comparators;

import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;

import java.util.Comparator;

public class CheapestDeliveryComparator implements Comparator<Delivery> {

    @Override
    public int compare(Delivery d1, Delivery d2) {
        int priceComparison = Double.compare(d1.getDeliveryCost().price(), d2.getDeliveryCost().price());
        if (priceComparison != 0) {
            return priceComparison;
        }
        return Integer.compare(d1.getDeliveryCost().estimatedTime(), d2.getDeliveryCost().estimatedTime());
    }
}
