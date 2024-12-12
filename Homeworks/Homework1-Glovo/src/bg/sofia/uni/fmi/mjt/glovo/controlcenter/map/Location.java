package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import static java.lang.Math.sqrt;

public record Location(int x, int y) {

    public double getDistance(Location location) {
        int dx = x - location.x();
        int dy = y - location.y();
        return sqrt(dx * dx + dy * dy);
    }
}
