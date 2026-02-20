package view;

import app.AppCoordinator;
import data_transfer_object.Artist;
import data_transfer_object.Recording;
import interface_adapter.artist_search.ArtistSearchController;
import interface_adapter.read_from_db.ReadController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ArtistListingView {
    private final JPanel listingPanel;
    private final JButton loadMoreButton;
    private final JTextField searchField;
    private final JTextField countryField;
    private final JComboBox<String> typeDropdown;
    private int offset = 0;
    private final int LIMIT = 10;
    private boolean hasMore = true;
    private String searchArtist = "";
    private String searchCountry = "";
    private String searchType = "";
    private ArtistSearchController artistSearchController;
    private ReadController readController;

    public ArtistListingView() {
        JFrame frame = new JFrame("Artist search — My Music List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 920);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel("Artist search");
        frame.add(header, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.GAP, Theme.PAD_SMALL));
        searchPanel.setBackground(Theme.BACKGROUND);
        searchPanel.setBorder(new EmptyBorder(Theme.PAD_SMALL, Theme.PAD, Theme.PAD_SMALL, Theme.PAD));

        searchField = Theme.textField(14);
        countryField = Theme.textField(8);
        String[] types = {"Any", "Group", "Person", "Other"};
        typeDropdown = new JComboBox<>(types);
        typeDropdown.setFont(Theme.FONT_BODY);
        typeDropdown.setBackground(Theme.CARD_BG);

        JButton searchButton = Theme.primaryButton("Search");
        searchButton.addActionListener(new SearchListener());

        searchPanel.add(Theme.label("Artist", Theme.FONT_BODY));
        searchPanel.add(searchField);
        searchPanel.add(Theme.label("Country", Theme.FONT_BODY));
        searchPanel.add(countryField);
        searchPanel.add(Theme.label("Type", Theme.FONT_BODY));
        searchPanel.add(typeDropdown);
        searchPanel.add(searchButton);
        frame.add(searchPanel, BorderLayout.NORTH);

        listingPanel = new JPanel();
        listingPanel.setLayout(new BoxLayout(listingPanel, BoxLayout.Y_AXIS));
        listingPanel.setBackground(Theme.BACKGROUND);
        listingPanel.setBorder(new EmptyBorder(Theme.PAD_SMALL, Theme.PAD, Theme.PAD_SMALL, Theme.PAD));

        JScrollPane scrollPane = new JScrollPane(listingPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(Theme.BACKGROUND);
        southPanel.setBorder(new EmptyBorder(Theme.PAD_SMALL, 0, Theme.PAD, 0));
        loadMoreButton = Theme.secondaryButton("Load more");
        loadMoreButton.addActionListener(new LoadMoreListener());
        southPanel.add(loadMoreButton);
        frame.add(southPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createArtistPanel(Artist artist) {
        JPanel artistPanel = new JPanel();
        artistPanel.setLayout(new BorderLayout());
        artistPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(Theme.PAD_SMALL, Theme.PAD, Theme.PAD_SMALL, Theme.PAD)
        ));
        artistPanel.setMaximumSize(new Dimension(780, 110));
        artistPanel.setBackground(Theme.LIST_ITEM_BG);

        JLabel nameLabel = new JLabel("<html><b>" + artist.getArtistName() + "</b></html>");
        nameLabel.setFont(Theme.FONT_SUBTITLE);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        nameLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
        artistPanel.add(nameLabel, BorderLayout.NORTH);

        JLabel metaLabel = new JLabel("<html>Type: " + artist.getType() + " · Country: " + artist.getCountry() + " · Score: " + artist.getScore() + "</html>");
        metaLabel.setFont(Theme.FONT_SMALL);
        metaLabel.setForeground(Theme.TEXT_SECONDARY);
        artistPanel.add(metaLabel, BorderLayout.CENTER);

        artistPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                readController.execute(artist.getId(), artist);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                artistPanel.setBackground(Theme.LIST_ITEM_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                artistPanel.setBackground(Theme.LIST_ITEM_BG);
            }
        });

        return artistPanel;
    }

    public void presentResults(Artist[] artists) {
        try {
            if (artists.length == 0 && offset == 0) {
                JLabel noDataLabel = Theme.label("No artists found. Try different search terms.", Theme.FONT_BODY);
                noDataLabel.setBorder(new EmptyBorder(Theme.PAD, 0, Theme.PAD, 0));
                listingPanel.add(noDataLabel);
                hasMore = false;
                loadMoreButton.setEnabled(false);
            } else {
                for (Artist artist : artists) {
                    if (searchType.equals("Any") || artist.getType().equalsIgnoreCase(searchType)) {
                        listingPanel.add(createArtistPanel(artist));
                        listingPanel.add(Box.createVerticalStrut(8));
                    }
                }
                offset += LIMIT;
                listingPanel.revalidate();
                listingPanel.repaint();
                if (artists.length < LIMIT) {
                    hasMore = false;
                    loadMoreButton.setText("No more results");
                    loadMoreButton.setEnabled(false);
                }
            }
        } catch (Exception e) {
            JLabel errorLabel = Theme.label("Error: " + e.getMessage(), Theme.FONT_BODY);
            errorLabel.setForeground(Theme.ERROR);
            listingPanel.add(errorLabel);
            listingPanel.revalidate();
            listingPanel.repaint();
        }
    }

    private class LoadMoreListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (hasMore) {
                artistSearchController.searchArtists(searchArtist, searchCountry, LIMIT, offset);
            }
        }
    }

    private class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            searchArtist = searchField.getText().trim();
            searchCountry = countryField.getText().trim();
            searchType = (String) typeDropdown.getSelectedItem();
            offset = 0;
            hasMore = true;
            listingPanel.removeAll();
            loadMoreButton.setText("Load more");
            loadMoreButton.setEnabled(true);
            artistSearchController.searchArtists(searchArtist, searchCountry, LIMIT, offset);
        }
    }

    public void setArtistSearchController(ArtistSearchController artistSearchController) {
        this.artistSearchController = artistSearchController;
    }

    public void setReadController(ReadController readController) {
        this.readController = readController;
    }

    public void createArtistDetailView(Recording[] topSongs,
                                      Map<String, String> comments, Artist artist,
                                      Double averageRating) {
        AppCoordinator appCoordinator = AppCoordinator.getInstance();
        appCoordinator.createArtistDetailView(topSongs, comments, artist, averageRating);
    }
}
