package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel viewPanel;
    private DefaultListModel<String> scheduleModel = new DefaultListModel<>();;

    public CalendarUI() {
        setTitle("行事曆");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 上方按鈕區域
        JPanel buttonPanel = new JPanel();
        JButton weekButton = new JButton("週");
        JButton monthButton = new JButton("月");
        JButton yearButton = new JButton("年");

        buttonPanel.add(weekButton);
        buttonPanel.add(monthButton);
        buttonPanel.add(yearButton);

        // 中央視圖切換區域
        cardLayout = new CardLayout();
        viewPanel = new JPanel(cardLayout);

        // 各種視圖內容（可自行擴充）
        JPanel weekView = createWeekView();
        JPanel monthView = createMonthView();
        JPanel yearView = createYearView();

        viewPanel.add(weekView, "Week");
        viewPanel.add(monthView, "Month");
        viewPanel.add(yearView, "Year");

        // 按鈕事件綁定
        weekButton.addActionListener(e -> cardLayout.show(viewPanel, "Week"));
        monthButton.addActionListener(e -> cardLayout.show(viewPanel, "Month"));
        yearButton.addActionListener(e -> cardLayout.show(viewPanel, "Year"));

        // 加入元件到主視窗
        add(buttonPanel, BorderLayout.NORTH);
        add(viewPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createWeekView() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("週視圖 - 今日是：" + LocalDate.now(), SwingConstants.CENTER);
        label.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        // 日程列表
        JList<String> scheduleList = new JList<>(scheduleModel);
        JScrollPane scrollPane = new JScrollPane(scheduleList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 新增事件按鈕
        JButton addButton = new JButton("新增事件");
        addButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(CalendarUI.this, "輸入事件內容：");
            if (input != null && !input.trim().isEmpty()) {
                scheduleModel.addElement("🗓️ " + input.trim());
            }
        });

        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMonthView() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("月視圖 - 今日是：" + LocalDate.now(), SwingConstants.CENTER);
        label.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        // 日程列表
        JList<String> scheduleList = new JList<>(scheduleModel);
        JScrollPane scrollPane = new JScrollPane(scheduleList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 新增事件按鈕
        JButton addButton = new JButton("新增事件");
        addButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(CalendarUI.this, "輸入事件內容：");
            if (input != null && !input.trim().isEmpty()) {
                scheduleModel.addElement("🗓️ " + input.trim());
            }
        });

        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createYearView() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("年視圖 - 今日是：" + LocalDate.now(), SwingConstants.CENTER);
        label.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        // 日程列表
        JList<String> scheduleList = new JList<>(scheduleModel);
        JScrollPane scrollPane = new JScrollPane(scheduleList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 新增事件按鈕
        JButton addButton = new JButton("新增事件");
        addButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(CalendarUI.this, "輸入事件內容：");
            if (input != null && !input.trim().isEmpty()) {
                scheduleModel.addElement("🗓️ " + input.trim());
            }
        });

        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

}
