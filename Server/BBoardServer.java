package Server;

import java.net.*;
import java.io.*;
import java.util.*;
import BulletinBoard.*;

public class BBoardServer {

    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {

        // Parse command-line arguments for port
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    System.err.println("Error: Port must be between 1 and 65535. Using default port " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid port number '" + args[0] + "'. Using default port " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        // Parse command-line arguments for numerical parameters 
        int bWid, bHei, nWid, nHei;
        try {
            bWid = Integer.parseInt(args[1]);
            bHei = Integer.parseInt(args[2]);
            nWid = Integer.parseInt(args[3]);
            nHei = Integer.parseInt(args[4]);
        }
        catch (NumberFormatException e) {
            System.err.println("Please use numerical value for board width, board height, note width and note height");
            return;
        }

        // Extract colours
        String[] colours = new String[args.length - 5]; 
        for (int i = 5; i < args.length; ++ i) {
            colours[i - 5] = args[i];
        }
    
        // Creat new board
        Board board = new Board(bWid, bHei, nWid, nHei, colours);

        ServerSocket serverSocket = null;

        try {
            // Step 1: Create a ServerSocket to listen for incoming connections
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port + ". Waiting for client connections...");

            // Step 2: Accept a client connection (blocks until a client connects) 
            // and create one thread for each client

            int clientNum = 0;
            while (true) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept(); // blocks
                    ClientHandler handler = new ClientHandler(clientSocket, board, ++ clientNum);
                    new Thread(handler).start(); // new thread per client
                    
                } catch (IOException e) {
                    // Handle I/O exceptions for new client 
                    System.err.println("New client " + (clientNum + 1) + " - Server error: " + e.getMessage());
                    e.printStackTrace();
                    
                } finally {
                    // Always close resources to prevent memory leaks
                    try {
                        if (clientSocket != null) clientSocket.close();
                        System.out.println("New client " + (clientNum + 1) +" - Connection closed");
                    } catch (IOException e) {
                        System.err.println("New client " + (clientNum + 1) +" - Error closing resources: " + e.getMessage());
                    }
                }
        }

        } catch (IOException e) {
            // Handle I/O exceptions (connection refused, socket creation failed, etc.)
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // Step 3: Always close resources to prevent memory leaks
            // Close in the reverse order they were opened
            try {
                if (serverSocket != null) serverSocket.close();
                System.out.println("Server closed.");
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}