package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;

import java.util.Map;

public class ListPollsCommand implements Command {

    private final PollRepository repository;

    public ListPollsCommand(PollRepository repository) {
        this.repository = repository;
    }

    @Override
    public String execute(String[] args) {
        Map<Integer, Poll> polls = repository.getAllPolls();
        if (polls.isEmpty()) {
            return "{\"status\":\"ERROR\",\"message\":\"No active polls available.\"}";
        }

        return "{\"status\":\"OK\",\"polls\":" + polls + "}";
    }
}