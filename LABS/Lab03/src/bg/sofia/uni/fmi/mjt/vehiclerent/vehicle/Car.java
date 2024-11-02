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
    private static final int MAX_DAYS_IN_WEEK = 7;
    private static final int MAX_SECONDS_IN_MINUTE = 24;

    public Car(String id, String model, FuelType fuelType, int numberOfSeats, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model, fuelType, numberOfSeats);
        this.pricePerWeek = pricePerWeek;
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime start, LocalDateTime end) throws InvalidRentingPeriodException {
        Duration rentalDuration = Duration.between(start, end);

        if (rentalDuration.toMinutes() <= MAX_SECONDS_IN_MINUTE) {
            return pricePerHour;
        }

        long totalDays = rentalDuration.toDays();
        long weeks = totalDays / MAX_DAYS_IN_WEEK;
        long days = totalDays % MAX_DAYS_IN_WEEK;
        long hours = rentalDuration.minusDays(totalDays).toHours();

        double rentalCost = (weeks * pricePerWeek) + (days * pricePerDay) + (hours * pricePerHour);
        double seatTax = getNumberOfSeats() * SEAT_COST;
        double fuelTax = totalDays * getFuelType().getDailyTax();

        return rentalCost + seatTax + fuelTax;
    }
}