import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientGUI extends JFrame {
    private final JTextField ipField = new JTextField("127.0.0.1");
    private final JTextField portField = new JTextField("4554");
    private final JButton connectBtn = new JButton("Connect");
    private final JButton disconnectBtn = new JButton("Disconnect");

    private final JTextField commandField = new JTextField();
    private final JButton sendBtn = new JButton("Send Command");

    private final JButton postBtn = new JButton("POST...");
    private final JButton getBtn = new JButton("GET...");
    private final JButton pinBtn = new JButton("PIN...");
    private final JButton unpinBtn = new JButton("UNPIN...");
    private final JButton shakeBtn = new JButton("SHAKE");
    private final JButton clearBtn = new JButton("CLEAR");

    private final JTextArea output = new JTextArea();

    private final ProtocolClient client = new ProtocolClient();

    public ClientGUI() {
        super("CP372 A1 Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(buildConnectionPanel());
        top.add(buildCommandPanel());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(postBtn);
        actions.add(getBtn);
        actions.add(pinBtn);
        actions.add(unpinBtn);
        actions.add(shakeBtn);
        actions.add(clearBtn);

        add(top, BorderLayout.NORTH);
        add(actions, BorderLayout.CENTER);
        add(new JScrollPane(output), BorderLayout.SOUTH);

        // Make output area taller
        ((JScrollPane)getContentPane().getComponent(2)).setPreferredSize(new Dimension(900, 420));

        wireEvents();
        setButtonsConnected(false);
    }

    private JPanel buildConnectionPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("Server IP:"));
        ipField.setColumns(12);
        p.add(ipField);

        p.add(new JLabel("Port:"));
        portField.setColumns(6);
        p.add(portField);

        p.add(connectBtn);
        p.add(disconnectBtn);
        return p;
    }

    private JPanel buildCommandPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.add(new JLabel("Raw Command:"), BorderLayout.WEST);
        p.add(commandField, BorderLayout.CENTER);
        p.add(sendBtn, BorderLayout.EAST);
        return p;
    }

    private void wireEvents() {
        connectBtn.addActionListener(e -> onConnect());
        disconnectBtn.addActionListener(e -> onDisconnect());

        sendBtn.addActionListener(e -> {
            String cmd = commandField.getText().trim();
            if (cmd.isEmpty()) return;
            sendAndShow(cmd);
        });

        postBtn.addActionListener(e -> onPostDialog());
        getBtn.addActionListener(e -> onGetDialog());
        pinBtn.addActionListener(e -> onPinDialog());
        unpinBtn.addActionListener(e -> onUnpinDialog());
        shakeBtn.addActionListener(e -> sendAndShow("SHAKE"));
        clearBtn.addActionListener(e -> sendAndShow("CLEAR"));
    }

    private void onConnect() {
        String host = ipField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            append("ERROR: Port must be an integer.\n");
            return;
        }

        try {
            client.connect(host, port);
            append("Connected to " + host + ":" + port + "\n");
            setButtonsConnected(true);
        } catch (IOException ex) {
            append("ERROR: Could not connect: " + ex.getMessage() + "\n");
            setButtonsConnected(false);
        }
    }

    private void onDisconnect() {
        try {
            if (client.isConnected()) {
                append("> DISCONNECT\n");
                client.disconnectGracefully();
                append("(Disconnected)\n");
            }
        } catch (IOException ex) {
            append("ERROR while disconnecting: " + ex.getMessage() + "\n");
        } finally {
            setButtonsConnected(false);
        }
    }

    private void onPostDialog() {
        String x = JOptionPane.showInputDialog(this, "POST x:", "10");
        if (x == null) return;
        String y = JOptionPane.showInputDialog(this, "POST y:", "10");
        if (y == null) return;
        String color = JOptionPane.showInputDialog(this, "Color (must be allowed by server):", "white");
        if (color == null) return;
        String msg = JOptionPane.showInputDialog(this, "Message:", "Hello");
        if (msg == null) return;

        String cmd = "POST " + x.trim() + " " + y.trim() + " " + color.trim() + " " + msg;
        sendAndShow(cmd);
    }

    private void onGetDialog() {
        String color = JOptionPane.showInputDialog(this, "GET filter: color=<color> (blank = none)", "");
        if (color == null) return;
        String contains = JOptionPane.showInputDialog(this, "GET filter: contains=<x> <y> (e.g., 15 12) (blank = none)", "");
        if (contains == null) return;
        String refers = JOptionPane.showInputDialog(this, "GET filter: refersTo=<text> (blank = none)", "");
        if (refers == null) return;

        StringBuilder cmd = new StringBuilder("GET");
        if (!color.trim().isEmpty()) cmd.append(" color=").append(color.trim());
        if (!contains.trim().isEmpty()) cmd.append(" contains=").append(contains.trim());
        if (!refers.trim().isEmpty()) cmd.append(" refersTo=").append(refers.trim());

        sendAndShow(cmd.toString());
    }

    private void onPinDialog() {
        String x = JOptionPane.showInputDialog(this, "PIN x:", "15");
        if (x == null) return;
        String y = JOptionPane.showInputDialog(this, "PIN y:", "12");
        if (y == null) return;
        sendAndShow("PIN " + x.trim() + " " + y.trim());
    }

    private void onUnpinDialog() {
        String x = JOptionPane.showInputDialog(this, "UNPIN x:", "15");
        if (x == null) return;
        String y = JOptionPane.showInputDialog(this, "UNPIN y:", "12");
        if (y == null) return;
        sendAndShow("UNPIN " + x.trim() + " " + y.trim());
    }

    private void sendAndShow(String cmd) {
        if (!client.isConnected()) {
            append("ERROR: Not connected.\n");
            return;
        }

        append("> " + cmd + "\n");

        new Thread(() -> {
            try {
                client.sendLine(cmd);
                String resp = client.readResponse();
                SwingUtilities.invokeLater(() -> append(resp));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> append("ERROR: " + ex.getMessage() + "\n"));
            }
        }).start();
    }

    private void setButtonsConnected(boolean connected) {
        connectBtn.setEnabled(!connected);
        disconnectBtn.setEnabled(connected);

        sendBtn.setEnabled(connected);
        commandField.setEnabled(connected);

        postBtn.setEnabled(connected);
        getBtn.setEnabled(connected);
        pinBtn.setEnabled(connected);
        unpinBtn.setEnabled(connected);
        shakeBtn.setEnabled(connected);
        clearBtn.setEnabled(connected);
    }

    private void append(String text) {
        output.append(text);
        output.setCaretPosition(output.getDocument().getLength());
    }
}
