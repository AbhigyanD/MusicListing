package view;

import app.AppCoordinator;
import interface_adapter.login.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginView {
    private LoginController loginController;
    private JFrame frame;

    public LoginView() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Sign in — My Music List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 380);
        Theme.styleFrame(frame);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel header = Theme.createHeaderPanel("Sign in");
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
        card.add(Box.createVerticalStrut(Theme.PAD));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, Theme.GAP, 0));
        buttons.setBackground(Theme.CARD_BG);
        JButton loginButton = Theme.primaryButton("Sign in");
        JButton signupButton = Theme.secondaryButton("Create account");
        buttons.add(loginButton);
        buttons.add(signupButton);
        card.add(buttons);

        center.add(card);
        frame.add(center, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            loginController.execute(username, password);
        });

        signupButton.addActionListener(e -> {
            loginController.goSignupView();
            frame.dispose();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void toSignup() {
        AppCoordinator coordinator = AppCoordinator.getInstance();
        coordinator.createSignUpView();
    }

    public void loginFailureView() {
        JOptionPane.showMessageDialog(frame, "Invalid username or password.",
                "Sign in failed", JOptionPane.ERROR_MESSAGE);
    }

    public void toMainMenuView() {
        JOptionPane.showMessageDialog(frame, "Welcome back!");
        AppCoordinator coordinator = AppCoordinator.getInstance();
        coordinator.createMainMenuView();
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
