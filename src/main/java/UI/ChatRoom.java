package UI;
//UI和事件
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//GPT API
import ChatGPT.ChatGPT;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.Scanner;


public class ChatRoom extends JPanel {
    private final JTextField userQueryField;
    private final JTextArea chatArea;
    private final JButton sendButton;

    public ChatRoom(){
        // Set up the main panel
        setPreferredSize(new Dimension(300, 600));
        setLayout(new BorderLayout());

        // Create components
        userQueryField = new JTextField();
        chatArea = new JTextArea();
        userQueryField.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14)); // 輸入欄字體
        chatArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 14));       // 聊天區字體
        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 30));       // 調整按鈕大小
        sendButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 14));      // 調整按鈕字體

        // Configure chat area
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setPreferredSize(new Dimension(300, 500));

        // Create panels
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(300, 40));
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Add components to input panel
        inputPanel.add(userQueryField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        //連接到ChatGPT api
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Error: OPENAI_API_KEY not found in .env file");
            System.exit(1);
        }

        ChatGPT chatGPT = new ChatGPT(apiKey);
        Scanner scanner = new Scanner(System.in);

        // Add main panel to frame
        add(mainPanel);
        chatArea.append("您好，我可以如何幫助您?\n");
        // Add action listener to send button
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = userQueryField.getText();
                if (!query.isEmpty()) {
                    // Add user message to chat area
                    chatArea.append("你: " + query + "\n");
                    // Clear input field
                    userQueryField.setText("");
                    //接收回應
                    try {
                        String response = chatGPT.chat(query);
                        chatArea.append("助理: " + response);
                    } catch (IOException ev) {
                        chatArea.append("Error: " + ev.getMessage());
                    }
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