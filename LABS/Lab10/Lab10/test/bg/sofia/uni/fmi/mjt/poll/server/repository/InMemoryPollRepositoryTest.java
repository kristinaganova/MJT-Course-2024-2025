package bg.sofia.uni.fmi.mjt.poll.server.repository;

import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryPollRepositoryTest {
    private PollRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPollRepository();
    }

    @Test
    void testAddPoll() {
        Poll poll = new Poll("Favourite-color?", Map.of("Red", 0, "Blue", 0));
        int pollId = repository.addPoll(poll);

        assertEquals(1, pollId, "Poll ID should start from 1");
        assertEquals(poll, repository.getPoll(pollId), "Poll should be stored correctly");
    }

    @Test
    void testGetPoll() {
        Poll poll = new Poll("Best-movie?", Map.of("Titanic", 0, "Inception", 0));
        int pollId = repository.addPoll(poll);

        Poll retrievedPoll = repository.getPoll(pollId);
        assertNotNull(retrievedPoll, "Poll should exist in repository");
        assertEquals("Best-movie?", retrievedPoll.question(), "Poll question should match");
    }

    @Test
    void testGetAllPolls() {
        Poll poll1 = new Poll("Poll-1?", Map.of("Option1", 0));
        Poll poll2 = new Poll("Poll-2?", Map.of("Option2", 0));
        repository.addPoll(poll1);
        repository.addPoll(poll2);

        Map<Integer, Poll> allPolls = repository.getAllPolls();
        assertEquals(2, allPolls.size(), "Repository should contain all polls");
    }

    @Test
    void testClearAllPolls() {
        repository.addPoll(new Poll("Sample?", Map.of("Option1", 0)));
        repository.clearAllPolls();

        assertTrue(repository.getAllPolls().isEmpty(), "All polls should be cleared");
    }
}
