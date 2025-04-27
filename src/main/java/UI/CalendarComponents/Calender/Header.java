package UI.CalendarComponents.Calender;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Header extends JPanel {
    private JLabel timeLabel;
    private Timer timer;

    public Header() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        // Initialize time label
        timeLabel = new JLabel("", JLabel.CENTER);
        timeLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 20));
        add(timeLabel, BorderLayout.CENTER);
        
        // Start the clock
        startClock();
    }

    private void startClock() {
        // Update time immediately
        updateTime();
        
        // Create timer to update time every second
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        }, 0, 1000);
    }

    private void updateTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        timeLabel.setText(df.format(new Date()));
    }
}
