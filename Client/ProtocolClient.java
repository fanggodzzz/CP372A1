// ProtocolClient.java
// Basic TCP client for CP372 A1 protocol
// IMPORTANT: readResponse() handles multi-line GET responses (OK N + N NOTE lines)

import java.io.*;
import java.net.Socket;

public class ProtocolClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public void sendLine(String line) {
        out.println(line);
        out.flush();
    }

    public String readResponse() throws IOException {
        StringBuilder sb = new StringBuilder();

        String firstLine = in.readLine();
        if (firstLine == null) return "";

        sb.append(firstLine).append("\n");

        // If response is "OK N", read N more lines (NOTE lines)
        if (firstLine.startsWith("OK ")) {
            String[] parts = firstLine.split("\\s+");
            if (parts.length == 2) {
                try {
                    int n = Integer.parseInt(parts[1]);
                    for (int i = 0; i < n; i++) {
                        String line = in.readLine();
                        if (line != null) sb.append(line).append("\n");
                    }
                } catch (NumberFormatException e) {
                    // single-line OK
                }
            }
        }

        return sb.toString();
    }

    public void disconnect() throws IOException {
        if (socket != null) socket.close();
    }
}

