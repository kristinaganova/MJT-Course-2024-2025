package bg.sofia.uni.fmi.mjt.gameplatform.store;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.ItemFilter;
import java.math.BigDecimal;

public class GameStore implements StoreAPI {
    private static final String VAN40 = "VAN40";
    private static final String YO100 = "100YO";

    private StoreItem[] availableItems;
    private boolean van40Applied = false;
    private boolean y100Applied = false;

    public GameStore(StoreItem[] availableItems) {
        this.availableItems = availableItems;
    }

    @Override
    public StoreItem[] findItemByFilters(ItemFilter[] itemFilters) {
        if (itemFilters == null || itemFilters.length == 0) {
            return availableItems.clone();
        }

        StoreItem[] result = new StoreItem[availableItems.length];
        int resultCount = 0;

        for (StoreItem item : availableItems) {
            boolean matchesAllFilters = true;

            for (ItemFilter filter : itemFilters) {
                if (!filter.matches(item)) {
                    matchesAllFilters = false;
                    break;
                }
            }

            if (matchesAllFilters) {
                result[resultCount++] = item;
            }
        }

        StoreItem[] trimmedResult = new StoreItem[resultCount];
        System.arraycopy(result, 0, trimmedResult, 0, resultCount);
        return trimmedResult;
    }

    @Override
    public void applyDiscount(String promoCode) {
        if (promoCode.equals("VAN40") && !van40Applied) {
            for (StoreItem item : availableItems) {
                BigDecimal discount = item.getPrice().multiply(new BigDecimal("0.40"));
                item.setPrice(item.getPrice().subtract(discount));
            }
            van40Applied = true;
        } else if (promoCode.equals("100YO") && !y100Applied) {
            for (StoreItem item : availableItems) {
                item.setPrice(BigDecimal.ZERO);
            }
            y100Applied = true;
        }
    }

    @Override
    public boolean rateItem(StoreItem item, int rating) {
        if (item == null || rating < 1 || rating > 5) {
            return false;
        }

        item.rate(rating);
        return true;
    }
}
