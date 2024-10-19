package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public abstract class BaseGameItem implements StoreItem {
    private String title;
    private BigDecimal price;
    private LocalDateTime releaseDate;
    private double rating;
    private int ratingCount;

    public BaseGameItem(String title, BigDecimal price, LocalDateTime releaseDate) {
        this.title = title;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.releaseDate = releaseDate;
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public double getRating() {
        return ratingCount == 0 ? 0 : rating;
    }

    @Override
    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public void rate(double rating) {
        this.rating = (this.rating * ratingCount + rating) / ++ratingCount;
    }
}