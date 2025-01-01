package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

public class ListPollsCommand implements Command {

    @Override
    public String execute(PollRepository pollRepository) {
        var polls = pollRepository.getAllPolls();
        if (polls.isEmpty()) {
            return "{\"status\":\"ERROR\",\"message\":\"No active polls available.\"}";
        }

        return "{\"status\":\"OK\",\"polls\":" + polls.toString() + "}";
    }
}