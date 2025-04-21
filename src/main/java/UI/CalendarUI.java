package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

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

        // 中央視圖切換區域
        cardLayout = new CardLayout();
        viewPanel = new JPanel(cardLayout);

        // 各種視圖內容
        JPanel dayView = createViewPanel("今日");
        JPanel weekView = createWeekView(); // 週視圖
        JPanel monthView = createViewPanel("這是月視圖");
        JPanel yearView = createViewPanel("這是年視圖");

        //viewPanel.add(dayView, "Day");
        viewPanel.add(weekView, "Week");
        viewPanel.add(monthView, "Month");
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
        String[] columnNames = {"時段一", "時段二", "時段三", "時段四", "時段五"};
        // 建立行事曆主表格資料
        String[][] tableData = new String[rowNames.length][columnNames.length]; // 預設為空

        // 表格模型
        DefaultTableModel scheduleModel = new DefaultTableModel(tableData, 1);
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
