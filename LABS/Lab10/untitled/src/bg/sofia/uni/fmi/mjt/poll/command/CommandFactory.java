package bg.sofia.uni.fmi.mjt.poll.command;

import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

public class CommandFactory {

    public static String handleCommand(String commandLine, PollRepository pollRepository) {
        String[] tokens = commandLine.split(" ", 2);
        String commandName = tokens[0];

        Command command;
        switch (commandName.toLowerCase()) {
            case "create-poll":
                command = new CreatePollCommand(tokens[1]);
                break;
            case "list-polls":
                command = new ListPollsCommand();
                break;
            case "submit-vote":
                command = new SubmitVoteCommand(tokens[1]);
                break;
            case "disconnect":
                return "Goodbye!";
            default:
                return "{\"status\":\"ERROR\",\"message\":\"Invalid command.\"}";
        }

        return command.execute(pollRepository);
    }
}