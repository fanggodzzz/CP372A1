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

        // Button actions
        
        connectButton.addActionListener(e -> connect());
        disconnectButton.addActionListener(e -> disconnect());
        sendButton.addActionListener(e -> sendCommand());

        // POST using input fields
        postButton.addActionListener(e -> {
            String x = xField.getText().trim();
            String y = yField.getText().trim();
            String color = colorField.getText().trim();
            String msg = messageField.getText().trim();
            sendCommand("POST " + x + " " + y + " " + color + " " + msg);
        });

        // GET 
        getButton.addActionListener(e -> sendCommand("GET"));

        // Dynamic PIN
        pinButton.addActionListener(e -> {
            String x = xField.getText().trim();
            String y = yField.getText().trim();
            sendCommand("PIN " + x + " " + y);
        });

        // Dynamic UNPIN
        unpinButton.addActionListener(e -> {
            String x = xField.getText().trim();
            String y = yField.getText().trim();
            sendCommand("UNPIN " + x + " " + y);
        });

        shakeButton.addActionListener(e -> sendCommand("SHAKE"));
        clearButton.addActionListener(e -> sendCommand("CLEAR"));
    }

    private void connect() {
        try {
            String ip = ipField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            client.connect(ip, port);

            String response = client.readResponse();
            if (response != null) outputArea.append(response);

            outputArea.append("Connected to server\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());

        } catch (Exception e) {
            outputArea.append("ERROR: " + e.getMessage() + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }
    }

    private void disconnect() {
    	try {
        	outputArea.append(client.disconnect());         
        	outputArea.append("Disconnected\n"); 
        	outputArea.setCaretPosition(outputArea.getDocument().getLength());
    	} catch (IOException e) {
        	outputArea.append("ERROR disconnecting\n");
        	outputArea.setCaretPosition(outputArea.getDocument().getLength());
    		}
	}

    private void sendCommand() {
        String cmd = commandField.getText().trim();
        if (!cmd.isEmpty()) {
            sendCommand(cmd);
        } else {
            outputArea.append("ERROR: Raw Command is empty\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }
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
        }
    }
}
