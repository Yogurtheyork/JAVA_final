package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JFrame {
    private JTextField userQueryField;
    private JTextArea chatArea;
    private JButton sendButton;

    public UI() {
        // Set up the main frame
        setTitle("Calendar Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create components
        userQueryField = new JTextField();
        chatArea = new JTextArea();
        sendButton = new JButton("Send");

        // Configure chat area
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Create panels
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Add components to input panel
        inputPanel.add(userQueryField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Add action listener to send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = userQueryField.getText();
                if (!query.isEmpty()) {
                    // Add user message to chat area
                    chatArea.append("You: " + query + "\n");
                    // Clear input field
                    userQueryField.setText("");
                    // TODO: Process query and get response
                    // chatArea.append("Assistant: " + response + "\n");
                }
            }
        });

        // Add action listener to input field (Enter key)
        userQueryField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });
    }
}