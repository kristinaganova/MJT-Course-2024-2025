package bg.sofia.uni.fmi.mjt.frauddetector.transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Transaction(String transactionID, String accountID, double transactionAmount,
                          LocalDateTime transactionDate, String location, Channel channel) {

    private static final char SEPARATOR = ',';
    private static final int VALUES_COUNT = 6;

    public static Transaction of(String line) {

        String[] parts = line.split(String.valueOf(SEPARATOR));
        if (parts.length != VALUES_COUNT) {
            throw new IllegalArgumentException("Invalid data format: " + line);
        }

        int i = 0; //to avoid checkstyle errors
        String transactionID = parts[i].strip();
        String accountID = parts[i += 1].strip();
        double transactionAmount = Double.parseDouble(parts[i += 1].strip());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime transactionDate = LocalDateTime.parse(parts[i += 1].strip(), formatter);

        String location = parts[i += 1].strip();
        Channel channel = Channel.valueOf(parts[i += 1].strip().toUpperCase());

        return new Transaction(transactionID, accountID, transactionAmount, transactionDate, location, channel);
    }

}
