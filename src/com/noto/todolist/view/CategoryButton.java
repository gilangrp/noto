package com.noto.todolist.view;

import javax.swing.*;

import com.noto.database.DatabaseManager;

import java.awt.*;
import java.awt.event.*;

/**
 * CategoryButton - A class to add a "Categories" button to the HomeFrame
 * for direct access to the category management page
 */
public class CategoryButton extends JButton {
    private static final Color BUTTON_CATEGORY_COLOR = new Color(230, 230, 250); // Light lavender
    private static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 16);
    
    public CategoryButton(int userId) {
        setText("Categories");
        setBackground(BUTTON_CATEGORY_COLOR);
        setForeground(Color.BLACK);
        setFocusPainted(false);
        setFont(BOLD_FONT);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BUTTON_CATEGORY_COLOR.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                CategoryPage categoryPage = new CategoryPage(userId);
                categoryPage.setVisible(true);
            });
        });
    }
}
