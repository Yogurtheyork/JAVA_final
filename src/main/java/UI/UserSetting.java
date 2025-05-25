package UI;

import DataStructures.Setting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class UserSetting extends JFrame {
    private JComboBox<String> languageComboBox;
    private JComboBox<String> sizeComboBox;
    private JCheckBox autoStartCheckBox;
    private static final String FILE_PATH = "src/main/resources/userSetting.json";
    private static final Setting DEFAULT_SETTING = new Setting("繁體中文", "Normal", true);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public UserSetting() {
        setTitle("使用者設定");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1));

        // 語言選單
        languageComboBox = new JComboBox<>(new String[]{"English", "繁體中文"});

        add(createPanel("Language:", languageComboBox));

        // 字型大小
        sizeComboBox = new JComboBox<>(new String[]{"Large", "Normal", "Small"});
        add(createPanel("Size:", sizeComboBox));

        // 開機啟動勾選
        autoStartCheckBox = new JCheckBox("開機時啟動");

        JPanel autoPanel = new JPanel();
        autoPanel.add(autoStartCheckBox);
        add(autoPanel);

        // 按鈕區
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("儲存");
        JButton defaultButton = new JButton("返回預設");

        buttonPanel.add(saveButton);
        buttonPanel.add(defaultButton);
        add(buttonPanel);

        // 按鈕動作
        saveButton.addActionListener(e -> saveSetting());
        defaultButton.addActionListener(e -> resetToDefault());

        // 載入現有設定
        loadSetting();
        setVisible(true);
    }

    private JPanel createPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    private void saveSetting() {
        Setting setting = new Setting(
                (String) languageComboBox.getSelectedItem(),
                (String) sizeComboBox.getSelectedItem(),
                autoStartCheckBox.isSelected()
        );
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(setting, writer);
            JOptionPane.showMessageDialog(this, "設定已儲存");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "儲存失敗: " + e.getMessage());
        }
    }

    private void resetToDefault() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(DEFAULT_SETTING, writer);
            applySetting(DEFAULT_SETTING); // 直接套用，不需重新載入
            JOptionPane.showMessageDialog(this, "已恢復預設設定");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "無法恢復預設: " + e.getMessage());
        }
    }


    private void loadSetting() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            applySetting(DEFAULT_SETTING);
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Setting setting = gson.fromJson(reader, Setting.class);
            applySetting(setting);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "載入失敗: " + e.getMessage());
        }
    }

    private void applySetting(Setting setting) {
        languageComboBox.setSelectedItem(setting.language);
        sizeComboBox.setSelectedItem(setting.fontSize);
        autoStartCheckBox.setSelected(setting.autoStart);
    }
}
