package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

public class RatingItemFilter implements ItemFilter {
    private final double minRating;

    public RatingItemFilter(double minRating) {
        this.minRating = minRating;
    }

    @Override
    public boolean matches(StoreItem item) {
        return item.getRating() >= minRating;
    }
}