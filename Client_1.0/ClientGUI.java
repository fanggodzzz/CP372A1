// ClientGUI.java
// Builds GUI and handles button clicks

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientGUI extends JFrame {

    private JTextField ipField;
    private JTextField portField;
    private JTextField commandField;

    // Input
    private JTextField xField;
    private JTextField yField;
    private JTextField colorField;
    private JTextField messageField;

    private JButton connectButton;
    private JButton disconnectButton;
    private JButton sendButton;

    private JButton postButton;
    private JButton getButton;
    private JButton pinButton;
    private JButton unpinButton;
    private JButton shakeButton;
    private JButton clearButton;

    private JTextArea outputArea;
    private ProtocolClient client;
    private boolean connected = false;

    public ClientGUI() {

        client = new ProtocolClient();

        setTitle("CP372 A1 Client");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1));

        // Connect panel
        JPanel connectPanel = new JPanel();
        connectPanel.add(new JLabel("Server IP:"));
        ipField = new JTextField("10.0.0.193", 10);
        connectPanel.add(ipField);

        connectPanel.add(new JLabel("Port:"));
        portField = new JTextField("4554", 5);
        connectPanel.add(portField);

        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        connectPanel.add(connectButton);
        connectPanel.add(disconnectButton);

        topPanel.add(connectPanel);

        // Raw command
        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(new JLabel("Raw Command:"), BorderLayout.WEST);

        commandField = new JTextField();
        commandPanel.add(commandField, BorderLayout.CENTER);

        sendButton = new JButton("Send Command");
        commandPanel.add(sendButton, BorderLayout.EAST);

        topPanel.add(commandPanel);

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("X:"));
        xField = new JTextField("10", 4);
        inputPanel.add(xField);

        inputPanel.add(new JLabel("Y:"));
        yField = new JTextField("10", 4);
        inputPanel.add(yField);

        inputPanel.add(new JLabel("Color:"));
        colorField = new JTextField("white", 7);
        inputPanel.add(colorField);

        inputPanel.add(new JLabel("Message:"));
        messageField = new JTextField("Hello", 14);
        inputPanel.add(messageField);

        topPanel.add(inputPanel);

        add(topPanel, BorderLayout.NORTH);

        // Output
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
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

        // Initial state: not connected
        setCommandButtonsEnabled(false);

        // Button actions
        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        sendButton.addActionListener(e -> sendCommand());

        // POST using input fields (validated)
        postButton.addActionListener(e -> {
            if (!requireConnected()) return;

            String x = xField.getText().trim();
            String y = yField.getText().trim();
            String color = colorField.getText().trim();
            String msg = messageField.getText().trim();

            if (!isInt(x) || !isInt(y)) {
                clientError("POST requires integer X and Y (e.g., 10 10).");
                return;
            }
            if (color.isEmpty()) {
                clientError("POST requires a color (e.g., white).");
                return;
            }
            if (msg.isEmpty()) {
                clientError("POST requires a message.");
                return;
            }

            sendCommand("POST " + x + " " + y + " " + color + " " + msg);
        });

        // GET (validated)
        getButton.addActionListener(e -> {
            if (!requireConnected()) return;

            String x = xField.getText().trim();
            String y = yField.getText().trim();
            String color = colorField.getText().trim();
            String refers = messageField.getText().trim();

            boolean hasX = !x.isEmpty();
            boolean hasY = !y.isEmpty();

            if (hasX || hasY) {
                if (!(hasX && hasY)) {
                    clientError("GET contains= requires BOTH X and Y.");
                    return;
                }
                if (!isInt(x) || !isInt(y)) {
                    clientError("GET contains= requires integer X and Y.");
                    return;
                }
            }

            StringBuilder cmd = new StringBuilder("GET");

            if (!color.isEmpty()) {
                cmd.append(" color=").append(color);
            }
            if (hasX && hasY) {
                cmd.append(" contains=").append(x).append(" ").append(y);
            }
            if (!refers.isEmpty()) {
                cmd.append(" refersTo=").append(refers);
            }

            sendCommand(cmd.toString());
        });

        // PIN (validated)
        pinButton.addActionListener(e -> {
            if (!requireConnected()) return;

            String x = xField.getText().trim();
            String y = yField.getText().trim();

            if (!isInt(x) || !isInt(y)) {
                clientError("PIN requires integer X and Y (e.g., 15 12).");
                return;
            }

            sendCommand("PIN " + x + " " + y);
        });

        // UNPIN (validated)
        unpinButton.addActionListener(e -> {
            if (!requireConnected()) return;

            String x = xField.getText().trim();
            String y = yField.getText().trim();

            if (!isInt(x) || !isInt(y)) {
                clientError("UNPIN requires integer X and Y (e.g., 15 12).");
                return;
            }

            sendCommand("UNPIN " + x + " " + y);
        });

        shakeButton.addActionListener(e -> {
            if (!requireConnected()) return;
            sendCommand("SHAKE");
        });

        clearButton.addActionListener(e -> {
            if (!requireConnected()) return;
            sendCommand("CLEAR");
        });
    }

    private void connect() {
        try {
            String ip = ipField.getText().trim();
            if (ip.isEmpty()) {
                clientError("IP address cannot be empty.");
                return;
            }

            String portStr = portField.getText().trim();
            if (!isInt(portStr)) {
                clientError("Port must be an integer (e.g., 4554).");
                return;
            }

            int port = Integer.parseInt(portStr);
            client.connect(ip, port);

            String response = client.readResponse();
            if (response != null) outputArea.append(response);

            outputArea.append("Connected to server\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());

            connected = true;
            setCommandButtonsEnabled(true);

        } catch (Exception e) {
            connected = false;
            setCommandButtonsEnabled(false);

            outputArea.append("ERROR: " + e.getMessage() + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }
    }

    private void disconnect() {
    	try {
        	client.disconnect();         
        	outputArea.append("Disconnected\n"); 
        	outputArea.setCaretPosition(outputArea.getDocument().getLength());
    	} catch (IOException e) {
        	outputArea.append("ERROR disconnecting\n");
        	outputArea.setCaretPosition(outputArea.getDocument().getLength());
    		}
	}


    private void sendCommand() {
        if (!requireConnected()) return;

        String cmd = commandField.getText().trim();
        if (cmd.isEmpty()) {
            clientError("Raw Command is empty.");
            return;
    }

        String err = validateRawCommand(cmd);
        if (err != null) {
            clientError(err);
            return;
    }

        sendCommand(cmd);
}


    private void sendCommand(String cmd) {
        try {
            outputArea.append("> " + cmd + "\n");
            client.sendLine(cmd);

            String response = client.readResponse();
            if (response != null) outputArea.append(response);

            outputArea.setCaretPosition(outputArea.getDocument().getLength());

        } catch (Exception e) {
            outputArea.append("ERROR: " + e.getMessage() + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());

            connected = false;
            setCommandButtonsEnabled(false);
        }
    }

    // Validation helpers 

    private boolean isInt(String s) {
        if (s == null) return false;
        s = s.trim();
        if (s.isEmpty()) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void clientError(String msg) {
        outputArea.append("CLIENT ERROR: " + msg + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    private boolean requireConnected() {
        if (!connected) {
            clientError("You must CONNECT before sending commands.");
            return false;
        }
        return true;
    }

    private void setCommandButtonsEnabled(boolean enabled) {
        postButton.setEnabled(enabled);
        getButton.setEnabled(enabled);
        pinButton.setEnabled(enabled);
        unpinButton.setEnabled(enabled);
        shakeButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        sendButton.setEnabled(enabled); 
        disconnectButton.setEnabled(enabled);
        connectButton.setEnabled(!enabled);
    }

    private String validateRawCommand(String cmd) {
        String[] parts = cmd.trim().split("\\s+");
        if (parts.length == 0) return "Raw Command is empty.";
    
        String op = parts[0].toUpperCase();
    
        switch (op) {
            case "POST":
                // POST x y color message...
                if (parts.length < 5) return "POST format: POST <x> <y> <color> <message>";
                if (!isInt(parts[1]) || !isInt(parts[2])) return "POST: x and y must be integers.";
                if (parts[3].trim().isEmpty()) return "POST: color cannot be empty.";
                return null;
    
            case "PIN":
            case "UNPIN":
                if (parts.length != 3) return op + " format: " + op + " <x> <y>";
                if (!isInt(parts[1]) || !isInt(parts[2])) return op + ": x and y must be integers.";
                return null;
    
            case "GET":
                if (parts.length == 2 && parts[1].equalsIgnoreCase("PINS")) return null;
    
                // validate contains=if present needs both x and y ints
                for (int i = 1; i < parts.length; i++) {
                    String t = parts[i].toLowerCase();
                    if (t.startsWith("contains=")) {
                        String xStr = parts[i].substring("contains=".length()).trim();
                        if (xStr.isEmpty()) return "GET: contains= requires X and Y (e.g., contains=10 10).";
                        if (i + 1 >= parts.length) return "GET: contains= requires BOTH X and Y.";
                        String yStr = parts[i + 1];
                        if (yStr.contains("=")) return "GET: contains= requires BOTH X and Y.";
                        if (!isInt(xStr) || !isInt(yStr)) return "GET: contains= requires integer X and Y.";
                    }
                }
                return null;
    
            case "SHAKE":
            case "CLEAR":
            case "DISCONNECT":
                if (parts.length != 1) return op + " takes no parameters.";
                return null;
    
            default:
                return "Unknown command. Use POST, GET, PIN, UNPIN, SHAKE, CLEAR, DISCONNECT.";
        }
    }
}
    






