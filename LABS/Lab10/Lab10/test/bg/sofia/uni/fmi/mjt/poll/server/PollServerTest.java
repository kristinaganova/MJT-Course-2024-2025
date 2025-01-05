package bg.sofia.uni.fmi.mjt.poll.server;

import bg.sofia.uni.fmi.mjt.poll.PollServer;
import bg.sofia.uni.fmi.mjt.poll.server.repository.InMemoryPollRepository;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;

class PollServerTest {
    private static final int TEST_PORT = 9090;
    private PollServer server;
    private Thread serverThread;

    @BeforeEach
    void setUp() {
        PollRepository repository = new InMemoryPollRepository();
        server = new PollServer(TEST_PORT, repository);

        serverThread = new Thread(server::start);
        serverThread.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
        try {
            serverThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testServerHandlesCreatePoll() throws IOException {
        try (SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress("localhost", TEST_PORT))) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            sendCommand(clientChannel, buffer, "create-poll Best-animal? Dog Cat");
            String response = readResponse(clientChannel, buffer);

            assertTrue(response.contains("Poll 1 created successfully"), "Server should create poll successfully");
        }
    }

    private void sendCommand(SocketChannel clientChannel, ByteBuffer buffer, String command) throws IOException {
        buffer.clear();
        buffer.put(command.getBytes());
        buffer.flip();
        clientChannel.write(buffer);
    }

    private String readResponse(SocketChannel clientChannel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        clientChannel.read(buffer);
        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit());
    }
}
