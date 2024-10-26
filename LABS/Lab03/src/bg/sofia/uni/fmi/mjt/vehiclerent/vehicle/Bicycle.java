package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;

import java.time.Duration;
import java.time.LocalDateTime;

public non-sealed class Bicycle extends Vehicle {
    private final double pricePerHour;
    private final double pricePerDay;

    public Bicycle(String id, String model, double pricePerDay, double pricePerHour) {
        super(id, model);
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    public double calculateRentalPrice(LocalDateTime start, LocalDateTime end) throws InvalidRentingPeriodException {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start or end time cannot be null.");
        }
        if (end.isBefore(start)) {
            throw new InvalidRentingPeriodException("End time cannot be before start time.");
        }

        Duration duration = Duration.between(start, end);
        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();

        if (days >= 7) {
            throw new InvalidRentingPeriodException("Bicycles cannot be rented for more than 6 days.");
        }

        return days * pricePerDay + hours * pricePerHour;
    }
}