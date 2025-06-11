package com.noto.auth;
import javax.swing.*;
import javax.swing.border.*;

import com.noto.database.DatabaseManager;
import com.noto.home.HomeFrame;

import java.awt.*;
import java.awt.event.*;
// Removed unused imports: java.awt.geom.RoundRectangle2D

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLink;
    
    // Style constants from NotesToDo/HomeFrame
    private static final Font UI_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 14); // Slightly smaller bold for buttons
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(50, 50, 80);
    private static final Color BUTTON_COLOR = new Color(186, 225, 255); // Blue soft from NotesToDo save
    private static final Color LINK_COLOR = new Color(65, 105, 225);
    private static final Color BORDER_COLOR = new Color(200, 200, 225);

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
    
    private void applyGlobalStyles() {
        UIManager.put("Label.font", UI_FONT);
        UIManager.put("Button.font", BOLD_FONT);
        UIManager.put("TextField.font", UI_FONT);
        UIManager.put("PasswordField.font", UI_FONT);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", Color.BLACK);
        // Set consistent border for text fields
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8) // Padding inside border
        ));
        UIManager.put("PasswordField.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
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

    // Helper to create styled buttons (consistent with NotesToDo/HomeFrame)
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(BOLD_FONT); 
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15) 
        ));
        // Add hover effect (optional, simple version)
        button.addMouseListener(new MouseAdapter() {
             Color originalColor = button.getBackground();
             @Override
             public void mouseEntered(MouseEvent e) {
                 button.setBackground(originalColor.brighter());
                 button.setCursor(new Cursor(Cursor.HAND_CURSOR));
             }
             @Override
             public void mouseExited(MouseEvent e) {
                 button.setBackground(originalColor);
             }
        });
        return button;
    }
    
    // Helper method for placeholder text
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR); // Use standard text color
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('*'); // Set echo char for password
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0); // Clear echo char for placeholder
                    }
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
        
        // Special handling for password field initial state
        if (field instanceof JPasswordField) {
             ((JPasswordField) field).setEchoChar((char) 0);
        }
    }
    
    // Removed GradientPanel, TransparentPanel, RoundedBorder classes
    // Removed FontAwesomeUtils and AnimationUtils dependencies
}
