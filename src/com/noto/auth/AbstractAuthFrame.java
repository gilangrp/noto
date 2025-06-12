package com.noto.auth;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class AbstractAuthFrame extends JFrame {
    protected static final Font UI_FONT = new Font("SansSerif", Font.PLAIN, 14);
    protected static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 14);
    protected static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    protected static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    protected static final Color TEXT_COLOR = new Color(50, 50, 80);
    protected static final Color BUTTON_COLOR = new Color(186, 225, 255);
    protected static final Color LINK_COLOR = new Color(65, 105, 225);
    protected static final Color BORDER_COLOR = new Color(200, 200, 225);

    protected void applyGlobalStyles() {
        UIManager.put("Label.font", UI_FONT);
        UIManager.put("Button.font", BOLD_FONT);
        UIManager.put("TextField.font", UI_FONT);
        UIManager.put("PasswordField.font", UI_FONT);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", Color.BLACK);
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        UIManager.put("PasswordField.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    protected JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(BOLD_FONT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
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

    protected void addPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('*');
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }
    }
}
