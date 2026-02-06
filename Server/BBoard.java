import java.net.*;
import java.io.*;
import java.util.*;

public class Board {
    public static void main(String[] args) throws IOException {

        int port = Integer.parseInt(args[0]);
        int bWid = Integer.parseInt(args[1]);
        int bHei = Integer.parseInt(args[2]);
        int nWid = Integer.parseInt(args[3]);
        int nHei = Integer.parseInt(args[4]);

        String[] colours = new String[args.length - 5]; 
        for (int i = 5; i < args.length; ++ i) {
            colours[i - 5] = args[i];
        }
    
        Board board = new Board(bWid, bHei, nWid, nHei, colours);

        // ServerSocket serverSocket = new ServerSocket(port);
        // System.out.println("Server running on port " + port);

        // while (true) {
        //     Socket client = serverSocket.accept();
        //     new Thread(new ClientHandler(client, board)).start();
        // }
    }
}