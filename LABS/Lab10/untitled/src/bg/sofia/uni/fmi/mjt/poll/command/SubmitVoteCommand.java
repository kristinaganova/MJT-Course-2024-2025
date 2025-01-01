package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.model.Poll;

public class SubmitVoteCommand implements Command {
    private final String args;

    public SubmitVoteCommand(String args) {
        this.args = args;
    }

    @Override
    public String execute(PollRepository pollRepository) {
        String[] tokens = args.split(" ");
        if (tokens.length != 2) {
            return "{\"status\":\"ERROR\",\"message\":\"Usage: submit-vote <poll-id> <option>\"}";
        }

        try {
            int pollId = Integer.parseInt(tokens[0]);
            String option = tokens[1];

            Poll poll = pollRepository.getPoll(pollId);
            if (poll == null) {
                return "{\"status\":\"ERROR\",\"message\":\"Poll with ID " + pollId + " does not exist.\"}";
            }

            if (!poll.options().containsKey(option)) {
                return "{\"status\":\"ERROR\",\"message\":\"Invalid option. Option " + option + " does not exist.\"}";
            }

            poll.options().put(option, poll.options().get(option) + 1);
            return "{\"status\":\"OK\",\"message\":\"Vote submitted successfully for option: " + option + "\"}";
        } catch (NumberFormatException e) {
            return "{\"status\":\"ERROR\",\"message\":\"Poll ID must be a number.\"}";
        }
    }
}