package view;

import app.AppCoordinator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainMenuView {

    public MainMenuView() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("My Music List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(440, 380);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel("Main menu");
        frame.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(Theme.BACKGROUND);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JButton musicListingsButton = Theme.primaryButton("Music Listings");
        musicListingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton accountButton = Theme.secondaryButton("My Account");
        accountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton logoutButton = Theme.secondaryButton("Log out");

        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(musicListingsButton);
        card.add(Box.createVerticalStrut(Theme.GAP));
        card.add(accountButton);
        card.add(Box.createVerticalStrut(Theme.GAP));
        card.add(logoutButton);

        center.add(card);
        frame.add(center, BorderLayout.CENTER);

        AppCoordinator appCoordinator = AppCoordinator.getInstance();
        musicListingsButton.addActionListener(e -> {
            frame.dispose();
            appCoordinator.createSearchSelectionView();
        });
        logoutButton.addActionListener(e -> {
            frame.dispose();
            appCoordinator.createLoginView();
        });
        accountButton.addActionListener(e -> appCoordinator.createUserAccountView());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainMenuView();
    }
}
