package bg.sofia.uni.fmi.mjt.olympics;

import bg.sofia.uni.fmi.mjt.olympics.competition.Competition;
import bg.sofia.uni.fmi.mjt.olympics.competition.CompetitionResultFetcher;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MJTOlympicsTest {

    @Test
    void testUpdateMedalStatisticsWithValidCompetition() {
        Athlete athlete1 = new Athlete("1", "Maria", "BG");
        Athlete athlete2 = new Athlete("2", "Jane", "Canada");
        Athlete athlete3 = new Athlete("3", "Emily", "UK");

        Set<Competitor> registeredCompetitors = Set.of(athlete1, athlete2, athlete3);
        Competition competition = new Competition("Running", "100m Sprint", Set.of(athlete1, athlete2, athlete3));

        CompetitionResultFetcher mockFetcher = mock(CompetitionResultFetcher.class);
        TreeSet<Competitor> ranking = new TreeSet<>(Comparator.comparing(Competitor::getIdentifier));
        ranking.add(athlete1);
        ranking.add(athlete2);
        ranking.add(athlete3);

        when(mockFetcher.getResult(competition)).thenReturn(ranking);

        MJTOlympics olympics = new MJTOlympics(registeredCompetitors, mockFetcher);

        olympics.updateMedalStatistics(competition);

        assertEquals(1, athlete1.getMedalCount(Medal.GOLD), "First-ranked athlete should have a gold medal");
        assertEquals(1, athlete2.getMedalCount(Medal.SILVER), "Second-ranked athlete should have a silver medal");
        assertEquals(1, athlete3.getMedalCount(Medal.BRONZE), "Third-ranked athlete should have a bronze medal");
    }

    @Test
    void testUpdateMedalStatisticsThrowsForNullCompetition() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        CompetitionResultFetcher mockFetcher = mock(CompetitionResultFetcher.class);

        MJTOlympics olympics = new MJTOlympics(competitors, mockFetcher);

        assertThrows(IllegalArgumentException.class,
                () -> olympics.updateMedalStatistics(null),
                "Updating medal statistics with null competition should throw an exception");
    }

    @Test
    void testUpdateMedalStatisticsThrowsForUnregisteredCompetitor() {
        Athlete registeredAthlete = new Athlete("1", "Maria", "BG");
        Athlete unregisteredAthlete = new Athlete("2", "Jane Doe", "GBR");

        Set<Competitor> registeredCompetitors = Set.of(registeredAthlete);
        Competition competition = new Competition("Swimming", "100m Freestyle", Set.of(unregisteredAthlete));

        CompetitionResultFetcher mockFetcher = mock(CompetitionResultFetcher.class);

        MJTOlympics olympics = new MJTOlympics(registeredCompetitors, mockFetcher);

        assertThrows(IllegalArgumentException.class,
                () -> olympics.updateMedalStatistics(competition),
                "Updating medal statistics with unregistered competitors should throw an exception");
    }

    @Test
    void testGetTotalMedalsForValidNationality() {
        Athlete athlete1 = new Athlete("1", "Maria", "BG");
        Athlete athlete2 = new Athlete("2", "Jane Doe", "BG");

        Set<Competitor> competitors = Set.of(athlete1, athlete2);
        Competition competition = new Competition("Running", "100m Sprint", competitors);

        CompetitionResultFetcher mockFetcher = mock(CompetitionResultFetcher.class);
        TreeSet<Competitor> ranking = new TreeSet<>(Comparator.comparing(Competitor::getIdentifier));
        ranking.add(athlete1);
        ranking.add(athlete2);

        when(mockFetcher.getResult(competition)).thenReturn(ranking);

        MJTOlympics olympics = new MJTOlympics(competitors, mockFetcher);

        olympics.updateMedalStatistics(competition);

        assertEquals(2, olympics.getTotalMedals("BG"),
                "Total medals for BG should match the medals awarded to its athletes");
    }

    @Test
    void testGetTotalMedalsThrowsForInvalidNationality() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        CompetitionResultFetcher mockFetcher = mock(CompetitionResultFetcher.class);

        MJTOlympics olympics = new MJTOlympics(competitors, mockFetcher);

        assertThrows(IllegalArgumentException.class,
                () -> olympics.getTotalMedals("GBR"),
                "Getting total medals for an invalid nationality should throw an exception");
    }

    @Test
    void testGetNationsRankList() {
        Athlete athlete1 = new Athlete("1", "Maria", "BG");
        Athlete athlete2 = new Athlete("2", "Jane", "Canada");
        Athlete athlete3 = new Athlete("3", "Emily", "BG");

        Set<Competitor> registeredCompetitors = Set.of(athlete1, athlete2, athlete3);
        Competition competition = new Competition("Running", "100m Sprint", Set.of(athlete1, athlete2, athlete3));

        CompetitionResultFetcher mockFetcher = mock(CompetitionResultFetcher.class);
        TreeSet<Competitor> ranking = new TreeSet<>(Comparator.comparing(Competitor::getIdentifier));
        ranking.add(athlete1);
        ranking.add(athlete2);
        ranking.add(athlete3);

        when(mockFetcher.getResult(competition)).thenReturn(ranking);

        MJTOlympics olympics = new MJTOlympics(registeredCompetitors, mockFetcher);

        olympics.updateMedalStatistics(competition);

        TreeSet<String> nationsRankList = olympics.getNationsRankList();

        assertEquals("BG", nationsRankList.first(),
                "BG should rank higher due to more medals compared to Canada.");
        assertEquals("Canada", nationsRankList.last(),
                "Canada should rank lower due to fewer medals compared to BG.");
    }

    @Test
    void testImmutabilityOfRegisteredCompetitors() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "Maria", "BG"));
        CompetitionResultFetcher mockFetcher = mock(CompetitionResultFetcher.class);

        MJTOlympics olympics = new MJTOlympics(competitors, mockFetcher);

        Set<Competitor> registeredCompetitors = olympics.getRegisteredCompetitors();

        assertThrows(UnsupportedOperationException.class, () -> registeredCompetitors.add(new Athlete("2", "Jane", "USA")));
    }

    @Test
    void testValidateCompetitionThrowsForUnregisteredCompetitor() {
        Athlete registeredAthlete = new Athlete("1", "Maria", "BG");
        Athlete unregisteredAthlete = new Athlete("2", "Jane", "Canada");

        Set<Competitor> competitors = Set.of(registeredAthlete);
        Competition competition = new Competition("Swimming", "100m Freestyle", Set.of(unregisteredAthlete));

        CompetitionResultFetcher mockFetcher = mock(CompetitionResultFetcher.class);
        MJTOlympics olympics = new MJTOlympics(competitors, mockFetcher);

        assertThrows(IllegalArgumentException.class,
                () -> olympics.updateMedalStatistics(competition),
                "Expected exception for unregistered competitor");
    }
}