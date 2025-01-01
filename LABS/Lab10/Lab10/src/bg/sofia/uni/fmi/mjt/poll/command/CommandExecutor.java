package bg.sofia.uni.fmi.mjt.poll.command;

import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {

    private final Map<String, Command> commands = new HashMap<>();

    public void registerCommand(String name, Command handler) {
        commands.put(name, handler);
    }

    public String executeCommand(String name, String[] args) {
        Command handler = commands.get(name);
        if (handler == null) {
            return "{\"status\":\"ERROR\",\"message\":\"Unknown command.\"}";
        }

        return handler.execute(args);
    }
}