package com.noto.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.noto.database.DatabaseManager;

public class RegisterFrame extends AbstractAuthFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JCheckBox showPasswordCheckbox;
    private JButton registerButton;
    private JLabel loginLink;
    private JLabel validationLabel; // Keep for simple validation messages
    
    // Email validation pattern
    private static final java.util.regex.Pattern EMAIL_PATTERN = 
        java.util.regex.Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", java.util.regex.Pattern.CASE_INSENSITIVE);
    
    // Register button color (not in AbstractAuthFrame)
    private static final Color REGISTER_BUTTON_COLOR = new Color(144, 238, 144); // Light green for register
    private static final Color ERROR_COLOR = Color.RED;

    public RegisterFrame() {
        // Apply global styles
        applyGlobalStyles();
        
        // Set up the frame
        setTitle("Register - Noto App");
        setSize(450, 550); // Adjusted size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        // Use standard decorations
        
        // Create main panel 
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
    }
    
    private void setupComponents(JPanel panel) {
        // Title label
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(TEXT_COLOR);
        panel.add(titleLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // --- Input Fields ---
        nameField = new JTextField(20);
        addPlaceholder(nameField, "Full Name");
        panel.add(createFieldPanel("Full Name:", nameField));
        
        emailField = new JTextField(20);
        addPlaceholder(emailField, "Email Address");
        panel.add(createFieldPanel("Email:", emailField));
        
        usernameField = new JTextField(20);
        addPlaceholder(usernameField, "Username");
        panel.add(createFieldPanel("Username:", usernameField));
        
        passwordField = new JPasswordField(20);
        addPlaceholder(passwordField, "Password");
        panel.add(createFieldPanel("Password:", passwordField));
        
        confirmPasswordField = new JPasswordField(20);
        addPlaceholder(confirmPasswordField, "Confirm Password");
        panel.add(createFieldPanel("Confirm Password:", confirmPasswordField));
        
        // Show password checkbox
        showPasswordCheckbox = new JCheckBox("Show Password");
        showPasswordCheckbox.setFont(UI_FONT);
        showPasswordCheckbox.setForeground(TEXT_COLOR);
        showPasswordCheckbox.setOpaque(false);
        showPasswordCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT); // Align left
        showPasswordCheckbox.setFocusPainted(false);
        showPasswordCheckbox.setMargin(new Insets(0, 0, 10, 0)); // Add bottom margin
        
        showPasswordCheckbox.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                boolean show = showPasswordCheckbox.isSelected();
                passwordField.setEchoChar(show ? (char) 0 : '\u2022');
                confirmPasswordField.setEchoChar(show ? (char) 0 : '\u2022');
            }
        });
        
        // Add checkbox to a panel to control alignment if needed
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setOpaque(false);
        checkboxPanel.add(showPasswordCheckbox);
        panel.add(checkboxPanel);
        
        // Validation label
        validationLabel = new JLabel(" "); // Add space to reserve height
        validationLabel.setFont(UI_FONT);
        validationLabel.setForeground(ERROR_COLOR);
        validationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        validationLabel.setPreferredSize(new Dimension(300, 20)); // Fixed height
        validationLabel.setMaximumSize(new Dimension(300, 20));
        panel.add(validationLabel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Register button
        registerButton = createStyledButton("Register", REGISTER_BUTTON_COLOR);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension buttonSize = new Dimension(250, 40); // Wider button
        registerButton.setPreferredSize(buttonSize);
        registerButton.setMaximumSize(buttonSize);
        registerButton.setMinimumSize(buttonSize);
        
        // Add action listener
        registerButton.addActionListener(this::handleRegistration);
        
        panel.add(registerButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Login link
        loginLink = new JLabel("Already have an account? Login here");
        loginLink.setFont(UI_FONT);
        loginLink.setForeground(LINK_COLOR);
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add underline on hover and click action
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setText("<html><u>Already have an account? Login here</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setText("Already have an account? Login here");
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Hide register frame and show login frame (no animation)
                setVisible(false);
                dispose();
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
        
        panel.add(loginLink);
    }
    
    // Helper to create a panel containing a label and a text field
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(UI_FONT);
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Ensure field takes available width
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height)); 

        fieldPanel.add(label);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 3))); // Small gap
        fieldPanel.add(field);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Gap below field
        
        return fieldPanel;
    }

    // Helper method to handle registration logic
    private void handleRegistration(java.awt.event.ActionEvent e) {
        if (validateRegistration()) {
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            // Register user in database
            DatabaseManager dbManager = DatabaseManager.getInstance();
            
            // Check if username or email already exists
            if (dbManager.usernameExists(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (dbManager.emailExists(email)) {
                JOptionPane.showMessageDialog(this, "Email already registered.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Register the user
            boolean success = dbManager.registerUser(fullName, email, username, password);
            
            if (success) {
                // Get the user ID of the newly registered user
                dbManager.authenticateUser(username, password); // Removed unused variable userId
                
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Hide register frame and show login frame (no animation)
                setVisible(false);
                dispose();
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Validation logic
    private boolean validateRegistration() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Reset validation label
        validationLabel.setText(" ");

        if (name.isEmpty() || name.equals("Full Name")) {
            showValidationError("Full Name is required.");
            return false;
        }
        if (email.isEmpty() || email.equals("Email Address")) {
            showValidationError("Email is required.");
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showValidationError("Invalid email format.");
            return false;
        }
        if (username.isEmpty() || username.equals("Username")) {
            showValidationError("Username is required.");
            return false;
        }
        if (password.isEmpty() || password.equals("Password")) {
            showValidationError("Password is required.");
            return false;
        }
        if (password.length() < 6) { // Example: Minimum password length
            showValidationError("Password must be at least 6 characters.");
            return false;
        }
        if (confirmPassword.isEmpty() || confirmPassword.equals("Confirm Password")) {
            showValidationError("Please confirm your password.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showValidationError("Passwords do not match.");
            return false;
        }
        
        return true; // All validations passed
    }
    
    private void showValidationError(String message) {
        validationLabel.setText(message);
    }
}
