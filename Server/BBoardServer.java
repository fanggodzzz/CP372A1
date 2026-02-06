package Server;

import java.net.*;
import java.io.*;
import java.util.*;
import BulletinBoard.*;

public class BBoardServer {

    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

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

        // ServerSocket serverSocket = new ServerSocket(port);
        // System.out.println("Server running on port " + port);

        // while (true) {
        //     Socket client = serverSocket.accept();
        //     new Thread(new ClientHandler(client, board)).start();
        // }
    }
}