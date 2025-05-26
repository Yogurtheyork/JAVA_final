package UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Curriculum extends JFrame {
    private static final String[] DAYS = {"週一", "週二", "週三", "週四", "週五", "週六", "週日"};

    private static final int MAX_PERIODS = 14; // 第0節到第13節

    private JPanel timePanel;
    private JPanel schedulePanel;
    private JTextField[][] courseFields;
    private JTextField[] timeFields;
    private JTextField planNameField;

    public Curriculum() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("課程表排程系統");
        setLayout(new BorderLayout(10, 10));

        // 初始化組件
        initComponents();

        // 設置窗口大小和位置
        pack();
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        // 北區 - 標題和方案名稱
        JPanel northPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("課程表排程系統", JLabel.CENTER);
        titleLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        northPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel planPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        planPanel.add(new JLabel("方案名稱:"));
        planNameField = new JTextField(20);
        planPanel.add(planNameField);
        northPanel.add(planPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        // 西區 - 時間設置
        timePanel = new JPanel(new GridLayout(MAX_PERIODS + 1, 1));
        timePanel.setBorder(new TitledBorder("課程時間"));

        timePanel.add(new JLabel("時段", JLabel.CENTER));
        timeFields = new JTextField[MAX_PERIODS];

        // 預設的時間設置
        String[] defaultTimes = {
                "07:10-08:00", "08:10-09:00", "09:10-10:00", "10:10-11:00",
                "11:10-12:00", "12:10-13:00", "13:10-14:00", "14:10-15:00",
                "15:10-16:00", "16:10-17:00", "17:10-18:00", "18:10-19:00",
                "19:10-20:00", "20:10-21:00"
        };

        for (int i = 0; i < MAX_PERIODS; i++) {
            JPanel periodPanel = new JPanel(new BorderLayout());
            periodPanel.add(new JLabel("第" + i + "節:", JLabel.CENTER), BorderLayout.WEST);
            timeFields[i] = new JTextField(defaultTimes[i]);
            periodPanel.add(timeFields[i], BorderLayout.CENTER);
            timePanel.add(periodPanel);
        }

        add(timePanel, BorderLayout.WEST);

        // 中央 - 課程表
        schedulePanel = new JPanel(new GridLayout(MAX_PERIODS + 1, DAYS.length + 1));
        schedulePanel.setBorder(new TitledBorder("課程安排"));

        // 添加表頭
        schedulePanel.add(new JLabel(""));
        for (String day : DAYS) {
            schedulePanel.add(new JLabel(day, JLabel.CENTER));
        }

        // 創建課程輸入框
        courseFields = new JTextField[MAX_PERIODS][DAYS.length];
        for (int i = 0; i < MAX_PERIODS; i++) {
            schedulePanel.add(new JLabel("第" + i + "節", JLabel.CENTER));
            for (int j = 0; j < DAYS.length; j++) {
                courseFields[i][j] = new JTextField();
                courseFields[i][j].setHorizontalAlignment(JTextField.CENTER);
                schedulePanel.add(courseFields[i][j]);
            }
        }

        add(new JScrollPane(schedulePanel), BorderLayout.CENTER);

        // 南區 - 按鈕
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton saveButton = new JButton("保存方案");
        saveButton.addActionListener(e -> savePlan());

        JButton loadButton = new JButton("載入方案");
        loadButton.addActionListener(e -> loadPlan());

        JButton clearButton = new JButton("清空課表");
        clearButton.addActionListener(e -> clearSchedule());

        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void savePlan() {

    }

    private void loadPlan() {

    }

    private void clearSchedule() {
        // 清空方案名稱
        planNameField.setText("");

        // 清空課程
        for (int i = 0; i < MAX_PERIODS; i++) {
            for (int j = 0; j < DAYS.length; j++) {
                courseFields[i][j].setText("");
            }
        }
    }
}