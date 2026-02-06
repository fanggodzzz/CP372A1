// ProtocolClient.java
// To handles talking to the server using TCP sockets

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;

public class ProtocolClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    // Check if the client is connected
    public boolean isConnected() {
        if (socket == null) return false;
        if (socket.isClosed()) return false;
        return true;
    }
    // Connect to the server
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
        out = new PrintWriter(
                socket.getOutputStream(), true
        );
    }
    // Send command to the server
    public void sendLine(String line) {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected");
        }
        out.println(line);
    }
    // Read response from the server
    public String readResponse() throws IOException {
        String firstLine = in.readLine();
        if (firstLine == null) {
            throw new IOException("Server closed connection");
        }
        // If response starts with OK
        if (firstLine.startsWith("OK ")) {
            String rest = firstLine.substring(3);
            // To read number after OK
            try {
                int count = Integer.parseInt(rest.trim());
                String result = firstLine + "\n";
                for (int i = 0; i < count; i++) {
                    result = result + in.readLine() + "\n";
                }
                return result;
            } catch (NumberFormatException e) {
                // OK but no number
                return firstLine + "\n";
            }
        }
        // ERROR response
        return firstLine + "\n";
    }
    // Disconnect
    public void disconnect() throws IOException {
        if (isConnected()) {
            out.println("DISCONNECT");
            try {
                readResponse();
            } catch (Exception e) {
                // ignore
            }
            socket.close();
        }
    }
}

