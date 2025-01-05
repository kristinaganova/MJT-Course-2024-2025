package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;
import bg.sofia.uni.fmi.mjt.poll.server.repository.InMemoryPollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ListPollsCommandTest {
    private PollRepository repository;
    private ListPollsCommand listPollsCommand;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPollRepository();
        listPollsCommand = new ListPollsCommand(repository);
    }

    @Test
    void testListPollsWhenNoPollsExist() {
        String result = listPollsCommand.execute(new String[]{});

        assertEquals("{\"status\":\"ERROR\",\"message\":\"No active polls available.\"}", result,
                "Response should indicate no polls are available");
    }
}
