package bg.sofia.uni.fmi.frauddetector.transaction;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void testTransactionCreation_ValidData() {
        String line = "1,acc1,100.0,2024-11-01 10:00:00,LocationA,ONLINE";

        Transaction transaction = Transaction.of(line);

        assertEquals("1", transaction.transactionID(), "Expected transaction ID to be '1'.");
        assertEquals("acc1", transaction.accountID(), "Expected account ID to be 'acc1'.");
        assertEquals(100.0, transaction.transactionAmount(), 0.001, "Expected transaction amount to be 100.0.");
        assertEquals(LocalDateTime.parse("2024-11-01 10:00:00", FORMATTER), transaction.transactionDate(),
                "Expected transaction date to match.");
        assertEquals("LocationA", transaction.location(), "Expected location to be 'LocationA'.");
        assertEquals(Channel.ONLINE, transaction.channel(), "Expected channel to be ONLINE.");
    }

    @Test
    public void testTransactionCreation_InvalidDataFormat_MissingValues() {
        String line = "1,acc1,100.0,2024-11-01 10:00:00,LocationA";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> Transaction.of(line));
        assertEquals("Invalid data format: " + line, exception.getMessage(),
                "Expected exception message for invalid format.");
    }

    @Test
    public void testTransactionCreation_InvalidDataFormat_InvalidDate() {
        String line = "1,acc1,100.0,invalid_date,LocationA,ONLINE";

        Exception exception = assertThrows(Exception.class, () -> Transaction.of(line));
        assertTrue(exception.getMessage().contains("Text 'invalid_date' could not be parsed"),
                "Expected exception message for invalid date format.");
    }

    @Test
    public void testTransactionCreation_InvalidChannel() {
        String line = "1,acc1,100.0,2024-11-01 10:00:00,LocationA,INVALID";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> Transaction.of(line));
        assertTrue(exception.getMessage().contains("No enum constant"),
                "Expected exception message for invalid channel.");
    }

    @Test
    public void testTransactionCreation_InvalidAmount() {
        String line = "1,acc1,invalid_amount,2024-11-01 10:00:00,LocationA,ONLINE";

        Exception exception = assertThrows(NumberFormatException.class, () -> Transaction.of(line));
        assertTrue(exception.getMessage().contains("For input string: \"invalid_amount\""),
                "Expected exception message for invalid amount.");
    }

    @Test
    public void testTransactionEquality() {
        String line1 = "1,acc1,100.0,2024-11-01 10:00:00,LocationA,ONLINE";
        String line2 = "1,acc1,100.0,2024-11-01 10:00:00,LocationA,ONLINE";

        Transaction transaction1 = Transaction.of(line1);
        Transaction transaction2 = Transaction.of(line2);

        assertEquals(transaction1, transaction2, "Expected transactions to be equal.");
    }

    @Test
    public void testTransactionInequality_DifferentTransactionID() {
        String line1 = "1,acc1,100.0,2024-11-01 10:00:00,LocationA,ONLINE";
        String line2 = "2,acc1,100.0,2024-11-01 10:00:00,LocationA,ONLINE";

        Transaction transaction1 = Transaction.of(line1);
        Transaction transaction2 = Transaction.of(line2);

        assertNotEquals(transaction1, transaction2, "Expected transactions to be different.");
    }

    @Test
    public void testTransactionInequality_DifferentDate() {
        String line1 = "1,acc1,100.0,2024-11-01 10:00:00,LocationA,ONLINE";
        String line2 = "1,acc1,100.0,2024-11-02 10:00:00,LocationA,ONLINE";

        Transaction transaction1 = Transaction.of(line1);
        Transaction transaction2 = Transaction.of(line2);

        assertNotEquals(transaction1, transaction2, "Expected transactions to be different.");
    }
}