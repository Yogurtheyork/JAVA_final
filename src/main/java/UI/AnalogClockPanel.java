package UI;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

public class AnalogClockPanel extends JPanel {

    public AnalogClockPanel() {
        // 每秒刷新畫面
        Timer timer = new Timer(1000, e -> repaint());
        timer.start();
        setPreferredSize(new Dimension(300, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();
        int radius = Math.min(w, h) / 2 - 10;
        int centerX = w / 2;
        int centerY = h / 2;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 畫錶盤
        g2.setColor(Color.WHITE);
        g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        g2.setColor(Color.BLACK);
        g2.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // 畫刻度
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30);
            int x1 = (int) (centerX + Math.sin(angle) * (radius - 10));
            int y1 = (int) (centerY - Math.cos(angle) * (radius - 10));
            int x2 = (int) (centerX + Math.sin(angle) * (radius - 2));
            int y2 = (int) (centerY - Math.cos(angle) * (radius - 2));
            g2.drawLine(x1, y1, x2, y2);
        }

        // 取得現在時間
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        // 畫時針、分針、秒針
        drawHand(g2, centerX, centerY, radius * 0.5, hour * 30 + minute * 0.5, 6, Color.BLACK);
        drawHand(g2, centerX, centerY, radius * 0.7, minute * 6, 4, Color.BLUE);
        drawHand(g2, centerX, centerY, radius * 0.9, second * 6, 2, Color.RED);
    }

    private void drawHand(Graphics2D g2, int x, int y, double length, double angleDegrees, int thickness, Color color) {
        double angle = Math.toRadians(angleDegrees);
        int xEnd = (int) (x + length * Math.sin(angle));
        int yEnd = (int) (y - length * Math.cos(angle));
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawLine(x, y, xEnd, yEnd);
    }
}