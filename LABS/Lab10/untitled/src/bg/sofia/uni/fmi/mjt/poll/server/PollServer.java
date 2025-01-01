package bg.sofia.uni.fmi.mjt.poll.server;

import bg.sofia.uni.fmi.mjt.poll.server.repository.PollRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PollServer {
    private final int port;
    private final PollRepository pollRepository;
    private final ExecutorService executorService;
    private ServerSocket serverSocket;

    private static final int MAX_THREADS = 10;

    public PollServer(int port, PollRepository pollRepository) {
        this.port = port;
        this.pollRepository = pollRepository;
        this.executorService = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Poll server started on port " + port);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new PollClientHandler(clientSocket, pollRepository));
            }
        } catch (IOException e) {
            System.out.println("Poll server could not be started: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            executorService.shutdown();
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Poll server could not be stopped: " + e.getMessage());
        }
    }

}
