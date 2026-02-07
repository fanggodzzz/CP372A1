// ClientGUI.java
// GUI with board drawing + output log (basic Java style)

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientGUI extends JFrame {

    // Connection controls
    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;
    private JButton disconnectButton;
    
    // Raw command controls
    private JTextField commandField;
    private JButton sendButton;
    
    // Action buttons
    private JButton postButton;
    private JButton getButton;
    private JButton pinButton;
    private JButton unpinButton;
    private JButton shakeButton;
    private JButton clearButton;

    // Output log
    private JTextArea outputArea;

    // Board drawing
    private BoardPanel boardPanel;

    // Split pane (so we can force divider)
    private JSplitPane splitPane;

    // Network client
    private ProtocolClient client;

    public ClientGUI() {

        client = new ProtocolClient();

        setTitle("CP372 A1 Client");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== Top panel (2 rows) =====
        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        // Row 1: connection panel
        JPanel connectPanel = new JPanel();
        connectPanel.add(new JLabel("Server IP:"));
        ipField = new JTextField("127.0.0.1", 12);
        connectPanel.add(ipField);

        connectPanel.add(new JLabel("Port:"));
        portField = new JTextField("4554", 6);
        connectPanel.add(portField);

        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        connectPanel.add(connectButton);
        connectPanel.add(disconnectButton);

        topPanel.add(connectPanel);

        // Row 2: raw command panel
        JPanel commandPanel = new JPanel(new BorderLayout());
        commandPanel.add(new JLabel("Raw Command:"), BorderLayout.WEST);

        commandField = new JTextField();
        commandPanel.add(commandField, BorderLayout.CENTER);

        sendButton = new JButton("Send Command");
        commandPanel.add(sendButton, BorderLayout.EAST);

        topPanel.add(commandPanel);

        add(topPanel, BorderLayout.NORTH);

        // ===== Bottom panel (buttons) =====
        JPanel buttonPanel = new JPanel();

        postButton = new JButton("POST");
        getButton = new JButton("GET");
        pinButton = new JButton("PIN");
        unpinButton = new JButton("UNPIN");
        shakeButton = new JButton("SHAKE");
        clearButton = new JButton("CLEAR");

        buttonPanel.add(postButton);
        buttonPanel.add(getButton);
        buttonPanel.add(pinButton);
        buttonPanel.add(unpinButton);
        buttonPanel.add(shakeButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Center split: board (top) + output log (bottom) =====
        boardPanel = new BoardPanel();
        boardPanel.setBoardConfig(200, 100, 20, 10);

        // DEBUG border so we can SEE the board panel exists
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

        // make sure it cannot collapse to zero
        boardPanel.setMinimumSize(new Dimension(300, 300));
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane logScroll = new JScrollPane(outputArea);
        logScroll.setMinimumSize(new Dimension(300, 120));
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, boardPanel, logScroll);
        splitPane.setResizeWeight(0.80);
        splitPane.setDividerSize(10);
        
        add(splitPane, BorderLayout.CENTER);

        // ===== Actions =====
        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        sendButton.addActionListener(e -> sendRawCommand());

        postButton.addActionListener(e -> doPostDialog());
        getButton.addActionListener(e -> sendCommand("GET"));
        pinButton.addActionListener(e -> doPinDialog());
        unpinButton.addActionListener(e -> doUnpinDialog());
        shakeButton.addActionListener(e -> sendCommand("SHAKE"));
        clearButton.addActionListener(e -> sendCommand("CLEAR"));
    }

    // Call AFTER setVisible(true) in ClientMain
    public void fixSplit() {
        SwingUtilities.invokeLater(() -> {
            if (splitPane != null) {
                splitPane.setDividerLocation(400); // pixels (very reliable)
            }
            revalidate();
            repaint();
        });
    }

    // ===== Connection =====

    private void connect() {
        try {
            String ip = ipField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());

            client.connect(ip, port);
            outputArea.append("Connected to " + ip + ":" + port + "\n");

        } catch (Exception e) {
            outputArea.append("ERROR: " + e.getMessage() + "\n");
        }
    }

    private void disconnect() {
        try {
            client.disconnect();
            outputArea.append("Disconnected\n");
        } catch (IOException e) {
            outputArea.append("ERROR disconnecting: " + e.getMessage() + "\n");
        }
    }

    // ===== Sending =====

    private void sendRawCommand() {
        String cmd = commandField.getText().trim();
        if (cmd.length() == 0) return;
        sendCommand(cmd);
    }

    private void sendCommand(String cmd) {
        try {
            outputArea.append("> " + cmd + "\n");
            client.sendLine(cmd);

            String response = client.readResponse();
            outputArea.append(response);

            // Update board only after GET (on Swing thread)
            if (cmd.trim().equalsIgnoreCase("GET")) {
                SwingUtilities.invokeLater(() -> updateBoardFromGetResponse(response));
            }

        } catch (Exception e) {
            outputArea.append("ERROR: " + e.getMessage() + "\n");
        }
    }

    // ===== Dialog helpers =====

    private void doPostDialog() {
        String x = JOptionPane.showInputDialog(this, "POST x:", "10");
        if (x == null) return;

        String y = JOptionPane.showInputDialog(this, "POST y:", "10");
        if (y == null) return;

        String color = JOptionPane.showInputDialog(this, "Color:", "red");
        if (color == null) return;

        String msg = JOptionPane.showInputDialog(this, "Message:", "Hello");
        if (msg == null) return;

        String cmd = "POST " + x.trim() + " " + y.trim() + " " + color.trim() + " " + msg;
        sendCommand(cmd);
    }

    private void doPinDialog() {
        String x = JOptionPane.showInputDialog(this, "PIN x:", "10");
        if (x == null) return;

        String y = JOptionPane.showInputDialog(this, "PIN y:", "10");
        if (y == null) return;

        sendCommand("PIN " + x.trim() + " " + y.trim());
    }

    private void doUnpinDialog() {
        String x = JOptionPane.showInputDialog(this, "UNPIN x:", "10");
        if (x == null) return;

        String y = JOptionPane.showInputDialog(this, "UNPIN y:", "10");
        if (y == null) return;

        sendCommand("UNPIN " + x.trim() + " " + y.trim());
    }

    // ===== Parse GET response and update board =====
    // Format:
    // OK N
    // NOTE x y color message  PINNED=false

    private void updateBoardFromGetResponse(String resp) {

        String[] lines = resp.split("\\r?\\n");
        List<BoardPanel.NoteDraw> notes = new ArrayList<>();

        for (String raw : lines) {
            String line = raw.trim();
            if (!line.startsWith("NOTE ")) continue;

            int pinIndex = line.indexOf("PINNED=");
            if (pinIndex < 0) continue;

            String left = line.substring(0, pinIndex).trim();
            String pinPart = line.substring(pinIndex).trim();

            boolean pinned = pinPart.toLowerCase().contains("true");

            String[] parts = left.split("\\s+");
            if (parts.length < 5) continue;

            int x, y;
            try {
                x = Integer.parseInt(parts[1]);
                y = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                continue;
            }

            String colorName = parts[3];
            Color c = BoardPanel.parseColor(colorName);

            StringBuilder msg = new StringBuilder();
            for (int k = 4; k < parts.length; k++) {
                if (k > 4) msg.append(" ");
                msg.append(parts[k]);
            }

            notes.add(new BoardPanel.NoteDraw(x, y, c, pinned, msg.toString()));
        }

        boardPanel.setNotes(notes);
        boardPanel.revalidate();
        boardPanel.repaint();
    }
}
