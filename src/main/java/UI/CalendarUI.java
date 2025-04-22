package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel viewPanel;

    public CalendarUI() {
        setTitle("行事曆切換視圖");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 上方按鈕區域
        JPanel buttonPanel = new JPanel();
        JButton weekButton = new JButton("週視圖");
        JButton monthButton = new JButton("月視圖");
        JButton yearButton = new JButton("年視圖");

        buttonPanel.add(weekButton);
        buttonPanel.add(monthButton);
        buttonPanel.add(yearButton);

        // 按鈕事件: 切換不同視圖
        weekButton.addActionListener(e -> cardLayout.show(viewPanel, "Week"));
        monthButton.addActionListener(e -> cardLayout.show(viewPanel, "Month"));
        yearButton.addActionListener(e -> cardLayout.show(viewPanel, "Year"));

        // 中央視圖切換區域
        cardLayout = new CardLayout();
        viewPanel = new JPanel(cardLayout);

        // 各種視圖內容
        JPanel dayView = createViewPanel("今日");
        JPanel weekView = createWeekView(); // 週視圖
        JPanel monthView = createMonthView();//月
        JPanel yearView = createYearView();//月

        //viewPanel.add(dayView, "Day");
        viewPanel.add(weekView, "Week");
        viewPanel.add(monthView, "Month");
        //JPanel monthView = createMonthView();
        viewPanel.add(yearView, "Year");

        // 加入元件到主視窗
        add(buttonPanel, BorderLayout.NORTH);
        add(viewPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // 加入表格視覺化的週視圖
    private JPanel createWeekView() {
        JPanel panel = new JPanel(new BorderLayout());

        // 顯示當前日期
        JLabel label = new JLabel("週視圖 - 今日是：" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), SwingConstants.CENTER);
        label.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        String[] rowNames = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
        String[] columnNames = {" "};
        // 建立行事曆主表格資料
        String[][] tableData = new String[rowNames.length][columnNames.length]; // 預設為空

        // 表格模型
        DefaultTableModel scheduleModel = new DefaultTableModel(tableData, 7);
        JTable scheduleTable = new JTable(scheduleModel);
        scheduleTable.setRowHeight(80);
        scheduleTable.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
        scheduleTable.setCellSelectionEnabled(true);
        scheduleTable.setDefaultRenderer(Object.class, new ScheduleCellRenderer());

        // 建立 row name 表格（單欄只顯示星期幾）
        String[][] rowData = new String[rowNames.length][1];
        for (int i = 0; i < rowNames.length; i++) {
            rowData[i][0] = rowNames[i];
        }
        JTable rowTable = new JTable(rowData, new String[]{""});
        rowTable.setRowHeight(scheduleTable.getRowHeight());
        rowTable.setEnabled(false);
        rowTable.setPreferredScrollableViewportSize(new Dimension(60, 0));
        rowTable.setFont(new Font("微軟正黑體", Font.BOLD, 14));

        // 加入 scrollPane 並放 rowTable 作為左側 row header
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setRowHeaderView(rowTable);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // 加入月視圖
    private JPanel createMonthView() {
        JPanel panel = new JPanel(new BorderLayout());

        // 初始日期
        LocalDate[] currentDate = { LocalDate.now() }; // 用陣列包，方便內部修改

        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        panel.add(label, BorderLayout.NORTH);

        JPanel calendarPanel = new JPanel(new GridLayout(0, 7));
        panel.add(calendarPanel, BorderLayout.CENTER);

        // 上/下月按鈕
        JPanel controlPanel = new JPanel();
        JButton prevButton = new JButton("<< 上個月");
        JButton nextButton = new JButton("下個月 >>");
        controlPanel.add(prevButton);
        controlPanel.add(nextButton);
        panel.add(controlPanel, BorderLayout.SOUTH);

        // 更新日曆內容
        Runnable updateCalendar = () -> {
            calendarPanel.removeAll();

            LocalDate today = currentDate[0];
            int year = today.getYear();
            int month = today.getMonthValue();

            label.setText("現在是 " + year + " 年 " + month + " 月");

            String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
            for (String day : weekDays) {
                JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
                dayLabel.setFont(new Font("微軟正黑體", Font.BOLD, 16));
                dayLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                calendarPanel.add(dayLabel);
            }

            LocalDate firstDay = LocalDate.of(year, month, 1);
            int firstWeekDay = firstDay.getDayOfWeek().getValue();  // 星期一=1, 日=7
            int blankDays = firstWeekDay % 7;

            for (int i = 0; i < blankDays; i++) {
                JLabel emptyLabel = new JLabel("");
                emptyLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                calendarPanel.add(emptyLabel);
            }

            int daysInMonth = firstDay.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
                dayLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
                dayLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                calendarPanel.add(dayLabel);
            }

            calendarPanel.revalidate();
            calendarPanel.repaint();
        };

        // 按鈕功能
        prevButton.addActionListener(e -> {
            currentDate[0] = currentDate[0].minusMonths(1);
            updateCalendar.run();
        });

        nextButton.addActionListener(e -> {
            currentDate[0] = currentDate[0].plusMonths(1);
            updateCalendar.run();
        });

        updateCalendar.run();  // 初始化第一次
        return panel;
    }

    private JPanel createMiniMonth(int year, int month) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel title = new JLabel(month + " 月", SwingConstants.CENTER);
        title.setFont(new Font("微軟正黑體", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);

        JPanel daysGrid = new JPanel(new GridLayout(0, 7)); // 星期日到星期六

        String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
        for (String day : weekDays) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
            daysGrid.add(dayLabel);
        }

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int firstWeekDay = firstDay.getDayOfWeek().getValue(); // 星期一=1，日=7
        int blankDays = firstWeekDay % 7; // 調整讓星期日是0

        for (int i = 0; i < blankDays; i++) {
            daysGrid.add(new JLabel(""));
        }

        int daysInMonth = firstDay.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
            daysGrid.add(dayLabel);
        }

        panel.add(daysGrid, BorderLayout.CENTER);
        return panel;
    }

    private void updateYearPanel(JPanel monthsPanel, int year) {//help repaint the month
        monthsPanel.removeAll();
        for (int month = 1; month <= 12; month++) {
            monthsPanel.add(createMiniMonth(year, month));
        }
    }
    // 加入年視圖
    private JPanel createYearView() {
        JPanel panel = new JPanel(new BorderLayout());

        // 外層用陣列包裝，讓按鈕能修改值
        final int[] currentYear = {LocalDate.now().getYear()};

        // 標題區
        JLabel yearLabel = new JLabel(currentYear[0] + " 年行事曆", SwingConstants.CENTER);
        yearLabel.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        panel.add(yearLabel, BorderLayout.NORTH);

        // 月份區域
        JPanel monthsPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        // 預設先畫今年
        updateYearPanel(monthsPanel, currentYear[0]);

        // 加入月份面板
        panel.add(monthsPanel, BorderLayout.CENTER);

        // 下方控制按鈕
        JPanel buttonPanel = new JPanel();
        JButton prevButton = new JButton("<< 上一年");
        JButton nextButton = new JButton("下一年 >>");

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 按鈕事件：換年份
        prevButton.addActionListener(e -> {
            currentYear[0]--;
            yearLabel.setText(currentYear[0] + " 年行事曆");
            updateYearPanel(monthsPanel, currentYear[0]);
            panel.revalidate();
            panel.repaint();
        });

        nextButton.addActionListener(e -> {
            currentYear[0]++;
            yearLabel.setText(currentYear[0] + " 年行事曆");
            updateYearPanel(monthsPanel, currentYear[0]);
            panel.revalidate();
            panel.repaint();
        });

        return panel;
    }
    //TODO: 加入行程的編輯功能
    //TODO: 加入重大事件，年曆只顯示每月份重大事件

    // 自定義渲染器，支持多行顯示
    private static class ScheduleCellRenderer extends JTextArea implements TableCellRenderer {
        public ScheduleCellRenderer() {
            setWrapStyleWord(true);
            setLineWrap(true);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }
    //TODO: 月、年圖

    // 其他簡單視圖（暫不加事件功能）
    private JPanel createViewPanel(String text) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(text);
        label.setFont(new Font("微軟正黑體", Font.BOLD, 24));
        panel.add(label);
        return panel;
    }
}
