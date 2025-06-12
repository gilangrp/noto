package com.noto.auth;
import javax.swing.*;
import javax.swing.border.*;
import com.noto.database.DatabaseManager;
import com.noto.home.HomeFrame;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends AbstractAuthFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLink;

    public LoginFrame() {
        // Apply global styles
        applyGlobalStyles();

        // Set up the frame
        setTitle("Login - Noto App");
        setSize(400, 350); // Adjusted size for simpler layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        // setUndecorated(true); // Use standard decorations now
        // setBackground(new Color(0, 0, 0, 0)); // No longer needed

        // Create main panel with simple background
        JPanel mainPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for centering
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40)); // Padding
        setContentPane(mainPanel);

        // Create content panel holding the components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false); // Inherit background

        // Add components to the content panel
        setupComponents(contentPanel);

        // Add content panel to main panel (centered)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(contentPanel, gbc);

        // Removed fade-in animation
        /*
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                AnimationUtils.fadeIn(LoginFrame.this, 500);
            }
        });
        */
    }

    private void setupComponents(JPanel panel) {
        // Title label
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(TEXT_COLOR);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Username field 
        usernameField = new JTextField(20); // Increased width
        // Add placeholder text using a utility or a focus listener
        addPlaceholder(usernameField, "Email or Username");
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password field
        passwordField = new JPasswordField(20);
        addPlaceholder(passwordField, "Password");
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Login button
        loginButton = createStyledButton("Login", BUTTON_COLOR);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Set fixed size for the button
        Dimension buttonSize = new Dimension(250, 40); // Wider button
        loginButton.setPreferredSize(buttonSize);
        loginButton.setMaximumSize(buttonSize);
        loginButton.setMinimumSize(buttonSize);

        // Add action listener
        loginButton.addActionListener(e -> handleLogin());

        panel.add(loginButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Register link
        registerLink = new JLabel("Don\'t have an account? Register here");
        registerLink.setFont(UI_FONT); // Use standard UI font
        registerLink.setForeground(LINK_COLOR);
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add underline on hover and click action
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setText("<html><u>Don\'t have an account? Register here</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setText("Don\'t have an account? Register here");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Hide login frame and show register frame (no animation)
                setVisible(false);
                dispose(); // Dispose current frame
                RegisterFrame registerFrame = new RegisterFrame();
                registerFrame.setVisible(true);
            }
        });

        panel.add(registerLink);
    }

    // Helper method to handle login logic
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Basic validation
        if (username.isEmpty() || username.equals("Email or Username") || password.isEmpty() || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Username and password are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Authenticate user with database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        int userId = dbManager.authenticateUser(username, password);

        if (userId > 0) {
            // Login successful
            String fullName = dbManager.getUserFullName(userId);
            // JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + fullName, "Success", JOptionPane.INFORMATION_MESSAGE);

            // Hide login frame and show home frame (no animation)
            setVisible(false);
            dispose();
            HomeFrame homeFrame = new HomeFrame(userId, fullName);
            homeFrame.setVisible(true);
        } else {
            // Login failed
            JOptionPane.showMessageDialog(this, "Invalid username/email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
