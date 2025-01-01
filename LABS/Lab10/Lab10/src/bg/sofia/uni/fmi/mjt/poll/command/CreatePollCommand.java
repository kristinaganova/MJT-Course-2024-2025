package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;

import java.util.HashMap;
import java.util.Map;

public class CreatePollCommand implements Command {

    private final PollRepository repository;

    public CreatePollCommand(PollRepository repository) {
        this.repository = repository;
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 3) {
            return "{\"status\":\"ERROR\",\"message\":\"Usage: create-poll <question> <option-1> <option-2> [... <option-N>]\"}";
        }

        String question = args[0];
        Map<String, Integer> options = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            options.put(args[i], 0);
        }

        Poll poll = new Poll(question, options);
        int pollId = repository.addPoll(poll);

        return "{\"status\":\"OK\",\"message\":\"Poll " + pollId + " created successfully.\"}";
    }
}