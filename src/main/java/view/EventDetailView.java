package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import data_transfer_object.Event;

public class EventDetailView {
    private JFrame frame;

    public EventDetailView(Event event) {
        frame = new JFrame(event.getName() + " — My Music List");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(520, 440);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel("Event details");
        frame.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(Theme.BACKGROUND);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.add(createRow("Name", event.getName()));
        card.add(createRow("Artist", event.getArtistName()));
        card.add(createRow("Type", event.getType()));
        card.add(createRow("Begin date", event.getBeginDate()));
        card.add(createRow("End date", event.getEndDate()));
        card.add(createRow("Time", event.getTime()));
        card.add(createRow("Place", event.getPlaceName()));
        card.add(createRow("Score", String.valueOf(event.getScore())));

        center.add(card);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setBackground(Theme.BACKGROUND);
        JButton backButton = Theme.secondaryButton("Back");
        backButton.addActionListener(e -> frame.dispose());
        south.add(backButton);
        center.add(Box.createVerticalStrut(Theme.PAD));
        center.add(south);

        frame.add(center, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JLabel createRow(String label, String value) {
        JLabel l = new JLabel("<html><b>" + label + "</b> " + value + "</html>");
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(6, 0, 6, 0));
        return l;
    }
}
