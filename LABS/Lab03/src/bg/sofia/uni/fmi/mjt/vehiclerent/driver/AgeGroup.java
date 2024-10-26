package bg.sofia.uni.fmi.mjt.vehiclerent.driver;

public enum AgeGroup {
    JUNIOR(10),
    EXPERIENCED(0),
    SENIOR(15);

    private final int dailyFee;

    AgeGroup(int dailyFee) {
        this.dailyFee = dailyFee;
    }

    public int getDailyFee() {
        return dailyFee;
    }
}

