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


public class ChatRoom extends JFrame {
    private JTextField userQueryField;
    private JTextArea chatArea;
    private JButton sendButton;

    public ChatRoom(){
        // Set up the main frame
        setTitle("Calendar Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);

        // Create components
        userQueryField = new JTextField();
        chatArea = new JTextArea();
        userQueryField.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16)); // 輸入欄字體
        chatArea.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 16));       // 聊天區字體
        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(100, 40));       // 調整按鈕大小
        sendButton.setFont(new Font("Microsoft JhengHei", Font.BOLD, 16));      // 調整按鈕字體

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