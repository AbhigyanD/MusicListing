package view;

import app.AppCoordinator;
import interface_adapter.signup.SignupController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SignupView {
    private SignupController signupController;
    private final JFrame frame;

    public SignupView() {
        frame = new JFrame("Create account — My Music List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 460);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel("Create account");
        frame.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(Theme.BACKGROUND);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE, Theme.PAD_LARGE));

        JPanel card = Theme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel userLabel = Theme.label("Username", Theme.FONT_BODY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        JTextField usernameField = Theme.textField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(Theme.GAP));

        JLabel passLabel = Theme.label("Password", Theme.FONT_BODY);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        JPasswordField passwordField = Theme.passwordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(Theme.GAP));

        JLabel confirmLabel = Theme.label("Confirm password", Theme.FONT_BODY);
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(confirmLabel);
        card.add(Box.createVerticalStrut(6));
        JPasswordField confirmPasswordField = Theme.passwordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(confirmPasswordField);
        card.add(Box.createVerticalStrut(Theme.PAD));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.GAP, 0));
        buttons.setBackground(Theme.CARD_BG);
        JButton createAccountButton = Theme.primaryButton("Create account");
        JButton backButton = Theme.secondaryButton("Back to sign in");
        buttons.add(createAccountButton);
        buttons.add(backButton);
        card.add(buttons);

        center.add(card);
        frame.add(center, BorderLayout.CENTER);

        createAccountButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            signupController.execute(username, password, confirmPassword);
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            signupController.switchToLoginView();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void setSignupController(SignupController signupController) {
        this.signupController = signupController;
    }

    public void goLoginView() {
        AppCoordinator appCoordinator = AppCoordinator.getInstance();
        appCoordinator.createLoginView();
    }

    public void signupSuccess() {
        JOptionPane.showMessageDialog(frame, "Account created. You can sign in now.");
        frame.dispose();
        AppCoordinator appCoordinator = AppCoordinator.getInstance();
        appCoordinator.createLoginView();
    }

    public void signupFailure() {
        JOptionPane.showMessageDialog(frame, "That username is already taken.", "Sign up failed", JOptionPane.WARNING_MESSAGE);
    }

    public void passwordUnmatched() {
        JOptionPane.showMessageDialog(frame, "Passwords do not match.", "Try again", JOptionPane.WARNING_MESSAGE);
    }
}
