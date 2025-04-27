package UI;

import UI.CalendarComponents.Calender.Header;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class activateUI {

    public static final JFrame mainFrame = new JFrame();
    public static final JPanel calenderPanel = new JPanel(new BorderLayout());
    public static final JPanel chatRoomPanel = new JPanel(new BorderLayout());

    public static void main(String[] args) {

        // ChatRoom panel initialization
        ChatRoom chatRoom = new ChatRoom();
        JButton chatRoomButton = new JButton("Toggle Chat");
        chatRoomButton.setPreferredSize(new Dimension(100, 30));
        chatRoomPanel.add(chatRoomButton, BorderLayout.SOUTH);
        chatRoomPanel.add(chatRoom, BorderLayout.CENTER);
        chatRoomPanel.setPreferredSize(new Dimension(300, 600));
        chatRoomPanel.setVisible(true);
        chatRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatRoom.setVisible(!chatRoom.isVisible());
            }
        });

        // Calendar panel initialization
        Header header = new Header();
        calenderPanel.add(header);

        // MainFrame initialization
        mainFrame.setSize(1200, 800);
        mainFrame.setMinimumSize(new Dimension(800, 600));
        //mainFrame.add(calenderPanel, BorderLayout.CENTER);
        mainFrame.add(calenderPanel, BorderLayout.CENTER);
        mainFrame.add(chatRoomPanel, BorderLayout.EAST);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
}