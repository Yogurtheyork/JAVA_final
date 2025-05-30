package UI.CalendarUI.view.Switcher;

import javax.swing.*;
import java.awt.*;

public class ViewSwitcher {
    private final JPanel container;
    private final CardLayout cardLayout;

    public ViewSwitcher(JPanel container) {
        this.container = container;
        this.cardLayout = new CardLayout();
        container.setLayout(cardLayout);
    }

    public void addView(String name, Component view) {
        container.add(view, name);
    }

    public void show(String name) {
        cardLayout.show(container, name);
    }
}
