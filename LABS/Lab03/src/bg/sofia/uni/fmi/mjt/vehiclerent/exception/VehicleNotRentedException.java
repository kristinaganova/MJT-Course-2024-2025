package bg.sofia.uni.fmi.mjt.vehiclerent.exception;

public class VehicleNotRentedException extends UnsupportedOperationException {
    public VehicleNotRentedException(String message) {
        super(message);
    }

    public VehicleNotRentedException(String message, Throwable cause) {
        super(message, cause);
    }
}
