package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.MultiSeatVehicle;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Car extends MultiSeatVehicle {
    private final double pricePerWeek;
    private final double pricePerDay;
    private final double pricePerHour;
    private static final double SEAT_COST = 5.0;

    public Car(String id, String model, FuelType fuelType, int numberOfSeats, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model, fuelType, numberOfSeats);
        this.pricePerWeek = pricePerWeek;
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime start, LocalDateTime end) throws InvalidRentingPeriodException {
        Duration rentalDuration = Duration.between(start, end);

        if (rentalDuration.toMinutes() <= 60) {
            return pricePerHour;
        }

        long totalDays = rentalDuration.toDays();
        long weeks = totalDays / 7;
        long days = totalDays % 7;
        long hours = rentalDuration.minusDays(totalDays).toHours();

        double rentalCost = (weeks * pricePerWeek) + (days * pricePerDay) + (hours * pricePerHour);
        double seatTax = getNumberOfSeats() * SEAT_COST;
        double fuelTax = totalDays * getFuelType().getDailyTax();

        return rentalCost + seatTax + fuelTax;
    }
}