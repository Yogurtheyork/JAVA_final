package UI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActiveUI {

    public static final JFrame mainFrame = new JFrame();
    public static final JPanel calendarPanel = new JPanel(new BorderLayout());
    public static final JPanel chatRoomPanel = new JPanel(new BorderLayout());
    public static final JPanel functionPanel = new JPanel(); // 左邊放功能按鈕的panel

    public ActiveUI() {

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
        CalendarUI calendarUI = new CalendarUI();
        calendarPanel.add(calendarUI, BorderLayout.CENTER);

        // Function panel initialization
        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.Y_AXIS)); // use BoxLayout
        functionPanel.setPreferredSize(new Dimension(200, 600));

        // --- 加入 Logo ---
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // use flowlayout left
        ImageIcon logoIcon = new ImageIcon("src/main/java/UI/logo.png");
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBorder(new LineBorder(Color.GRAY, 2, true)); //boarder
        logoLabel.setPreferredSize(new Dimension(200, 120)); // logo size
        logoPanel.add(logoLabel);
        functionPanel.add(logoPanel);

        // studennt id view
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel label1 = new JLabel("01257006");
        JLabel label2 = new JLabel("01257012");
        JLabel label3 = new JLabel("01257059");

        // size font
        label1.setFont(new Font("新細明體", Font.PLAIN, 20));
        label2.setFont(new Font("新細明體", Font.PLAIN, 20));
        label3.setFont(new Font("新細明體", Font.PLAIN, 20));

        infoPanel.add(label1);
        infoPanel.add(label2);
        infoPanel.add(label3);
        functionPanel.add(infoPanel);

        //down put toolbox
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        JToggleButton toolsButton = new JToggleButton("Tool Box");
        toolsButton.setPreferredSize(new Dimension(120, 60));
        toolsButton.setFont(new Font("Arial", Font.BOLD, 18));
        bottomPanel.add(toolsButton, BorderLayout.SOUTH);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new GridLayout(2, 2, 5, 5));
        toolsPanel.setVisible(false); // hidden

        JButton tool1 = new JButton("T1");
        JButton tool2 = new JButton("T2");
        JButton tool3 = new JButton("T3");
        JButton tool4 = new JButton("T4");
        JButton tool5 = new JButton("T5");
        JButton tool6 = new JButton("T6");

        toolsPanel.add(tool1);
        toolsPanel.add(tool2);
        toolsPanel.add(tool3);
        toolsPanel.add(tool4);
        toolsPanel.add(tool5);
        toolsPanel.add(tool6);

        bottomPanel.add(toolsPanel, BorderLayout.CENTER);
        functionPanel.add(bottomPanel);

        // toolsButton click open or close toolsPanel
        toolsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toolsPanel.setVisible(toolsButton.isSelected());
                functionPanel.revalidate();
                functionPanel.repaint();
            }
        });

        // MainFrame initialization
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(functionPanel, BorderLayout.WEST);
        mainFrame.add(calendarPanel, BorderLayout.CENTER);
        mainFrame.add(chatRoomPanel, BorderLayout.EAST);

        mainFrame.setSize(1200, 800);
        mainFrame.setMinimumSize(new Dimension(800, 600));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }
}
