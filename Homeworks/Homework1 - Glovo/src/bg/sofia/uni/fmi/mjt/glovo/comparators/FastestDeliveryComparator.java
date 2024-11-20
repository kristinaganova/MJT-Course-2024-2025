package bg.sofia.uni.fmi.mjt.glovo.comparators;

import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;

import java.util.Comparator;

public class FastestDeliveryComparator implements Comparator<Delivery> {

    @Override
    public int compare(Delivery d1, Delivery d2) {
        int timeComparison = Integer.compare(d1.getDeliveryCost().estimatedTime(),
                                             d2.getDeliveryCost().estimatedTime());
        if (timeComparison != 0) {
            return timeComparison;
        }
        return Double.compare(d1.getDeliveryCost().price(), d2.getDeliveryCost().price());
    }
}
