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

    private DefaultTableModel scheduleModel;

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
        JPanel weekView = createWeekView(); // 週視圖
        JPanel monthView = createViewPanel("這是月視圖");
        JPanel yearView = createViewPanel("這是年視圖");

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

    // 加入表格視覺化的週視圖
    private JPanel createWeekView() {
        JPanel panel = new JPanel(new BorderLayout());

        // 顯示當前日期
        JLabel label = new JLabel("週視圖 - 今日是：" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), SwingConstants.CENTER);
        label.setFont(new Font("微軟正黑體", Font.BOLD, 20));
        panel.add(label, BorderLayout.NORTH);

        // 設置表格模型，表格有 7 列（代表 7 天），每一天的行程
        String[] columnNames = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};
        scheduleModel = new DefaultTableModel(columnNames, 1); // 1 行用於顯示這一週

        // 建立 JTable 來顯示週視圖
        JTable scheduleTable = new JTable(scheduleModel);
        scheduleTable.setRowHeight(100); // 每一行的高度
        scheduleTable.setFont(new Font("微軟正黑體", Font.PLAIN, 14));

        // 讓表格支持自動換行
        scheduleTable.setCellSelectionEnabled(true);
        scheduleTable.setDefaultRenderer(Object.class, new ScheduleCellRenderer());

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 新增行程按鈕
        JButton addButton = new JButton("新增事件");
        addButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(CalendarUI.this, "輸入事件內容：");
            if (input != null && !input.trim().isEmpty()) {
                // 將事件添加到選中的單元格中
                int selectedRow = scheduleTable.getSelectedRow();
                int selectedColumn = scheduleTable.getSelectedColumn();
                if (selectedRow != -1 && selectedColumn != -1) {
                    scheduleModel.setValueAt("🗓️ " + input.trim(), selectedRow, selectedColumn);
                }
            }
        });
        panel.add(addButton, BorderLayout.SOUTH);

        return panel;
    }

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
