package bg.sofia.uni.fmi.mjt.gameplatform.store;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.category.Game;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.RatingItemFilter;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.ItemFilter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GameStoreTest {
    public static void main(String[] args) {
        StoreItem game1 = new Game("Epic Adventure", new BigDecimal("29.99"), LocalDateTime.of(2023, 10, 1, 0, 0), "Adventure");
        StoreItem game2 = new Game("Space Journey", new BigDecimal("59.99"), LocalDateTime.of(2023, 8, 15, 0, 0), "Sci-Fi");

        StoreItem[] items = { game1, game2 };
        GameStore store = new GameStore(items);

        game1.rate(5);
        game2.rate(3);
        ItemFilter ratingFilter = new RatingItemFilter(4.0);

        StoreItem[] filteredItems = store.findItemByFilters(new ItemFilter[]{ ratingFilter });
        System.out.println("Filtered Items (Rating >= 4.0):");
        for (StoreItem item : filteredItems) {
            System.out.println(item.getTitle());
        }

        System.out.println("\nApplying promo code: VAN40");
        store.applyDiscount("VAN40");
        System.out.println(game1.getTitle() + " new price: " + game1.getPrice());
        System.out.println(game2.getTitle() + " new price: " + game2.getPrice());

        // Test applyDiscount with 100YO promo code
        System.out.println("\nApplying promo code: 100YO");
        store.applyDiscount("100YO");
        System.out.println(game1.getTitle() + " new price: " + game1.getPrice());
        System.out.println(game2.getTitle() + " new price: " + game2.getPrice());

        // Test rating an item
        System.out.println("\nRating item:");
        boolean success = store.rateItem(game1, 4);
        System.out.println("Rating " + game1.getTitle() + " with 4: " + success);
        System.out.println(game1.getTitle() + " new rating: " + game1.getRating());

        // Try rating with an invalid value
        System.out.println("\nTrying to rate item with invalid rating (6):");
        success = store.rateItem(game1, 6);
        System.out.println("Rating " + game1.getTitle() + " with 6: " + success);
        System.out.println(game1.getTitle() + " rating remains: " + game1.getRating());
    }
}
