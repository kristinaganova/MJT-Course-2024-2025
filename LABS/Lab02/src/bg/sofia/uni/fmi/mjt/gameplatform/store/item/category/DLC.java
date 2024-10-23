package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DLC extends BaseGameItem {
    private Game game;

    public DLC(String title, BigDecimal price, LocalDateTime releaseDate, Game game) {
        super(title, price, releaseDate);
        setGame(game);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}