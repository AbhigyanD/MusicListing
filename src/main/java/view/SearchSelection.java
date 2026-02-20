package view;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import app.AppCoordinator;

public class SearchSelection {

    private final JFrame frame;

    public SearchSelection() {
        frame = new JFrame("Music Listing — My Music List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(440, 380);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel("Select category");
        frame.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(Theme.BACKGROUND);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JButton artistButton = Theme.primaryButton("Artist listings");
        artistButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton eventButton = Theme.secondaryButton("Event listings");
        eventButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton mainMenuButton = Theme.secondaryButton("Main menu");
        mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(artistButton);
        card.add(Box.createVerticalStrut(Theme.GAP));
        card.add(eventButton);
        card.add(Box.createVerticalStrut(Theme.GAP));
        card.add(mainMenuButton);

        center.add(card);
        frame.add(center, BorderLayout.CENTER);

        AppCoordinator appCoordinator = AppCoordinator.getInstance();
        artistButton.addActionListener(e -> {
            frame.dispose();
            appCoordinator.createArtistListingView();
        });
        eventButton.addActionListener(e -> {
            frame.dispose();
            appCoordinator.createEventListingView();
        });
        mainMenuButton.addActionListener(e -> {
            frame.dispose();
            appCoordinator.createMainMenuView();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(SearchSelection::new);
    }
}
