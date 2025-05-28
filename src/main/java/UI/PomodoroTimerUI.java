package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PomodoroTimerUI extends JFrame {

    private int workTime = 25 * 60;       // 預設 25 分鐘工作
    private int shortBreak = 5 * 60;      // 預設 5 分鐘短休息
    private int longBreak = 15 * 60;      // 預設 15 分鐘長休息
    private static final int POMODOROS_BEFORE_LONG_BREAK = 4;

    private JLabel timerLabel;
    private JLabel statusLabel;
    private JLabel pomodoroCountLabel;
    private JButton startPauseButton;
    private JButton resetButton;

    private JTextField workInput;
    private JTextField shortBreakInput;
    private JTextField longBreakInput;
    private JButton applyTimeButton;

    private JTextArea historyArea;

    private Timer timer;
    private int timeLeft;
    private boolean running = false;
    private boolean isWorkPeriod = true;
    private int pomodoroCount = 0;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public PomodoroTimerUI() {
        setTitle("番茄鐘");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 時間顯示
        timerLabel = new JLabel(formatTime(workTime), SwingConstants.CENTER);
        timerLabel.setFont(new Font("Microsoft JhengHei", Font.BOLD, 60));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 狀態文字，預設尚未開始
        statusLabel = new JLabel("尚未開始", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 24));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        // 完成番茄數
        pomodoroCountLabel = new JLabel("完成番茄數：0", SwingConstants.CENTER);
        pomodoroCountLabel.setFont(new Font("Microsoft JhengHei", Font.PLAIN, 18));
        pomodoroCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 按鈕區
        startPauseButton = new JButton("開始");
        startPauseButton.setPreferredSize(new Dimension(100, 40));
        resetButton = new JButton("重置");
        resetButton.setPreferredSize(new Dimension(100, 40));
        resetButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(startPauseButton);
        buttonPanel.add(resetButton);

        // 輸入區
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("自訂時間（分鐘）"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("工作時間："), gbc);
        gbc.gridx = 1;
        workInput = new JTextField("25", 5);
        inputPanel.add(workInput, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("短休息時間："), gbc);
        gbc.gridx = 1;
        shortBreakInput = new JTextField("5", 5);
        inputPanel.add(shortBreakInput, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("長休息時間："), gbc);
        gbc.gridx = 1;
        longBreakInput = new JTextField("15", 5);
        inputPanel.add(longBreakInput, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        applyTimeButton = new JButton("設定時間");
        inputPanel.add(applyTimeButton, gbc);

        // 歷史紀錄區
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setPreferredSize(new Dimension(220, 0));
        historyScroll.setBorder(BorderFactory.createTitledBorder("歷史紀錄"));

        // 中央時間與狀態面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(timerLabel, BorderLayout.CENTER);
        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(pomodoroCountLabel, BorderLayout.SOUTH);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 整體版面使用 BorderLayout
        setLayout(new BorderLayout(15, 15));
        add(historyScroll, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        // 初始化 timeLeft
        timeLeft = workTime;

        // 按鈕事件
        startPauseButton.addActionListener(e -> {
            if (running) pauseTimer();
            else startTimer();
        });

        resetButton.addActionListener(e -> resetTimer());

        applyTimeButton.addActionListener(e -> {
            try {
                int newWork = Integer.parseInt(workInput.getText());
                int newShort = Integer.parseInt(shortBreakInput.getText());
                int newLong = Integer.parseInt(longBreakInput.getText());

                if (newWork <= 0 || newShort <= 0 || newLong <= 0) {
                    JOptionPane.showMessageDialog(this, "請輸入正整數分鐘數", "錯誤", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                workTime = newWork * 60;
                shortBreak = newShort * 60;
                longBreak = newLong * 60;

                resetTimer();  // 直接重置，讓時間設定立即生效
                JOptionPane.showMessageDialog(this, "時間設定成功！", "設定完成", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "請輸入有效的數字", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    SwingUtilities.invokeLater(() -> tick());
                }
            }, 0, 1000);
        }
        running = true;
        startPauseButton.setText("暫停");
        resetButton.setEnabled(true);

        // 若尚未開始，設定狀態為工作中或休息中
        if (statusLabel.getText().equals("尚未開始")) {
            updateStatusLabel();
        }
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        running = false;
        startPauseButton.setText("開始");
    }

    private void resetTimer() {
        pauseTimer();
        if (isWorkPeriod) {
            timeLeft = workTime;
        } else {
            timeLeft = pomodoroCount % POMODOROS_BEFORE_LONG_BREAK == 0 ? longBreak : shortBreak;
        }
        timerLabel.setText(formatTime(timeLeft));
        resetButton.setEnabled(false);

        // 狀態標籤也更新，開始前顯示尚未開始
        if (!running) {
            statusLabel.setText("尚未開始");
            statusLabel.setForeground(Color.BLACK);
        }
    }

    private void tick() {
        if (timeLeft > 0) {
            timeLeft--;
            timerLabel.setText(formatTime(timeLeft));
        } else {
            pauseTimer();
            notifyPeriodEnd();
            logHistory();
            switchPeriod();
            resetTimer();
            startTimer();
        }
    }

    private void switchPeriod() {
        if (isWorkPeriod) {
            pomodoroCount++;
            pomodoroCountLabel.setText("完成番茄數：" + pomodoroCount);
        }
        isWorkPeriod = !isWorkPeriod;
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        if (isWorkPeriod) {
            statusLabel.setText("工作中");
            statusLabel.setForeground(new Color(200, 0, 0));
            timeLeft = workTime;
        } else {
            if (pomodoroCount % POMODOROS_BEFORE_LONG_BREAK == 0) {
                statusLabel.setText("長休息");
            } else {
                statusLabel.setText("短休息");
            }
            statusLabel.setForeground(new Color(0, 150, 0));
            timeLeft = (pomodoroCount % POMODOROS_BEFORE_LONG_BREAK == 0) ? longBreak : shortBreak;
        }
        timerLabel.setText(formatTime(timeLeft));
    }

    private void notifyPeriodEnd() {
        String message = isWorkPeriod ? "工作時間結束，休息一下！" : "休息時間結束，繼續工作！";
        JOptionPane.showMessageDialog(this, message, "時間到", JOptionPane.INFORMATION_MESSAGE);
        Toolkit.getDefaultToolkit().beep();
    }

    private void logHistory() {
        String now = timeFormat.format(new Date());
        String period = isWorkPeriod ? "工作" : "休息";
        String record = String.format("[%s] 完成 %s階段。完成番茄數：%d\n", now, period, pomodoroCount);
        historyArea.append(record);
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    private String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

}