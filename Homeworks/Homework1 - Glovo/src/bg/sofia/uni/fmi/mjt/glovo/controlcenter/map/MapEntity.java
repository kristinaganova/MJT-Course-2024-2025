package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

public record MapEntity(Location location, MapEntityType type) {
    public static MapEntity parseFromSymbol(char symbol, int row, int col) {
        MapEntityType type = MapEntityType.fromSymbol(symbol);
        return new MapEntity(new Location(row, col), type);
    }

    public char toSymbol() {
        return type.getSymbol();
    }
}