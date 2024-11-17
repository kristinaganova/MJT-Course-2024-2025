package bg.sofia.uni.fmi.mjt.olympics.comparator;

import bg.sofia.uni.fmi.mjt.olympics.MJTOlympics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NationMedalComparatorTest {
    @Test
    void testNationMedalComparatorWithDifferentMedalCounts() {
        MJTOlympics mockOlympics = mock(MJTOlympics.class);
        when(mockOlympics.getTotalMedals("BG")).thenReturn(5);
        when(mockOlympics.getTotalMedals("USA")).thenReturn(3);

        NationMedalComparator comparator = new NationMedalComparator(mockOlympics);

        assertTrue(comparator.compare("BG", "USA") < 0, "BG should rank higher than USA due to more medals");
        assertTrue(comparator.compare("USA", "BG") > 0, "USA should rank lower than BG due to fewer medals");
    }

    @Test
    void testNationMedalComparatorWithEqualMedalCounts() {
        MJTOlympics mockOlympics = mock(MJTOlympics.class);
        when(mockOlympics.getTotalMedals("BG")).thenReturn(5);
        when(mockOlympics.getTotalMedals("USA")).thenReturn(5);

        NationMedalComparator comparator = new NationMedalComparator(mockOlympics);

        assertTrue(comparator.compare("BG", "USA") < 0, "BG should rank higher alphabetically when medals are equal");
        assertTrue(comparator.compare("USA", "BG") > 0, "USA should rank lower alphabetically when medals are equal");
    }

    @Test
    void testNationMedalComparatorHandlesNullOlympics() {
        assertThrows(IllegalArgumentException.class,
                () -> new NationMedalComparator(null),
                "NationMedalComparator should throw IllegalArgumentException when MJTOlympics is null");
    }

    @Test
    void testNationMedalComparatorHandlesNullNation() {
        MJTOlympics mockOlympics = mock(MJTOlympics.class);
        NationMedalComparator comparator = new NationMedalComparator(mockOlympics);

        assertThrows(IllegalArgumentException.class,
                () -> comparator.compare(null, "USA"),
                "Comparator should throw IllegalArgumentException when the first nation is null");

        assertThrows(IllegalArgumentException.class,
                () -> comparator.compare("BG", null),
                "Comparator should throw IllegalArgumentException when the second nation is null");
    }
}