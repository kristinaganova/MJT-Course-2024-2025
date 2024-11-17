package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompetitionTest {

    @Test
    public void testCompetitionValidArguments() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        Competition competition = new Competition("100m Sprint", "Sprint", competitors);

        assertNotNull(competition, "Competition should be created successfully");
        assertEquals("100m Sprint", competition.getName());
        assertEquals("Sprint", competition.getDiscipline());
        assertEquals(competitors, competition.competitors());
    }

    @Test
    public void testCompetitionNullName() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        assertThrows(IllegalArgumentException.class, () -> new Competition(null, "Sprint", competitors));
    }

    @Test
    public void testCompetitionBlankName() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        assertThrows(IllegalArgumentException.class, () -> new Competition("   ", "Sprint", competitors));
    }

    @Test
    public void testCompetitionNullDiscipline() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", null, competitors));
    }

    @Test
    public void testCompetitionBlankDiscipline() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", "   ", competitors));
    }

    @Test
    public void testCompetitionNullCompetitors() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", "Sprint", null));
    }

    @Test
    public void testCompetitionEmptyCompetitors() {
        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", "Sprint", Collections.emptySet()));
    }

    @Test
    public void testCompetitionCompetitorsWithNullElement() {
        Set<Competitor> competitors = new HashSet<>();
        competitors.add(new Athlete("1", "Maria", "BG"));
        competitors.add(null);

        assertThrows(IllegalArgumentException.class, () -> new Competition("100m Sprint", "Sprint", competitors));
    }
    @Test
    public void testCompetitionImmutabilityOfCompetitors() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        Competition competition = new Competition("100m Sprint", "Sprint", competitors);

        assertThrows(UnsupportedOperationException.class, () -> {
            competition.competitors().add(new Athlete("2", "John", "USA"));
        });
    }

    @Test
    public void testCompetitionEqualitySameObject() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        Competition competition = new Competition("100m Sprint", "Sprint", competitors);

        assertEquals(competition, competition, "The competition should be equal to itself");
    }

    @Test
    public void testCompetitionEqualityDifferentObjects() {
        Set<Competitor> competitors1 = Set.of(new Athlete("1", "Maria", "BG"));
        Set<Competitor> competitors2 = Set.of(new Athlete("2", "John", "USA"));
        Competition competition1 = new Competition("100m Sprint", "Sprint", competitors1);
        Competition competition2 = new Competition("100m Sprint", "Sprint", competitors2);

        assertEquals(competition1, competition2, "Competitions with the same name and discipline should be equal");
    }
    @Test
    public void testCompetitionEqualityOfNullObject() {
        Set<Competitor> competitors1 = Set.of(new Athlete("1", "Maria", "BG"));

        assertFalse(competitors1.equals(null), "Cannot compare with null objects");
    }

    @Test
    public void testCompetitionEqualityOfDifferentObjects() {
        Set<Competitor> competitors1 = Set.of(new Athlete("1", "Maria", "BG"));

        assertFalse(competitors1.equals("Sth"),"Cannot compare with null objects");
    }

    @Test
    public void testCompetitionInequalityDifferentName() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        Competition competition1 = new Competition("100m Sprint", "Sprint", competitors);
        Competition competition2 = new Competition("200m Sprint", "Sprint", competitors);

        assertNotEquals(competition1, competition2, "Competitions with different names should not be equal");
    }

    @Test
    public void testCompetitionInequalityDifferentDiscipline() {
        Set<Competitor> competitors = new HashSet<>();
        competitors.add(new Athlete("1", "Maria", "BG")); // Valid competitor
        Competition competition1 = new Competition("100m Sprint", "Sprint", competitors);
        Competition competition2 = new Competition("100m Sprint", "Hurdles", competitors);

        assertNotEquals(competition1, competition2, "Competitions with different disciplines should not be equal");
    }

    @Test
    public void testCompetitionHashCodeConsistency() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        Competition competition = new Competition("100m Sprint", "Sprint", competitors);

        int hashCode1 = competition.hashCode();
        int hashCode2 = competition.hashCode();

        assertEquals(hashCode1, hashCode2, "HashCode should remain consistent");
    }
}
