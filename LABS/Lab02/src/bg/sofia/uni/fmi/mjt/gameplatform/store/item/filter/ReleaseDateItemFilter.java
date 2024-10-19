package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import java.time.LocalDateTime;

public class ReleaseDateItemFilter implements ItemFilter {
    private final LocalDateTime lowerBound;
    private final LocalDateTime upperBound;

    public ReleaseDateItemFilter(LocalDateTime lowerBound, LocalDateTime upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public boolean matches(StoreItem item) {
        LocalDateTime releaseDate = item.getReleaseDate();
        return (lowerBound == null || !releaseDate.isBefore(lowerBound)) &&
                (upperBound == null || !releaseDate.isAfter(upperBound));
    }
}