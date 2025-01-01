package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

public interface Command {
    String execute(PollRepository pollRepository);
}