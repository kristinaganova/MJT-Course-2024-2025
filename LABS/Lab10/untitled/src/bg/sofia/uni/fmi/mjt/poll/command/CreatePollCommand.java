package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

import java.util.HashMap;
import java.util.Map;

public class CreatePollCommand implements Command {
    private final String args;

    private static final int MINIMUM_LENGTH = 3;
    public CreatePollCommand(String args) {
        this.args = args;
    }

    @Override
    public String execute(PollRepository pollRepository) {
        String[] tokens = args.split(" ");
        if (tokens.length < MINIMUM_LENGTH) {
            return "{\"status\":\"ERROR\",\"message\":\"Usage:" +
                    " create-poll <question> <option-1> <option-2> [... <option-N>]\"}";
        }

        String question = tokens[0];
        Map<String, Integer> options = createOptionsMap(tokens);

        Poll poll = new Poll(question, options);
        int pollId = pollRepository.addPoll(poll);

        return "{\"status\":\"OK\",\"message\":\"Poll " + pollId + " created successfully.\"}";
    }

    private Map<String, Integer> createOptionsMap(String[] tokens) {
        Map<String, Integer> optionsMap = new HashMap<>();
        for (int i = 1; i < tokens.length; i++) {
            optionsMap.put(tokens[i], 0);
        }
        return optionsMap;
    }
}
