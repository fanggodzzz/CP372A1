import java.io.*;
import java.net.Socket;

public class ProtocolClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public void sendLine(String line) {
        if (!isConnected()) throw new IllegalStateException("Not connected.");
        out.println(line); // adds \n
        out.flush();
    }

    /**
     * Reads exactly ONE server response.
     * Supports:
     *  - OK <text>
     *  - OK <number> followed by <number> data lines (GET results)
     *  - ERROR <code> <message>
     */
    public String readResponse() throws IOException {
        if (!isConnected()) throw new IllegalStateException("Not connected.");

        String first = in.readLine();
        if (first == null) throw new IOException("Server closed connection.");

        if (first.startsWith("OK ")) {
            String rest = first.substring(3).trim();
            try {
                int n = Integer.parseInt(rest);
                StringBuilder sb = new StringBuilder();
                sb.append(first).append("\n");
                for (int i = 0; i < n; i++) {
                    String line = in.readLine();
                    if (line == null) throw new IOException("Server closed during multi-line response.");
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } catch (NumberFormatException ignored) {
                return first + "\n";
            }
        }

        return first + "\n";
    }

    public void disconnectGracefully() throws IOException {
        if (isConnected()) {
            sendLine("DISCONNECT");
            try { readResponse(); } catch (Exception ignored) {}
            close();
        }
    }

    public void close() throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
        out = null; in = null; socket = null;
    }
}
