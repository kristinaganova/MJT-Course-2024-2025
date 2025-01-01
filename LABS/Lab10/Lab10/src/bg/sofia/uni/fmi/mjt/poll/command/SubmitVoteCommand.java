package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;

import java.util.Map;

public class SubmitVoteCommand implements Command {

    private final PollRepository repository;

    public SubmitVoteCommand(PollRepository repository) {
        this.repository = repository;
    }

    @Override
    public String execute(String[] args) {
        if (args.length != 2) {
            return "{\"status\":\"ERROR\",\"message\":\"Usage: submit-vote <poll-id> <option>\"}";
        }

        int pollId;
        try {
            pollId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return "{\"status\":\"ERROR\",\"message\":\"Invalid poll ID.\"}";
        }

        Poll poll = repository.getPoll(pollId);
        if (poll == null) {
            return "{\"status\":\"ERROR\",\"message\":\"Poll with ID " + pollId + " does not exist.\"}";
        }

        String option = args[1];
        Map<String, Integer> options = poll.options();
        if (!options.containsKey(option)) {
            return "{\"status\":\"ERROR\",\"message\":\"Invalid option. Option " + option + " does not exist.\"}";
        }

        options.put(option, options.get(option) + 1);
        return "{\"status\":\"OK\",\"message\":\"Vote submitted successfully for option: " + option + "\"}";
    }
}