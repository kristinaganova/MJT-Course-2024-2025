package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.Vehicle;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.FuelType;

public abstract non-sealed class MultiSeatVehicle extends Vehicle {
    private final int numberOfSeats;
    private final FuelType fuelType;

    public MultiSeatVehicle(String id, String model, FuelType fuelType, int numberOfSeats) {
        super(id, model);
        this.fuelType = fuelType;
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public FuelType getFuelType() {
        return fuelType;
    }
}
