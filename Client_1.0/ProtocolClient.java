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

    StringBuilder sb = new StringBuilder();
    sb.append(firstLine).append("\n");

    if (firstLine.startsWith("OK")) {
        String[] parts = firstLine.trim().split("\\s+");
        if (parts.length >= 2) {
            String last = parts[parts.length - 1];
            try {
                int count = Integer.parseInt(last);
                for (int i = 0; i < count; i++) {
                    String line = in.readLine();
                    if (line == null) break; 
                    sb.append(line).append("\n");
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    return sb.toString();
}

    // Disconnect
    public String disconnect() throws IOException {
    if (!isConnected()) return "Already disconnected\n";

    out.println("DISCONNECT");
    out.flush();

    String response = in.readLine();
    if (response == null) response = "Server closed connection";

    try { in.close(); } catch (Exception ignored) {}
    try { out.close(); } catch (Exception ignored) {}
    try { socket.close(); } catch (Exception ignored) {}

    return response + "\n";
}
}




