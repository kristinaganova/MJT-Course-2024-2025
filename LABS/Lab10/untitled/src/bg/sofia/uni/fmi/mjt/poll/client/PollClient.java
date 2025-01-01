package bg.sofia.uni.fmi.mjt.poll.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PollClient {
    private final String host;
    private final int port;

    public PollClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to Poll Server");

            String command;
            while ((command = consoleReader.readLine()) != null) {
                writer.println(command);
                String response = reader.readLine();
                System.out.println("Server response: " + response);

                if ("disconnect".equalsIgnoreCase(command.trim())) {
                    System.out.println("Disconnected from server.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        PollClient client = new PollClient("localhost", 8080);
        client.start();
    }
}