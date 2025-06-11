package com.noto.todolist.view;

import javax.swing.*;

import java.awt.*;

/**
 * CategoryButton - A class to add a "Categories" button to the HomeFrame
 * for direct access to the category management page
 */
public class CategoryButton extends JButton {
    private static final Color BUTTON_CATEGORY_COLOR = new Color(230, 230, 250); // Light lavender
    private static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 16);
    
    public CategoryButton(int userId, Runnable onClick) {
        setText("Write Notes");
        setBackground(BUTTON_CATEGORY_COLOR);
        setForeground(Color.BLACK);
        setFocusPainted(false);
        setFont(BOLD_FONT);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_CATEGORY_COLOR.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        addActionListener(e -> {
            if (onClick != null) onClick.run();
        });
    }
    
    // Keep the old constructor for compatibility
    public CategoryButton(int userId) {
        this(userId, null);
    }
}
