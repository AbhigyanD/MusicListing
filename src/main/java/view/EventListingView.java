package view;

import data_transfer_object.Event;
import interface_adapter.event_search.EventSearchController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class EventListingView {
    private final JFrame frame;
    private final JPanel listingPanel;
    private final JButton loadMoreButton;
    private final JTextField searchField;
    private final JTextField locationField;
    private int offset = 0;
    private final int LIMIT = 10;
    private boolean hasMore = true;
    private String searchEvent = "";
    private String searchLocation = "";
    private EventSearchController eventSearchController;

    public EventListingView() {
        frame = new JFrame("Event search — My Music List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 920);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel("Event search");
        frame.add(header, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.GAP, Theme.PAD_SMALL));
        searchPanel.setBackground(Theme.BACKGROUND);
        searchPanel.setBorder(new EmptyBorder(Theme.PAD_SMALL, Theme.PAD, Theme.PAD_SMALL, Theme.PAD));

        searchField = Theme.textField(14);
        locationField = Theme.textField(10);
        JButton searchButton = Theme.primaryButton("Search");
        searchButton.addActionListener(new SearchListener());

        searchPanel.add(Theme.label("Event", Theme.FONT_BODY));
        searchPanel.add(searchField);
        searchPanel.add(Theme.label("Location", Theme.FONT_BODY));
        searchPanel.add(locationField);
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

    public void setEventSearchController(EventSearchController eventSearchController) {
        this.eventSearchController = eventSearchController;
    }

    public void presentResults(Event[] events) {
        if (events.length == 0 && offset == 0) {
            JLabel noDataLabel = Theme.label("No events found. Try different search terms.", Theme.FONT_BODY);
            noDataLabel.setBorder(new EmptyBorder(Theme.PAD, 0, Theme.PAD, 0));
            listingPanel.add(noDataLabel);
            hasMore = false;
            loadMoreButton.setEnabled(false);
        } else {
            Arrays.stream(events).forEach(event -> {
                listingPanel.add(createEventPanel(event));
                listingPanel.add(Box.createVerticalStrut(8));
            });
            offset += LIMIT;
            listingPanel.revalidate();
            listingPanel.repaint();
            if (events.length < LIMIT) {
                hasMore = false;
                loadMoreButton.setText("No more results");
                loadMoreButton.setEnabled(false);
            }
        }
    }

    private JPanel createEventPanel(Event event) {
        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
        eventPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1),
                new EmptyBorder(Theme.PAD_SMALL, Theme.PAD, Theme.PAD_SMALL, Theme.PAD)
        ));
        eventPanel.setMaximumSize(new Dimension(780, 130));
        eventPanel.setBackground(Theme.LIST_ITEM_BG);

        JLabel nameLabel = new JLabel("<html><b>" + event.getName() + "</b></html>");
        nameLabel.setFont(Theme.FONT_SUBTITLE);
        nameLabel.setForeground(Theme.TEXT_PRIMARY);
        nameLabel.setBorder(new EmptyBorder(0, 0, 4, 0));
        eventPanel.add(nameLabel);

        JLabel info1 = new JLabel("<html>Type: " + event.getType() + " · Artist: " + event.getArtistName() + " · " + event.getPlaceName() + "</html>");
        info1.setFont(Theme.FONT_SMALL);
        info1.setForeground(Theme.TEXT_SECONDARY);
        info1.setBorder(new EmptyBorder(0, 0, 2, 0));
        eventPanel.add(info1);

        JLabel info2 = new JLabel("<html>Begin: " + event.getBeginDate() + " · End: " + event.getEndDate() + " · Score: " + event.getScore() + "</html>");
        info2.setFont(Theme.FONT_SMALL);
        info2.setForeground(Theme.TEXT_SECONDARY);
        eventPanel.add(info2);

        eventPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                eventPanel.setBackground(Theme.LIST_ITEM_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                eventPanel.setBackground(Theme.LIST_ITEM_BG);
            }
        });

        return eventPanel;
    }

    private class LoadMoreListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (hasMore) fetchAndDisplayListings();
        }
    }

    private class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            searchEvent = searchField.getText().trim();
            searchLocation = locationField.getText().trim();
            offset = 0;
            hasMore = true;
            listingPanel.removeAll();
            loadMoreButton.setText("Load more");
            loadMoreButton.setEnabled(true);
            fetchAndDisplayListings();
        }
    }

    private void fetchAndDisplayListings() {
        if (eventSearchController == null) {
            JOptionPane.showMessageDialog(frame, "Search controller not set.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        eventSearchController.searchEvents(searchEvent, searchLocation, LIMIT, offset);
    }
}
