package bg.sofia.uni.fmi.mjt.poll.command;

public class DisconnectCommand implements Command {

    @Override
    public String execute(String[] args) {
        return "DISCONNECT";
    }
}