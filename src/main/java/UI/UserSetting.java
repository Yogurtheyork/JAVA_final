package UI;

import DataStructures.Setting;

import Language.LanguageConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    private static final Setting DEFAULT_SETTING = new Setting("en", "Normal");
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private String Title, Language, Size, Large, Normal, Small, Save, Default, Saved, Defaulted, Error;

    public UserSetting() {
        String language = LanguageConfig.loadLanguage();
        String languageFile;
        // 根據語言選擇不同檔案
        if (language.equals("zh")) {
            languageFile = "src/main/resources/language/Chinese/UserSetting.json";
        } else if (language.equals("en")) {
            languageFile = "src/main/resources/language/English/UserSetting.json";
        } else {
            languageFile = "src/main/resources/language/English/UserSetting.json";
        }
        try (FileReader reader = new FileReader(languageFile)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            Title = jsonObject.get("Title").getAsString();
            Language = jsonObject.getAsJsonObject("Labels").get("Language").getAsString();
            Size = jsonObject.getAsJsonObject("Labels").get("Size").getAsString();
            Large = jsonObject.getAsJsonObject("Sizes").get("Large").getAsString();
            Normal = jsonObject.getAsJsonObject("Sizes").get("Normal").getAsString();
            Small = jsonObject.getAsJsonObject("Sizes").get("Small").getAsString();
            Save = jsonObject.getAsJsonObject("Buttons").get("Save").getAsString();
            Default = jsonObject.getAsJsonObject("Buttons").get("Default").getAsString();
            Saved = jsonObject.getAsJsonObject("Messages").get("Saved").getAsString();
            Defaulted = jsonObject.getAsJsonObject("Messages").get("Defaulted").getAsString();
            Error = jsonObject.getAsJsonObject("Messages").get("Error").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle(Title);
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1));

        // 語言選單
        languageComboBox = new JComboBox<>(new String[]{"en", "zh"});

        add(createPanel(Language, languageComboBox));

        // 字型大小
        sizeComboBox = new JComboBox<>(new String[]{Large, Normal, Small});
        add(createPanel(Size, sizeComboBox));

        // 按鈕區
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton(Save);
        JButton defaultButton = new JButton(Default);

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
                (String) sizeComboBox.getSelectedItem()
        );
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(setting, writer);
            JOptionPane.showMessageDialog(this, Saved);
            this.dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, Error + e.getMessage());
        }
    }

    private void resetToDefault() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(DEFAULT_SETTING, writer);
            applySetting(DEFAULT_SETTING); // 直接套用，不需重新載入
            JOptionPane.showMessageDialog(this, Defaulted);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, Error + e.getMessage());
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
            JOptionPane.showMessageDialog(this, "Failed: " + e.getMessage());
        }
    }

    private void applySetting(Setting setting) {
        languageComboBox.setSelectedItem(setting.language);
        sizeComboBox.setSelectedItem(setting.fontSize);
    }
}
