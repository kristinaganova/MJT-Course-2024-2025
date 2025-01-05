package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.repository.InMemoryPollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePollCommandTest {
    private PollRepository repository;
    private CreatePollCommand createPollCommand;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPollRepository();
        createPollCommand = new CreatePollCommand(repository);
    }

    @Test
    void testCreatePollWithValidInput() {
        String result = createPollCommand.execute(new String[]{"Favourite-food?", "Pizza", "Burger"});

        assertEquals("{\"status\":\"OK\",\"message\":\"Poll 1 created successfully.\"}", result,
                "Response should confirm poll creation");
        assertEquals(1, repository.getAllPolls().size(), "Repository should contain one poll");
    }

    @Test
    void testCreatePollWithInvalidInput() {
        String result = createPollCommand.execute(new String[]{"Favourite-food?"});

        assertEquals("{\"status\":\"ERROR\",\"message\":\"Usage: create-poll <question> <option-1> <option-2> [... <option-N>]\"}",
                result, "Response should show an error for invalid input");
    }
}
