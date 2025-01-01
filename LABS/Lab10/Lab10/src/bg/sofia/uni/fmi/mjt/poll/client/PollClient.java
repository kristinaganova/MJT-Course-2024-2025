package bg.sofia.uni.fmi.mjt.poll.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PollClient {

    private static final int BUFFER_SIZE = 1024;
    private static final int PORT = 8080;

    private final String host;
    private final int port;

    public PollClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(host, port));
            System.out.println("Connected to PollServer.");

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();

                if ("disconnect".equalsIgnoreCase(command.trim())) {
                    sendCommand(socketChannel, buffer, command);
                    System.out.println("Disconnected from server.");
                    break;
                }

                sendCommand(socketChannel, buffer, command);
                String response = receiveResponse(socketChannel, buffer);
                System.out.println(response);
            }

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private void sendCommand(SocketChannel socketChannel, ByteBuffer buffer, String command) throws IOException {
        buffer.clear();
        buffer.put(command.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        socketChannel.write(buffer);
    }

    private String receiveResponse(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        int bytesRead = socketChannel.read(buffer);
        if (bytesRead == -1) {
            throw new IOException("Server closed the connection.");
        }
        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        PollClient client = new PollClient("localhost", PORT);
        client.start();
    }
}