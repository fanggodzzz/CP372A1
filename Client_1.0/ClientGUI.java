// ClientGUI.java
// Builds GUI and handles button clicks

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientGUI extends JFrame {

    private JTextField ipField;
    private JTextField portField;
    private JTextField commandField;

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
        setLayout(new BorderLayout()); // make sure BorderLayout is used

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));

        JPanel connectPanel = new JPanel();
        connectPanel.add(new JLabel("Server IP:"));
        ipField = new JTextField("127.0.0.1", 10);
        connectPanel.add(ipField);

        connectPanel.add(new JLabel("Port:"));
        portField = new JTextField("4554", 5);
        connectPanel.add(portField);

        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        connectPanel.add(connectButton);
        connectPanel.add(disconnectButton);

        topPanel.add(connectPanel);

        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(new JLabel("Raw Command:"), BorderLayout.WEST);
        commandField = new JTextField();
        commandPanel.add(commandField, BorderLayout.CENTER);
        sendButton = new JButton("Send Command");
        commandPanel.add(sendButton, BorderLayout.EAST);

        topPanel.add(commandPanel);

        add(topPanel, BorderLayout.NORTH);

        // Output area (MAKE IT LARGE by putting it in CENTER)
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel (move to SOUTH)
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

        postButton.addActionListener(e -> sendCommand("POST 10 10 white Hello"));
        getButton.addActionListener(e -> sendCommand("GET"));
        pinButton.addActionListener(e -> sendCommand("PIN 15 12"));
        unpinButton.addActionListener(e -> sendCommand("UNPIN 15 12"));
        shakeButton.addActionListener(e -> sendCommand("SHAKE"));
        clearButton.addActionListener(e -> sendCommand("CLEAR"));
    }

    private void connect() {
        try {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            client.connect(ip, port);
            outputArea.append(">Connected to server\n");
            String response = client.readResponse();
            outputArea.append(response);

        } catch (Exception e) {
            outputArea.append("ERROR: " + e.getMessage() + "\n");
        }
    }

    private void disconnect() {
        try {
            sendCommand("DISCONNECT");
            String response = client.readResponse();
            outputArea.append(response);
            client.disconnect();
            outputArea.append(">Disconnected\n");
        } catch (IOException e) {
            outputArea.append("ERROR disconnecting\n");
        }
    }

    private void sendCommand() {
        String cmd = commandField.getText();
        sendCommand(cmd);
    }

    private void sendCommand(String cmd) {
        try {
            outputArea.append("> " + cmd + "\n");
            client.sendLine(cmd);
            String response = client.readResponse();
            outputArea.append(response);

            // auto-scroll to bottom
            outputArea.setCaretPosition(outputArea.getDocument().getLength());

        } catch (Exception e) {
            outputArea.append("ERROR: " + e.getMessage() + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }
    }
}
