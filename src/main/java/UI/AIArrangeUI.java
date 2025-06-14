package UI;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.*;

import ChatGPT.EventArranger;
import DataStructures.*;

public class AIArrangeUI extends JFrame {
    private SimplifiedEvent TargetEvent = null;
    private String EventName = null;
    private final String[] begin = {"now", "event start"};
    private final String[] finish = {"3 months", "event end"};
    private final String[] duration = {"15 minutes", "30 minutes", "1 hour", "3 hours"};
    public AIArrangeUI(SimplifiedEvent TargetEvent) {
        this.TargetEvent = TargetEvent;
        this.EventName = TargetEvent.title;
        setTitle("AI 行程安排");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 事件名稱
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("事件名稱:"), gbc);
        gbc.gridx = 1;
        JTextField eventNameField = new JTextField(EventName);
        eventNameField.isValid();
        panel.add(eventNameField, gbc);

        // 選項
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("選項:"), gbc);
        gbc.gridx = 1;
        String[] eventOptions = {"安排學習計畫", "安排複習考試", "安排專案進度", "其他"};
        JComboBox<String> eventComboBox = new JComboBox<>(eventOptions);
        panel.add(eventComboBox, gbc);

        // 從
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("從:"), gbc);
        gbc.gridx = 1;
        String[] fromOptions = {"現在", "本事件開始", "某月某日"};
        JComboBox<String> fromComboBox = new JComboBox<>(fromOptions);
        panel.add(fromComboBox, gbc);

        // 從日期選擇器（預設隱藏）
        gbc.gridx = 2;
        JSpinner fromDateSpinner = new JSpinner(new SpinnerDateModel());
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "yyyy/MM/dd"));
        fromDateSpinner.setVisible(false);
        panel.add(fromDateSpinner, gbc);

        // 到
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("到:"), gbc);
        gbc.gridx = 1;
        String[] toOptions = {"永久(3個月)", "本事件結束", "某月某日"};
        JComboBox<String> toComboBox = new JComboBox<>(toOptions);
        panel.add(toComboBox, gbc);

        // 到日期選擇器（預設隱藏）
        gbc.gridx = 2;
        JSpinner toDateSpinner = new JSpinner(new SpinnerDateModel());
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "yyyy/MM/dd"));
        toDateSpinner.setVisible(false);
        panel.add(toDateSpinner, gbc);

        // 最多幾次
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("最多幾次:"), gbc);
        gbc.gridx = 1;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner repeatSpinner = new JSpinner(spinnerModel);
        panel.add(repeatSpinner, gbc);

        // 每次持續多久
        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("每次:"), gbc);
        gbc.gridx = 1;
        String[] durations = {"15分鐘", "30分鐘", "1小時", "3小時"};
        JComboBox<String> durationComboBox = new JComboBox<>(durations);
        panel.add(durationComboBox, gbc);

        // 按鈕
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton arrangeButton = new JButton("安排");
        panel.add(arrangeButton, gbc);

        // 根據選擇顯示日期選擇器
        fromComboBox.addActionListener(e -> {
            String selected = (String) fromComboBox.getSelectedItem();
            fromDateSpinner.setVisible("某月某日".equals(selected));
            pack();
        });

        toComboBox.addActionListener(e -> {
            String selected = (String) toComboBox.getSelectedItem();
            toDateSpinner.setVisible("某月某日".equals(selected));
            pack();
        });

        // 安排事件邏輯
        arrangeButton.addActionListener(e -> {
            String eventName = eventNameField.getText().trim();
            int option = eventComboBox.getSelectedIndex();
            int from = fromComboBox.getSelectedIndex();
            int to = toComboBox.getSelectedIndex();
            int times = (Integer) repeatSpinner.getValue();
            int till = durationComboBox.getSelectedIndex();

            if (from == 1 & to == 1) {
                JOptionPane.showMessageDialog(null, "開始與結束不能都選本事件！");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String fromDateStr = from==2 ? sdf.format((Date) fromDateSpinner.getValue()) : begin[from];
            String toDateStr = to==2 ? sdf.format((Date) toDateSpinner.getValue()) : finish[to];
            String tillStr = duration[till];

            EventArranger eventArranger = null;
            try {
                eventArranger = new EventArranger(option, fromDateStr, toDateStr, String.valueOf(times), tillStr, TargetEvent);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            eventArranger.arrangeEvents();
            JOptionPane.showMessageDialog(null,
                    "事件名稱: " + eventName + "\n" +
                            "選項: " + option + "\n" +
                            "從: " + fromDateStr + "\n" +
                            "到: " + toDateStr + "\n" +
                            "最多次數: " + times + "\n" +
                            "每次: " + tillStr,
                    "安排成功", JOptionPane.INFORMATION_MESSAGE);
        });

        add(panel);
        setVisible(true);
    }
}
