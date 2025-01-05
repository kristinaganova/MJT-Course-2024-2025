package bg.sofia.uni.fmi.mjt.poll;

import bg.sofia.uni.fmi.mjt.poll.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.poll.command.CreatePollCommand;
import bg.sofia.uni.fmi.mjt.poll.command.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.poll.command.ListPollsCommand;
import bg.sofia.uni.fmi.mjt.poll.command.SubmitVoteCommand;
import bg.sofia.uni.fmi.mjt.poll.server.repository.InMemoryPollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class PollServer {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";

    private final int port;
    private final CommandExecutor commandExecutor;

    private boolean isRunning;
    private Selector selector;
    private ByteBuffer buffer;

    public PollServer(int port, PollRepository pollRepository) {
        this.port = port;
        this.commandExecutor = new CommandExecutor();
        initializeCommands(pollRepository);
    }

    private void initializeCommands(PollRepository repository) {
        commandExecutor.registerCommand("create-poll", new CreatePollCommand(repository));
        commandExecutor.registerCommand("list-polls", new ListPollsCommand(repository));
        commandExecutor.registerCommand("submit-vote", new SubmitVoteCommand(repository));
        commandExecutor.registerCommand("disconnect", new DisconnectCommand());
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            setupServer(serverSocketChannel);

            while (isRunning) {
                processReadyKeys();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    private void setupServer(ServerSocketChannel serverSocketChannel) throws IOException {
        selector = Selector.open();
        serverSocketChannel.bind(new InetSocketAddress(HOST, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        isRunning = true;
    }

    private void processReadyKeys() throws IOException {
        selector.select();
        for (SelectionKey key : selector.selectedKeys()) {
            if (key.isAcceptable()) {
                handleAcceptableKey((ServerSocketChannel) key.channel());
            } else if (key.isReadable()) {
                handleReadableKey((SocketChannel) key.channel());
            }
        }
        selector.selectedKeys().clear();
    }

    private void handleAcceptableKey(ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel clientChannel = acceptClient(serverSocketChannel);
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    private SocketChannel acceptClient(ServerSocketChannel serverSocketChannel) throws IOException {
        return serverSocketChannel.accept();
    }

    private void handleReadableKey(SocketChannel clientChannel) throws IOException {
        String request = readFromClient(clientChannel);
        if (request == null) {
            clientChannel.close();
            return;
        }

        String response = processRequest(request);
        sendResponse(clientChannel, response);

        if ("DISCONNECT".equals(response)) {
            clientChannel.close();
        }
    }

    private String readFromClient(SocketChannel clientChannel) throws IOException {
        buffer.clear();
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            return null;
        }

        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit()).trim();
    }

    private String processRequest(String request) {
        String[] tokens = request.split(" ", 2);
        String commandName = tokens[0];
        String[] args = tokens.length > 1 ? tokens[1].split(" ") : new String[0];
        return commandExecutor.executeCommand(commandName, args);
    }

    private void sendResponse(SocketChannel clientChannel, String response) throws IOException {
        buffer.clear();
        buffer.put(response.getBytes());
        buffer.flip();
        clientChannel.write(buffer);
    }

    public void stop() {
        isRunning = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private static final int PORT = 8080;

    public static void main(String[] args) {
        PollRepository repository = new InMemoryPollRepository();
        PollServer server = new PollServer(PORT, repository);
        server.start();
    }
}
