package bg.sofia.uni.fmi.mjt.vehiclerent;

import bg.sofia.uni.fmi.mjt.vehiclerent.driver.Driver;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;

import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.Bicycle;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.Vehicle;

import java.time.Duration;
import java.time.LocalDateTime;

public class RentalService {

    public void rentVehicle(Driver driver, Vehicle vehicle, LocalDateTime startOfRent) throws VehicleAlreadyRentedException {
        validateRentalParameters(driver, vehicle, startOfRent);
        vehicle.rent(driver, startOfRent);
    }

    public double returnVehicle(Vehicle vehicle, LocalDateTime endOfRent) throws InvalidRentingPeriodException, VehicleNotRentedException {
        validateReturnParameters(vehicle, endOfRent);

        if(!vehicle.isRented()){
            throw new VehicleNotRentedException("Vehicle is not rented");
        }

        LocalDateTime startOfRent = vehicle.getStartOfRent();
        double rentalCost = vehicle.calculateRentalPrice(startOfRent, endOfRent);
        Driver driver = vehicle.getCurrentDriver();

        if (driver != null && !(vehicle instanceof Bicycle)) {
            double dailySurcharge = driver.ageGroup().getDailyFee();
            long hours = Duration.between(startOfRent, endOfRent).toHours();
            long days = (hours + 23) / 24;
            rentalCost += dailySurcharge * days;
        }
        vehicle.returnBack(endOfRent);
        return rentalCost;
    }

    private void validateRentalParameters(Driver driver, Vehicle vehicle, LocalDateTime startOfRent) {
        if (driver == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (startOfRent == null) {
            throw new IllegalArgumentException("Start of rent time cannot be null");
        }
    }

    private void validateReturnParameters(Vehicle vehicle, LocalDateTime endOfRent) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        if (endOfRent == null) {
            throw new IllegalArgumentException("End of rent time cannot be null");
        }
    }
}