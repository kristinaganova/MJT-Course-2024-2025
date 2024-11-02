package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Caravan extends MultiSeatVehicle {

    private final int numberOfBeds;
    private final double pricePerWeek;
    private final double pricePerDay;
    private final double pricePerHour;

    private static final double SEAT_COST = 5.0;
    private static final double BED_COST = 10.0;
    private static final int MAX_DAYS_IN_WEEK = 7;

    public Caravan(String id, String model, FuelType fuelType, int numberOfSeats, int numberOfBeds, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model, fuelType, numberOfSeats);
        this.numberOfBeds = numberOfBeds;
        this.pricePerWeek = pricePerWeek;
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {
        if (startOfRent == null || endOfRent == null) {
            throw new InvalidRentingPeriodException("Start or end time cannot be null.");
        }
        if (endOfRent.isBefore(startOfRent)) {
            throw new InvalidRentingPeriodException("End time cannot be before start time.");
        }

        long days = ChronoUnit.DAYS.between(startOfRent.toLocalDate(), endOfRent.toLocalDate());

        if (days < 1) {
            throw new InvalidRentingPeriodException("Caravan must be rented for at least one day.");
        }

        long weeks = days / MAX_DAYS_IN_WEEK;
        long remainingDays = days % MAX_DAYS_IN_WEEK;

        LocalDateTime endOfLastFullDay = startOfRent.plusDays(days);
        long hours = ChronoUnit.HOURS.between(endOfLastFullDay, endOfRent);

        double baseCost = weeks * pricePerWeek + remainingDays * pricePerDay + hours * pricePerHour;
        double seatCost = getNumberOfSeats() * SEAT_COST;
        double bedCost = numberOfBeds * BED_COST;
        double fuelSurcharge = getFuelType().getDailyTax() * days;

        return baseCost + seatCost + bedCost + fuelSurcharge;
    }
}