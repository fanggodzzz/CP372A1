package Server;

import java.io.*;
import java.net.Socket;
import BulletinBoard.Board;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Board board;
    private final int clientNum;
    private static final int MAX_LINE_LENGTH = 1024;

    public ClientHandler(Socket socket, Board board, int clientNum) {
        this.socket = socket;
        this.board = board;
        this.clientNum = clientNum;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            // Step 1: Set up I/O streams for communication with the client
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Step 2: Initialize the protocol handler (manages knock-knock joke state)
            BBoardProtocol protocol = new BBoardProtocol(board);

            // Step 3: Server return accept connection
            String serverResponse = protocol.processInput(null);
            out.println(serverResponse);
            System.out.println("Server: " + serverResponse);

            // Step 4: Read client input and respond until conversation ends
            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                String line = in.readLine();

                // DoS avoid
                if (line == null || line.length() > MAX_LINE_LENGTH) {
                    out.println("ERROR MESSAGE_TOO_LARGE");
                    return;
                }

                System.out.println("Client " + clientNum + ": " + clientInput);
                
                serverResponse = protocol.processInput(clientInput.trim());
                out.println(serverResponse);
                System.out.println("Server: " + serverResponse);

                // Exit loop when the conversation is complete
                if (serverResponse.equals("CONNECTION_CLOSED")) {
                    break;
                }
            }

            System.out.println("Client " + clientNum + " disconnected. Conversation ended");

        } catch (IOException e) {
            System.out.println("Client " + clientNum + " disconnected.");

        } finally {
            // Step 5: Always close resources to prevent memory leaks
            // Close in the reverse order they were opened
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
                System.out.println("Client " + clientNum + " - All resources closed.");
            } catch (IOException e) {
                System.err.println("Client " + clientNum + " - Error closing resources: " + e.getMessage());
            }
        }
    }

}