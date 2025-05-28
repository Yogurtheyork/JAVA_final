package UI.utils;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    public static JLabel createCenteredLabel(String text, int fontSize) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        return label;
    }

    public static JPanel wrapWithPadding(Component component, int padding) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        wrapper.add(component);
        return wrapper;
    }
}
