package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.driver.Driver;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;

import java.time.LocalDateTime;

public abstract sealed class Vehicle implements Rentable permits Bicycle, MultiSeatVehicle {
    private final String id;
    private final String model;
    private boolean isRented;
    private Driver currentDriver;
    private LocalDateTime rentStartTime;

    public Vehicle(String id, String model) {
        this.id = id;
        this.model = model;
        this.isRented = false;
    }

    @Override
    public void rent(Driver driver, LocalDateTime startRentTime) throws VehicleAlreadyRentedException {
        if (isRented) {
            throw new VehicleAlreadyRentedException("Vehicle is already rented.");
        }
        this.isRented = true;
        this.currentDriver = driver;
        this.rentStartTime = startRentTime;
    }

    @Override
    public void returnBack(LocalDateTime rentalEnd) throws VehicleNotRentedException, InvalidRentingPeriodException {
        if (!isRented) {
            throw new VehicleNotRentedException("Vehicle is not currently rented.");
        }
        if (rentalEnd.isBefore(rentStartTime)) {
            throw new InvalidRentingPeriodException("Rental end time is before start time.");
        }
        this.isRented = false;
        this.currentDriver = null;
        this.rentStartTime = null;
    }

    public abstract double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException;

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public boolean isRented() {
        return isRented;
    }

    public Driver getCurrentDriver() {
        return currentDriver;
    }

    public LocalDateTime getStartOfRent() {
        return rentStartTime;
    }
}