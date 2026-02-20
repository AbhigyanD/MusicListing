package view;

import app.AppCoordinator;
import global_storage.CurrentUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserAccountView {

    public UserAccountView() {
        show();
    }

    private void show() {
        JFrame frame = new JFrame("My account — My Music List");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(420, 280);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel("My account");
        frame.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(Theme.BACKGROUND);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel userLabel = Theme.label("Signed in as", Theme.FONT_SMALL);
        userLabel.setForeground(Theme.TEXT_SECONDARY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(userLabel);
        card.add(Box.createVerticalStrut(4));
        JLabel nameLabel = Theme.label(CurrentUser.username.isEmpty() ? "—" : CurrentUser.username, Theme.FONT_SUBTITLE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(Theme.PAD));

        JButton backMenu = Theme.secondaryButton("Back to main menu");
        backMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(backMenu);

        center.add(card);
        frame.add(center, BorderLayout.CENTER);

        backMenu.addActionListener(e -> {
            frame.dispose();
            AppCoordinator appCoordinator = AppCoordinator.getInstance();
            appCoordinator.createMainMenuView();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
