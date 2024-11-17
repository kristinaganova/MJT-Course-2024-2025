package bg.sofia.uni.fmi.mjt.olympics.competitor;

import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class AthleteTest {

    @Test
    void testAthleteConstructorValidArguments() {
        Athlete athlete = new Athlete("1", "Maria", "BG");

        assertNotNull(athlete, "Athlete should be created successfully");
        assertEquals("1", athlete.getIdentifier(), "Identifier should match");
        assertEquals("Maria", athlete.getName(), "Name should match");
        assertEquals("BG", athlete.getNationality(), "Nationality should match");
        assertTrue(athlete.getMedals().isEmpty(), "New athlete should have no medals");
    }

    @Test
    void testAthleteConstructorNullIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> new Athlete(null, "Maria", "BG"),
                "Expected IllegalArgumentException for null identifier");
    }

    @Test
    void testAthleteConstructorBlankIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> new Athlete("   ", "Maria", "BG"),
                "Expected IllegalArgumentException for blank identifier");
    }

    @Test
    void testAthleteConstructorNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Athlete("1", null, "BG"),
                "Expected IllegalArgumentException for null name");
    }

    @Test
    void testAthleteConstructorBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Athlete("1", "   ", "BG"),
                "Expected IllegalArgumentException for blank name");
    }

    @Test
    void testAthleteConstructorNullNationality() {
        assertThrows(IllegalArgumentException.class,
                () -> new Athlete("1", "Maria", null),
                "Expected IllegalArgumentException for null nationality");
    }

    @Test
    void testAthleteConstructorBlankNationality() {
        assertThrows(IllegalArgumentException.class,
                () -> new Athlete("1", "Maria", "   "),
                "Expected IllegalArgumentException for blank nationality");
    }

    @Test
    void testAddMedalValid() {
        Athlete athlete = new Athlete("1", "Maria", "BG");

        athlete.addMedal(Medal.GOLD);

        assertEquals(1, athlete.getMedals().size(), "Expected one medal");
        assertEquals(Medal.GOLD, athlete.getMedals().iterator().next(), "Expected GOLD medal");
    }

    @Test
    void testAddMedalNull() {
        Athlete athlete = new Athlete("1", "Maria", "BG");

        assertThrows(IllegalArgumentException.class,
                () -> athlete.addMedal(null),
                "Expected IllegalArgumentException for null medal");
    }

    @Test
    void testGetMedalCountValid() {
        Athlete athlete = new Athlete("1", "Maria", "BG");

        athlete.addMedal(Medal.GOLD);
        athlete.addMedal(Medal.GOLD);

        assertEquals(2, athlete.getMedalCount(Medal.GOLD), "Expected 2 GOLD medals");
        assertEquals(0, athlete.getMedalCount(Medal.SILVER), "Expected 0 SILVER medals");
    }

    @Test
    void testGetMedalCountNull() {
        Athlete athlete = new Athlete("1", "Maria", "BG");

        assertThrows(IllegalArgumentException.class,
                () -> athlete.getMedalCount(null),
                "Expected IllegalArgumentException for null medal type");
    }

    @Test
    void testGetMedalsImmutability() {
        Athlete athlete = new Athlete("1", "Maria", "BG");
        athlete.addMedal(Medal.BRONZE);

        Collection<Medal> medals = athlete.getMedals();

        assertThrows(UnsupportedOperationException.class,
                () -> medals.add(Medal.GOLD),
                "Expected UnsupportedOperationException when modifying medals collection");
    }

    @Test
    void testEqualitySameObject() {
        Athlete athlete = new Athlete("1", "Maria", "BG");

        assertEquals(athlete, athlete, "Athlete should be equal to itself");
    }

    @Test
    void testEqualityDifferentObject() {
        Athlete athlete1 = new Athlete("1", "Maria", "BG");
        Athlete athlete2 = new Athlete("1", "Maria", "BG");

        assertEquals(athlete1, athlete2, "Athletes with the same attributes should be equal");
    }

    @Test
    void testInequalityDifferentIdentifier() {
        Athlete athlete1 = new Athlete("1", "Maria", "BG");
        Athlete athlete2 = new Athlete("2", "Maria", "BG");

        assertNotEquals(athlete1, athlete2, "Athletes with different identifiers should not be equal");
    }

    @Test
    void testInequalityDifferentName() {
        Athlete athlete1 = new Athlete("1", "Maria", "BG");
        Athlete athlete2 = new Athlete("1", "Jane Doe", "BG");

        assertNotEquals(athlete1, athlete2, "Athletes with different names should not be equal");
    }

    @Test
    void testInequalityDifferentNationality() {
        Athlete athlete1 = new Athlete("1", "Maria", "BG");
        Athlete athlete2 = new Athlete("1", "Maria", "CAN");

        assertNotEquals(athlete1, athlete2, "Athletes with different nationalities should not be equal");
    }

    @Test
    void testHashCodeConsistency() {
        Athlete athlete = new Athlete("1", "Maria", "BG");

        int hashCode1 = athlete.hashCode();
        int hashCode2 = athlete.hashCode();

        assertEquals(hashCode1, hashCode2, "Hash code should be consistent");
    }

    @Test
    void testToString() {
        Athlete athlete = new Athlete("1", "Maria", "BG");

        String expected = "Athlete{identifier='1', name='Maria', nationality='BG', medals={BRONZE=0, SILVER=0, GOLD=0}}";

        assertEquals(expected, athlete.toString(), "toString output should match expected format");
    }

    @Test
    void testAthleteAddMultipleMedalsOfSameType() {
        Athlete athlete = new Athlete("1", "Maria", "BG");
        athlete.addMedal(Medal.GOLD);
        athlete.addMedal(Medal.GOLD);

        assertEquals(2, athlete.getMedalCount(Medal.GOLD), "Expected 2 GOLD medals");
    }

}