package com.noto.todolist.view;

import com.noto.database.DatabaseManager;
import com.noto.todolist.model.NoteData;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * CategoryPage - A page to view and manage note categories
 */
public class CategoryPage extends JPanel {
    private JPanel mainPanel;
    private JPanel categoriesPanel;
    private JPanel categoriesContentPanel; // New: separate content panel for scrolling
    private JPanel notesPanel;
    private JList<String> notesList;
    private DefaultListModel<String> notesListModel;
    private Runnable onBack;
    private int userId;
    private String currentCategory = null;
    private Map<String, Color> categoryColors = new HashMap<>();
    private Map<String, Integer> categoryIds = new HashMap<>();
    private DatabaseManager dbManager;
    private boolean isAdmin;

    public CategoryPage(int userId, Runnable onBack) {
        this.userId = userId;
        this.dbManager = DatabaseManager.getInstance();
        this.onBack = onBack;
        // Cek status admin
        this.isAdmin = dbManager.isUserAdmin(userId);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 600));
        // Set up UI components
        setupUI();
        // Load categories
        loadCategories();
        // Contoh: log status admin
        System.out.println("User is admin: " + isAdmin);
    }

    private void setupUI() {
        mainPanel = new JPanel(new BorderLayout());

        // Add back button at the top
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });
        topPanel.add(backButton, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Categories panel (left side) - Fixed header with scrollable content
        categoriesPanel = new JPanel(new BorderLayout());
        categoriesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        categoriesPanel.setBackground(new Color(245, 245, 245));

        // Category header (fixed at top)
        JPanel categoryHeaderPanel = new JPanel(new BorderLayout());
        categoryHeaderPanel.setBackground(categoriesPanel.getBackground());
        JLabel categoryLabel = new JLabel("Categories");
        categoryLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        categoryHeaderPanel.add(categoryLabel, BorderLayout.WEST);

        // Add category button - Made smaller
        JButton addCategoryBtn = new JButton("+");
        addCategoryBtn.setFont(new Font("SansSerif", Font.BOLD, 12)); // Reduced font size
        addCategoryBtn.setPreferredSize(new Dimension(25, 25)); // Set specific size
        addCategoryBtn.setFocusPainted(false);
        addCategoryBtn.setMargin(new Insets(2, 2, 2, 2)); // Smaller margins
        addCategoryBtn.addActionListener(e -> handleAddCategory());
        categoryHeaderPanel.add(addCategoryBtn, BorderLayout.EAST);

        categoriesPanel.add(categoryHeaderPanel, BorderLayout.NORTH);

        // Categories content panel (scrollable)
        categoriesContentPanel = new JPanel();
        categoriesContentPanel.setLayout(new BoxLayout(categoriesContentPanel, BoxLayout.Y_AXIS));
        categoriesContentPanel.setBackground(categoriesPanel.getBackground());

        // Wrap content panel in scroll pane
        JScrollPane categoriesScrollPane = new JScrollPane(categoriesContentPanel);
        categoriesScrollPane.setBorder(BorderFactory.createEmptyBorder());
        categoriesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        categoriesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        categoriesScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        categoriesPanel.add(categoriesScrollPane, BorderLayout.CENTER);

        // Notes panel (right side)
        notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        notesPanel.setBackground(new Color(250, 250, 250));

        // Notes list
        notesListModel = new DefaultListModel<>();
        notesList = new JList<>(notesListModel);
        notesList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        notesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        notesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedNote();
                }
            }
        });

        JScrollPane notesScrollPane = new JScrollPane(notesList);
        notesScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Notes header
        JPanel notesHeaderPanel = new JPanel(new BorderLayout());
        notesHeaderPanel.setBackground(notesPanel.getBackground());
        JLabel notesLabel = new JLabel("Notes");
        notesLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        notesHeaderPanel.add(notesLabel, BorderLayout.WEST);

        // Add Note button
        JButton addNoteBtn = new JButton("+");
        addNoteBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addNoteBtn.setPreferredSize(new Dimension(25, 25));
        addNoteBtn.setFocusPainted(false);
        addNoteBtn.setMargin(new Insets(2, 2, 2, 2));
        addNoteBtn.addActionListener(e -> handleAddNotePopup());
        notesHeaderPanel.add(addNoteBtn, BorderLayout.EAST);

        notesPanel.add(notesHeaderPanel, BorderLayout.NORTH);
        notesPanel.add(notesScrollPane, BorderLayout.CENTER);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoriesPanel, notesPanel);
        splitPane.setDividerLocation(250);
        splitPane.setOneTouchExpandable(true);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Add mainPanel to this JPanel
        this.removeAll();
        this.add(mainPanel, BorderLayout.CENTER);
    }

    private void loadCategories() {
        // Clear existing categories from content panel only
        categoriesContentPanel.removeAll();

        // Add spacing at top
        categoriesContentPanel.add(Box.createVerticalStrut(10));

        // Get categories from database
        List<Map<String, Object>> categories = dbManager.getUserCategories(userId);

        // Get note counts per category
        Map<Integer, Integer> categoryCounts = dbManager.getCategoryNoteCounts(userId);

        // Add "All Notes" category first
        JPanel allNotesPanel = createCategoryPanel("All Notes", new Color(220, 220, 220), -1);
        categoriesContentPanel.add(allNotesPanel);
        categoriesContentPanel.add(Box.createVerticalStrut(5));

        // Add category buttons
        for (Map<String, Object> category : categories) {
            int categoryId = (int) category.get("id");
            String categoryName = (String) category.get("name");
            String colorHex = (String) category.get("color");

            // Store category ID for later use
            categoryIds.put(categoryName, categoryId);

            // Parse color
            Color color;
            try {
                color = Color.decode(colorHex);
            } catch (NumberFormatException e) {
                color = Color.WHITE;
            }

            // Store color for later use
            categoryColors.put(categoryName, color);

            // Get note count
            int noteCount = categoryCounts.getOrDefault(categoryId, 0);

            // Create category panel
            JPanel categoryPanel = createCategoryPanel(categoryName, color, noteCount);
            categoriesContentPanel.add(categoryPanel);
            categoriesContentPanel.add(Box.createVerticalStrut(5));
        }

        // Add filler to push categories to the top
        categoriesContentPanel.add(Box.createVerticalGlue());

        // Refresh UI
        categoriesContentPanel.revalidate();
        categoriesContentPanel.repaint();

        // Select "All Notes" by default if no category is selected
        if (currentCategory == null) {
            selectCategory("All Notes");
        }
    }

    private JPanel createCategoryPanel(String categoryName, Color color, int noteCount) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(currentCategory != null && currentCategory.equals(categoryName)
                ? color.darker() : color);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        // Category name label
        JLabel nameLabel = new JLabel(categoryName);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Note count label (if available)
        JLabel countLabel = new JLabel();
        if (noteCount >= 0) {
            countLabel.setText(noteCount + " note" + (noteCount == 1 ? "" : "s"));
            countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        }

        // Add labels to panel
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBackground(panel.getBackground());
        labelPanel.add(nameLabel, BorderLayout.NORTH);
        if (noteCount >= 0) {
            labelPanel.add(countLabel, BorderLayout.SOUTH);
        }
        panel.add(labelPanel, BorderLayout.WEST);

        // Add edit/delete buttons for custom categories (not All Notes) - Made smaller
        if (!categoryName.equals("All Notes")) {
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0)); // Reduced spacing
            buttonPanel.setBackground(panel.getBackground());

            // Edit button - Made smaller
            JButton editBtn = new JButton("✎");
            editBtn.setFont(new Font("SansSerif", Font.PLAIN, 10)); // Smaller font
            editBtn.setPreferredSize(new Dimension(20, 20)); // Smaller size
            editBtn.setMargin(new Insets(1, 1, 1, 1)); // Smaller margins
            editBtn.setFocusPainted(false);
            editBtn.setContentAreaFilled(false);
            editBtn.setBorderPainted(false);
            editBtn.addActionListener(e -> handleEditCategory(categoryName));

            // Delete button - Made smaller
            JButton deleteBtn = new JButton("✕");
            deleteBtn.setFont(new Font("SansSerif", Font.PLAIN, 10)); // Smaller font
            deleteBtn.setPreferredSize(new Dimension(20, 20)); // Smaller size
            deleteBtn.setMargin(new Insets(1, 1, 1, 1)); // Smaller margins
            deleteBtn.setFocusPainted(false);
            deleteBtn.setContentAreaFilled(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.addActionListener(e -> handleDeleteCategory(categoryName));

            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
            panel.add(buttonPanel, BorderLayout.EAST);
        }

        // Add click listener
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCategory(categoryName);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (currentCategory == null || !currentCategory.equals(categoryName)) {
                    panel.setBackground(color.darker());
                    updatePanelBackground(panel, panel.getBackground());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                if (currentCategory == null || !currentCategory.equals(categoryName)) {
                    panel.setBackground(color);
                    updatePanelBackground(panel, panel.getBackground());
                }
            }
        });

        return panel;
    }

    // Helper method to update panel background recursively
    private void updatePanelBackground(Container container, Color background) {
        for (Component c : container.getComponents()) {
            c.setBackground(background);
            if (c instanceof Container) {
                updatePanelBackground((Container) c, background);
            }
        }
    }

    private void selectCategory(String categoryName) {
        currentCategory = categoryName;

        // Update UI to show selected category
        loadCategories();

        // Update notes list based on selected category
        loadNotesByCategory(categoryName);

        // Update notes header
        JPanel notesHeaderPanel = (JPanel) notesPanel.getComponent(0);
        JLabel notesLabel = (JLabel) ((BorderLayout) notesHeaderPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
        notesLabel.setText(categoryName + " Notes");
    }

    private void loadNotesByCategory(String categoryName) {
        // Clear notes list
        notesListModel.clear();

        // Load notes berdasarkan admin atau bukan
        Map<String, NoteData> notes;
        if (categoryName.equals("All Notes")) {
            if (isAdmin) {
                notes = dbManager.loadNotesWithAdmin(userId);
            } else {
                notes = dbManager.loadNotes(userId);
            }
        } else {
            notes = dbManager.loadNotesByCategory(userId, categoryName);
        }

        // Add notes to list model
        List<String> sortedTitles = new ArrayList<>(notes.keySet());
        Collections.sort(sortedTitles);
        for (String title : sortedTitles) {
            notesListModel.addElement(title);
        }
    }

    private void handleAddCategory() {
        // Show dialog to add new category
        String categoryName = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(this), "Enter category name:");
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            // Check if category already exists
            if (categoryIds.containsKey(categoryName)) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Category already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Choose color
            Color color = JColorChooser.showDialog(SwingUtilities.getWindowAncestor(this), "Choose Category Color", Color.WHITE);
            if (color == null) {
                color = Color.WHITE;
            }

            // Convert color to hex
            String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

            // Create category in database
            int categoryId = dbManager.createCategory(userId, categoryName, colorHex);
            if (categoryId > 0) {
                // Update UI
                categoryIds.put(categoryName, categoryId);
                categoryColors.put(categoryName, color);
                loadCategories();
                selectCategory(categoryName);
            } else {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Failed to create category.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEditCategory(String categoryName) {
        // Get current category ID
        Integer categoryId = categoryIds.get(categoryName);
        if (categoryId == null) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Category not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show dialog to edit category name
        String newName = JOptionPane.showInputDialog(SwingUtilities.getWindowAncestor(this), "Enter new category name:", categoryName);
        if (newName != null && !newName.trim().isEmpty()) {
            // Check if new name already exists (and it's not the same category)
            if (!newName.equals(categoryName) && categoryIds.containsKey(newName)) {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Category name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Choose color
            Color currentColor = categoryColors.getOrDefault(categoryName, Color.WHITE);
            Color newColor = JColorChooser.showDialog(SwingUtilities.getWindowAncestor(this), "Choose Category Color", currentColor);
            if (newColor == null) {
                newColor = currentColor;
            }

            // Convert color to hex
            String colorHex = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());

            // Update category in database
            boolean success = dbManager.updateCategory(categoryId, newName, colorHex);
            if (success) {
                // Update UI
                categoryIds.remove(categoryName);
                categoryIds.put(newName, categoryId);
                categoryColors.remove(categoryName);
                categoryColors.put(newName, newColor);

                // If this was the selected category, update selection
                if (categoryName.equals(currentCategory)) {
                    currentCategory = newName;
                }

                loadCategories();
            } else {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Failed to update category.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteCategory(String categoryName) {
        // Get current category ID
        Integer categoryId = categoryIds.get(categoryName);
        if (categoryId == null) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Category not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this),
                "Are you sure you want to delete the category '" + categoryName + "'?\n" +
                        "Notes in this category will be moved to 'Uncategorized'.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Delete category in database
            boolean success = dbManager.deleteCategory(userId, categoryId);
            if (success) {
                // Update UI
                categoryIds.remove(categoryName);
                categoryColors.remove(categoryName);

                // If this was the selected category, select All Notes
                if (categoryName.equals(currentCategory)) {
                    currentCategory = "All Notes";
                }

                loadCategories();
            } else {
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), "Failed to delete category.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAddNote() {
        // Create a new note in the current category
        NotesToDo notesApp = new NotesToDo(userId);

        // Set initial category if not All Notes
        if (currentCategory != null && !currentCategory.equals("All Notes")) {
            notesApp.setInitialCategory(currentCategory);
        }

        notesApp.setVisible(true);

        // Add window listener to refresh notes when NotesToDo is closed
        notesApp.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadCategories();
                loadNotesByCategory(currentCategory);
            }
        });
    }

    private void openSelectedNote() {
        String selectedNote = notesList.getSelectedValue();
        if (selectedNote != null) {
            // Open the selected note in NotesToDo
            NotesToDo notesApp = new NotesToDo(userId, selectedNote);
            notesApp.setVisible(true);

            // Add window listener to refresh notes when NotesToDo is closed
            notesApp.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadCategories();
                    loadNotesByCategory(currentCategory);
                }
            });
        }
    }

    // Tambahkan method baru untuk handle add note popup
    private void handleAddNotePopup() {
        if (currentCategory == null || currentCategory.equals("All Notes")) {
            JOptionPane.showMessageDialog(this, "Pilih kategori terlebih dahulu.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String noteTitle = JOptionPane.showInputDialog(this, "Masukkan nama note:");
        if (noteTitle != null && !noteTitle.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Buat note dengan nama: '" + noteTitle + "'?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Simpan ke DB
                Integer categoryId = categoryIds.get(currentCategory);
                if (categoryId == null) {
                    JOptionPane.showMessageDialog(this, "Kategori tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean success = dbManager.createNote(userId, noteTitle, categoryId);
                if (success) {
                    loadCategories();
                    loadNotesByCategory(currentCategory);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal membuat note.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}