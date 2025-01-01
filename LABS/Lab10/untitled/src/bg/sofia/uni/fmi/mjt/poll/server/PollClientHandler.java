package bg.sofia.uni.fmi.mjt.poll.server;

import bg.sofia.uni.fmi.mjt.poll.command.CommandFactory;
import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PollClientHandler implements Runnable {
    private final Socket clientSocket;
    private final PollRepository pollRepository;

    public PollClientHandler(Socket clientSocket, PollRepository pollRepository) {
        this.clientSocket = clientSocket;
        this.pollRepository = pollRepository;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String commandLine;
            while ((commandLine = reader.readLine()) != null) {
                if ("disconnect".equalsIgnoreCase(commandLine.trim())) {
                    writer.println("{\"status\":\"OK\",\"message\":\"Disconnected successfully.\"}");
                    break;
                }

                String response = CommandFactory.handleCommand(commandLine, pollRepository);
                writer.println(response);
            }
        } catch (IOException e) {
            System.err.println("Error communicating with client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}