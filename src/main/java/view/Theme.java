package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Shared visual theme for the app: colors, fonts, spacing, and styled components.
 * Use these constants and helpers so all views look consistent and modern.
 */
public final class Theme {

    private Theme() {}

    // ——— Colors ———
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color ACCENT = new Color(59, 130, 246);      // blue
    public static final Color ACCENT_HOVER = new Color(37, 99, 235);
    public static final Color ACCENT_SECONDARY = new Color(100, 116, 139);  // slate
    public static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color LIST_ITEM_BG = new Color(248, 250, 252);
    public static final Color LIST_ITEM_HOVER = new Color(224, 242, 254);
    public static final Color HEADER_BG = new Color(30, 41, 59);
    public static final Color HEADER_TEXT = Color.WHITE;
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color ERROR = new Color(239, 68, 68);

    // ——— Fonts (Java substitutes if Segoe UI unavailable) ———
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_INPUT = new Font(FONT_FAMILY, Font.PLAIN, 14);

    // ——— Spacing ———
    public static final int PAD = 16;
    public static final int PAD_SMALL = 10;
    public static final int PAD_LARGE = 24;
    public static final int GAP = 12;
    public static final int RADIUS = 8;

    /** Apply theme to a JFrame: background and optional title bar style. */
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND);
        try {
            frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        } catch (Exception ignored) {}
    }

    /** A panel that looks like a card: white, rounded border, padding. */
    public static JPanel createCardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(PAD_LARGE, PAD_LARGE, PAD_LARGE, PAD_LARGE)
        ));
        return p;
    }

    /** Header panel with dark background for title. */
    public static JPanel createHeaderPanel(String title) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(HEADER_BG);
        p.setBorder(new EmptyBorder(PAD, PAD, PAD, PAD));
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(HEADER_TEXT);
        p.add(lbl);
        return p;
    }

    /** Primary action button (accent color). */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(ACCENT_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(ACCENT);
            }
        });
        return b;
    }

    /** Secondary/outline-style button. */
    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setBackground(CARD_BG);
        b.setForeground(TEXT_PRIMARY);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 20, 10, 20)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(LIST_ITEM_BG);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(CARD_BG);
            }
        });
        return b;
    }

    /** Label with primary text style. */
    public static JLabel label(String text, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font != null ? font : FONT_BODY);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    /** Themed text field. */
    public static JTextField textField(int columns) {
        JTextField t = new JTextField(columns);
        t.setFont(FONT_INPUT);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return t;
    }

    /** Themed password field. */
    public static JPasswordField passwordField(int columns) {
        JPasswordField p = new JPasswordField(columns);
        p.setFont(FONT_INPUT);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        return p;
    }
}
