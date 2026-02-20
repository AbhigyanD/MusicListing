package view;

import data_transfer_object.Artist;
import data_transfer_object.Recording;
import global_storage.CurrentUser;
import interface_adapter.writer.WriterController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

public class ArtistDetailView {
    final JFrame frame;
    private JPanel commentsPanel;
    private JScrollPane commentsScrollPane;
    private WriterController writeController;

    public ArtistDetailView(Recording[] topSongs, Map<String, String> comments,
                           Artist artist, Double averageRating) {
        frame = new JFrame(artist.getArtistName() + " — My Music List");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 820);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel(artist.getArtistName());
        frame.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(Theme.GAP, Theme.GAP));
        center.setBackground(Theme.BACKGROUND);
        center.setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Theme.CARD_BG);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE)
        ));
        updateDetailsPanel(detailsPanel, artist, averageRating);
        center.add(detailsPanel, BorderLayout.CENTER);

        JPanel userInputPanel = createUserInputPanel(artist);
        center.add(userInputPanel, BorderLayout.SOUTH);
        frame.add(center, BorderLayout.CENTER);

        loadComments(comments);
        loadSongs(topSongs);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateDetailsPanel(JPanel detailsPanel, Artist artist, Double averageRating) {
        detailsPanel.removeAll();
        detailsPanel.add(createDetailRow("Artist", artist.getArtistName()));
        detailsPanel.add(createDetailRow("Country", artist.getCountry()));
        detailsPanel.add(createDetailRow("Type", artist.getType()));
        detailsPanel.add(createDetailRow("Average rating", String.valueOf(averageRating)));
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private JPanel createUserInputPanel(Artist artist) {
        JPanel userInputPanel = new JPanel();
        userInputPanel.setLayout(new BoxLayout(userInputPanel, BoxLayout.Y_AXIS));
        userInputPanel.setBackground(Theme.CARD_BG);
        userInputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(Theme.PAD, Theme.PAD_LARGE, Theme.PAD, Theme.PAD_LARGE)
        ));

        JPanel ratingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.GAP, 0));
        ratingRow.setBackground(Theme.CARD_BG);
        ratingRow.add(Theme.label("Rating", Theme.FONT_BODY));
        JComboBox<Integer> ratingDropdown = new JComboBox<>();
        for (int i = 0; i <= 10; i++) ratingDropdown.addItem(i);
        ratingDropdown.setFont(Theme.FONT_BODY);
        ratingRow.add(ratingDropdown);
        userInputPanel.add(ratingRow);

        userInputPanel.add(Box.createVerticalStrut(Theme.PAD_SMALL));
        userInputPanel.add(Theme.label("Comment", Theme.FONT_BODY));
        JTextArea commentBox = new JTextArea(3, 24);
        commentBox.setFont(Theme.FONT_INPUT);
        commentBox.setLineWrap(true);
        commentBox.setWrapStyleWord(true);
        commentBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane commentScroll = new JScrollPane(commentBox);
        userInputPanel.add(commentScroll);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.GAP, Theme.PAD_SMALL));
        buttonRow.setBackground(Theme.CARD_BG);
        JButton emojiButton = Theme.secondaryButton("Insert emoji");
        JButton addButton = Theme.primaryButton("Add comment");
        buttonRow.add(emojiButton);
        buttonRow.add(addButton);
        userInputPanel.add(buttonRow);

        emojiButton.addActionListener(e -> {
            String[] emojis = {"😀", "😂", "😍", "😢", "👍", "🔥", "💯", "🎶", "😎", "🤘", "❤️", "💔", "👏", "🙏", "🥳", "🤩", "😡", "🎉", "👀", "💥"};
            String selected = (String) JOptionPane.showInputDialog(frame, "Pick an emoji", "Emoji", JOptionPane.PLAIN_MESSAGE, null, emojis, emojis[0]);
            if (selected != null) commentBox.append(selected);
        });

        addButton.addActionListener(e -> {
            int rating = (Integer) ratingDropdown.getSelectedItem();
            String comment = commentBox.getText().trim();
            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a comment.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            writeController.execute(artist.getId(), CurrentUser.username, comment, rating);
            JLabel newCommentLabel = new JLabel("<html><b>" + CurrentUser.username + ":</b> " + comment + "</html>");
            newCommentLabel.setFont(Theme.FONT_BODY);
            newCommentLabel.setBorder(new EmptyBorder(4, 0, 4, 0));
            commentsPanel.add(newCommentLabel);
            commentsPanel.revalidate();
            commentsPanel.repaint();
            ratingDropdown.setSelectedIndex(0);
            commentBox.setText("");
        });

        return userInputPanel;
    }

    private void loadComments(Map<String, String> comments) {
        if (commentsPanel != null) frame.remove(commentsScrollPane);

        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        commentsPanel.setBackground(Theme.CARD_BG);
        TitledBorder commentsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER), "Comments", 0, 0, Theme.FONT_SUBTITLE, Theme.TEXT_PRIMARY);
        commentsPanel.setBorder(BorderFactory.createCompoundBorder(commentsBorder, new EmptyBorder(Theme.PAD_SMALL, Theme.PAD, Theme.PAD, Theme.PAD)));

        if (!comments.isEmpty()) {
            for (String key : comments.keySet()) {
                JLabel item = new JLabel("<html><b>" + key + ":</b> " + comments.get(key) + "</html>");
                item.setFont(Theme.FONT_BODY);
                item.setBorder(new EmptyBorder(4, 0, 4, 0));
                commentsPanel.add(item);
            }
        }
        commentsScrollPane = new JScrollPane(commentsPanel);
        commentsScrollPane.setPreferredSize(new Dimension(320, 260));
        commentsScrollPane.getViewport().setBackground(Theme.CARD_BG);
        frame.add(commentsScrollPane, BorderLayout.EAST);
        frame.revalidate();
        frame.repaint();
    }

    private void loadSongs(Recording[] topSongs) {
        JPanel songsPanel = new JPanel();
        songsPanel.setLayout(new BoxLayout(songsPanel, BoxLayout.Y_AXIS));
        songsPanel.setBackground(Theme.CARD_BG);
        TitledBorder songsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER), "Top songs", 0, 0, Theme.FONT_SUBTITLE, Theme.TEXT_PRIMARY);
        songsPanel.setBorder(BorderFactory.createCompoundBorder(songsBorder, new EmptyBorder(Theme.PAD_SMALL, Theme.PAD, Theme.PAD, Theme.PAD)));

        if (topSongs.length == 0) {
            songsPanel.add(Theme.label("No songs found.", Theme.FONT_BODY));
        } else {
            for (Recording song : topSongs) {
                JLabel songLabel = Theme.label(song.getTitle() + " · " + song.getFormattedLength(), Theme.FONT_BODY);
                songLabel.setBorder(new EmptyBorder(4, 0, 4, 0));
                songsPanel.add(songLabel);
            }
        }
        JScrollPane songsScrollPane = new JScrollPane(songsPanel);
        songsScrollPane.setPreferredSize(new Dimension(320, 260));
        songsScrollPane.getViewport().setBackground(Theme.CARD_BG);
        frame.add(songsScrollPane, BorderLayout.WEST);
        frame.revalidate();
        frame.repaint();
    }

    private JLabel createDetailRow(String field, String value) {
        JLabel l = new JLabel("<html><b>" + field + "</b> " + value + "</html>");
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(4, 0, 4, 0));
        return l;
    }

    public void commentSuccess(String message) {
        JOptionPane.showMessageDialog(frame, message, "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    public void commentFailure(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setWriterController(WriterController writerController) {
        this.writeController = writerController;
    }
}
