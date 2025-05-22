package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AIArrangeUI extends JFrame {

    public AIArrangeUI() {
        setTitle("AI 行程安排");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 主面板
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 事件名稱 Label + TextField
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("事件名稱:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        JTextField eventNameField = new JTextField();
        panel.add(eventNameField, gbc);

        // 行程下拉選單 Label + ComboBox
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("選擇行程:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        String[] schedules = {"行程A", "行程B", "行程C"};
        JComboBox<String> scheduleComboBox = new JComboBox<>(schedules);
        panel.add(scheduleComboBox, gbc);

        // 起始時間下拉選單 Label + ComboBox
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("開始時間:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        String[] times = new String[24];
        for (int i = 0; i < 24; i++) {
            times[i] = String.format("%02d:00", i);
        }
        JComboBox<String> startTimeComboBox = new JComboBox<>(times);

        panel.add(startTimeComboBox, gbc);

        // 結束時間下拉選單 Label + ComboBox
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("結束時間:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        JComboBox<String> endTimeComboBox = new JComboBox<>(times);
        panel.add(endTimeComboBox, gbc);

        // 每次持續多久下拉選單 Label + ComboBox
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("每次持續:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        String[] durations = {"15 分鐘", "30 分鐘", "45 分鐘", "60 分鐘"};
        JComboBox<String> durationComboBox = new JComboBox<>(durations);
        panel.add(durationComboBox, gbc);

        // 按鈕
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton arrangeButton = new JButton("安排");
        panel.add(arrangeButton, gbc);

        // 按鈕事件
        arrangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eventName = eventNameField.getText();
                String schedule = (String) scheduleComboBox.getSelectedItem();
                String startTime = (String) startTimeComboBox.getSelectedItem();
                String endTime = (String) endTimeComboBox.getSelectedItem();
                String duration = (String) durationComboBox.getSelectedItem();

                JOptionPane.showMessageDialog(AIArrangeUI.this,
                        "事件: " + eventName + "\n"
                                + "行程: " + schedule + "\n"
                                + "時間: " + startTime + " ~ " + endTime + "\n"
                                + "每次持續: " + duration,
                        "安排完成",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        add(panel);
    }
}
