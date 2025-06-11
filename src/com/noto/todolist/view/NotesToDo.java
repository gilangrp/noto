package com.noto.todolist.view;

import javax.swing.*;

import com.noto.database.DatabaseManager;
import com.noto.todolist.model.NoteData;
import com.noto.todolist.model.TodoItem;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class NotesToDo extends JFrame {
    private DefaultListModel<String> noteListModel;
    private JList<String> noteList;
    private JTextArea noteContentArea;
    private JPanel todoPanel;
    private JComboBox<String> categoryComboBox;
    
    private Map<String, NoteData> notesMap = new HashMap<>();
    private String currentNoteTitle = null;
    private int userId; // User ID for data association
    private String initialCategory = null; // For pre-selecting category when opened from CategoryPage

    public NotesToDo(int userId) {
        this.userId = userId;
        initializeUI();
    }
    
    // Constructor with initial note selection
    public NotesToDo(int userId, String noteTitle) {
        this.userId = userId;
        initializeUI();
        
        // Select the specified note
        if (noteTitle != null && noteListModel.contains(noteTitle)) {
            noteList.setSelectedValue(noteTitle, true);
        }
    }
    
    private void initializeUI() {
        setTitle("Notes & Checklist - User " + userId);
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout());

        // --- UI Setup (Styles and Components) --- 
        Font uiFont = new Font("SansSerif", Font.PLAIN, 14);
        Font boldFont = new Font("SansSerif", Font.BOLD, 16);
        Color backgroundColor = new Color(245, 245, 245);
        Color selectedColor = new Color(200, 220, 255);
        Color buttonAddColor = new Color(255, 223, 186); // Peach soft
        Color buttonTodoColor = new Color(186, 255, 201); // Green soft
        Color buttonSaveColor = new Color(186, 225, 255); // Blue soft

        UIManager.put("Label.font", uiFont);
        UIManager.put("Button.font", uiFont);
        UIManager.put("TextArea.font", new Font("SansSerif", Font.PLAIN, 16));
        UIManager.put("TextField.font", uiFont);
        UIManager.put("List.font", boldFont);
        UIManager.put("CheckBox.font", uiFont);
        UIManager.put("ComboBox.font", uiFont);
        getContentPane().setBackground(backgroundColor);

        // Left Panel: Note List
        noteListModel = new DefaultListModel<>();
        noteList = new JList<>(noteListModel);
        noteList.setBackground(backgroundColor);
        noteList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                if (isSelected) {
                    label.setBackground(selectedColor);
                    label.setForeground(Color.BLACK);
                } else {
                    label.setBackground(backgroundColor);
                    label.setForeground(Color.BLACK);
                }
                label.setOpaque(true);
                return label;
            }
        });
        JScrollPane listScroll = new JScrollPane(noteList);
        listScroll.setPreferredSize(new Dimension(200, 0));
        listScroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        add(listScroll, BorderLayout.WEST);

        // Center Panel: Content Area and Checklist Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);
        noteContentArea = new JTextArea(12, 30);
        noteContentArea.setMargin(new Insets(10, 10, 10, 10));
        noteContentArea.setLineWrap(true);
        noteContentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(noteContentArea);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(contentScroll, BorderLayout.NORTH);
        todoPanel = new JPanel();
        todoPanel.setLayout(new BoxLayout(todoPanel, BoxLayout.Y_AXIS));
        todoPanel.setBackground(backgroundColor);
        todoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane todoScroll = new JScrollPane(todoPanel);
        todoScroll.setBorder(BorderFactory.createEmptyBorder());
        centerPanel.add(todoScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Top Panel: Buttons and Category Selector
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(backgroundColor);
        
        // Button Panel
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topButtonPanel.setBackground(backgroundColor);
        topButtonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton addNoteBtn = createStyledButton("Add Note", buttonAddColor);
        JButton addToDoBtn = createStyledButton("Add Checklist Item", buttonTodoColor);
        JButton saveBtn = createStyledButton("Save Note", buttonSaveColor);
        JButton deleteNoteBtn = createStyledButton("Delete Note", Color.PINK);
        // JButton viewCategoriesBtn = createStyledButton("Categories", new Color(230, 230, 250)); // Light lavender
        
        topButtonPanel.add(addNoteBtn);
        topButtonPanel.add(addToDoBtn);
        topButtonPanel.add(saveBtn);
        topButtonPanel.add(deleteNoteBtn);
        // topButtonPanel.add(viewCategoriesBtn);
        topPanel.add(topButtonPanel, BorderLayout.NORTH);
        
        // Category Panel
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.setBackground(backgroundColor);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        JLabel categoryLabel = new JLabel("Category:");
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setPreferredSize(new Dimension(200, 25));
        
        categoryPanel.add(categoryLabel);
        categoryPanel.add(categoryComboBox);
        topPanel.add(categoryPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);

        // --- Action Listeners --- 
        addNoteBtn.addActionListener(e -> handleAddNote());
        addToDoBtn.addActionListener(e -> handleAddChecklistItem());
        saveBtn.addActionListener(e -> handleSaveNote());
        deleteNoteBtn.addActionListener(e -> handleDeleteNote());
        // viewCategoriesBtn.addActionListener(e -> handleViewCategories());
        noteList.addListSelectionListener(e -> handleNoteSelection(e));

        // Load categories
        loadCategories();
        
        // Load initial notes using DatabaseManager
        loadFromDatabase();

        // Select first note if available
        if (noteListModel.size() > 0) {
            noteList.setSelectedIndex(0);
        } else {
             noteContentArea.setText("Add or select a note.");
             noteContentArea.setEnabled(false);
             todoPanel.removeAll();
        }

        // Save current note on close (optional, save button is primary)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Consider prompting user if there are unsaved changes
                // handleSaveNote(); // Optionally save automatically
            }
        });
        
        setLocationRelativeTo(null); // Center window
    }
    
    // --- Event Handlers --- 
    private void handleAddNote() {
        String title = JOptionPane.showInputDialog(this, "Enter note title:");
        if (title != null && !title.isBlank()) {
            title = title.trim();
            if (notesMap.containsKey(title)) {
                JOptionPane.showMessageDialog(this, "Note title already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Add to UI and local map (using model.NoteData)
            noteListModel.addElement(title);
            
            // Create new note with selected category
            NoteData newNote = new NoteData();
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            if (selectedCategory != null) {
                newNote.category = selectedCategory;
            }
            
            notesMap.put(title, newNote);
            noteList.setSelectedValue(title, true); // Select the new note
            noteContentArea.requestFocusInWindow(); 
        }
    }

    private void handleAddChecklistItem() {
         if (currentNoteTitle == null) {
             JOptionPane.showMessageDialog(this, "Please select or add a note first.", "Info", JOptionPane.INFORMATION_MESSAGE);
             return;
         }
        addChecklistItemToUI("", false); // Add an empty item
        // Focus the new text field
        if (todoPanel.getComponentCount() > 0) {
             JPanel lastItemPanel = (JPanel) todoPanel.getComponent(todoPanel.getComponentCount() - 1);
             JTextField lastTextField = (JTextField) lastItemPanel.getComponent(1);
             lastTextField.requestFocusInWindow();
        }
    }

    private void handleSaveNote() {
        if (currentNoteTitle != null) {
            saveCurrentNoteDataToMap(currentNoteTitle); // Update local map from UI
            saveToDatabase(currentNoteTitle); // Save this specific note to DB
        } else {
            JOptionPane.showMessageDialog(this, "No note selected to save.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeleteNote() {
        String selected = noteList.getSelectedValue();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the note \'" + selected + "\' and its checklist items?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = deleteNoteFromDatabase(selected);
                if (deleted) {
                    notesMap.remove(selected);
                    noteListModel.removeElement(selected);
                    // Select next or clear UI
                    if (noteListModel.getSize() > 0) {
                        noteList.setSelectedIndex(0);
                    } else {
                        currentNoteTitle = null;
                        noteContentArea.setText("");
                        todoPanel.removeAll();
                        todoPanel.revalidate();
                        todoPanel.repaint();
                    }
                } else {
                     JOptionPane.showMessageDialog(this, "Failed to delete note from database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a note to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // private void handleViewCategories() {
    //     // Open the category page
    //     CategoryPage categoryPage = new CategoryPage(userId);
    //     categoryPage.setVisible(true);
        
    //     // Add window listener to refresh notes when CategoryPage is closed
    //     categoryPage.addWindowListener(new WindowAdapter() {
    //         @Override
    //         public void windowClosed(WindowEvent e) {
    //             // Reload categories and notes
    //             loadCategories();
    //             loadFromDatabase();
                
    //             // Reselect current note if it still exists
    //             if (currentNoteTitle != null && noteListModel.contains(currentNoteTitle)) {
    //                 noteList.setSelectedValue(currentNoteTitle, true);
    //             } else if (noteListModel.size() > 0) {
    //                 noteList.setSelectedIndex(0);
    //             }
    //         }
    //     });
    // }

    private void handleNoteSelection(javax.swing.event.ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selected = noteList.getSelectedValue();
            // Save previous note before switching (optional, consider prompting)
            // if (currentNoteTitle != null && !currentNoteTitle.equals(selected)) {
            //     saveCurrentNoteDataToMap(currentNoteTitle);
            //     // Maybe prompt to save to DB?
            // }
            
            // Load selected note
            if (selected != null) {
                currentNoteTitle = selected;
                loadNoteDataToUI(selected);
            } else {
                currentNoteTitle = null;
                noteContentArea.setText("");
                todoPanel.removeAll();
                todoPanel.revalidate();
                todoPanel.repaint();
            }
        }
    }

    // --- UI Helper Methods --- 
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }

    private void addChecklistItemToUI(String text, boolean checked) {
        JPanel itemPanel = new JPanel(new BorderLayout(5, 0));
        itemPanel.setBackground(todoPanel.getBackground());
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(checked);
        checkBox.setBackground(itemPanel.getBackground());
        JTextField textField = new JTextField(text);
        textField.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
             BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        textField.setBackground(itemPanel.getBackground());
        
        JButton deleteBtn = new JButton("✕");
        deleteBtn.setForeground(Color.RED);
        deleteBtn.setMargin(new Insets(0, 4, 0, 4));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.addActionListener(e -> {
            todoPanel.remove(itemPanel);
            todoPanel.revalidate();
            todoPanel.repaint();
        });

        itemPanel.add(checkBox, BorderLayout.WEST);
        itemPanel.add(textField, BorderLayout.CENTER);
        itemPanel.add(deleteBtn, BorderLayout.EAST);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        todoPanel.add(itemPanel);
        todoPanel.revalidate();
        todoPanel.repaint();
    }
    
    // Load categories into combo box
    private void loadCategories() {
        categoryComboBox.removeAllItems();
        
        // Get categories from database
        DatabaseManager dbManager = DatabaseManager.getInstance();
        List<Map<String, Object>> categories = dbManager.getUserCategories(userId);
        
        // Add categories to combo box
        for (Map<String, Object> category : categories) {
            String categoryName = (String) category.get("name");
            categoryComboBox.addItem(categoryName);
        }
        
        // Select initial category if specified
        if (initialCategory != null) {
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                if (initialCategory.equals(categoryComboBox.getItemAt(i))) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
            initialCategory = null; // Reset after use
        }
    }

    // --- Data Handling Methods --- 
    private void loadNoteDataToUI(String title) {
        noteContentArea.setText("");
        todoPanel.removeAll();
        noteContentArea.setEnabled(true);

        NoteData data = notesMap.get(title);
        if (data != null) {
            noteContentArea.setText(data.content); // Accessing public field
            
            // Set category in combo box
            if (data.category != null) {
                for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                    if (data.category.equals(categoryComboBox.getItemAt(i))) {
                        categoryComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            for (TodoItem item : data.todos) { // Accessing public field
                addChecklistItemToUI(item.text, item.completed); // Accessing public fields
            }
        } else {
            noteContentArea.setText("Error loading note data.");
            noteContentArea.setEnabled(false);
        }

        todoPanel.revalidate();
        todoPanel.repaint();
    }

    // Saves the current UI state (content area, checklist items) to the notesMap for the given title
    private void saveCurrentNoteDataToMap(String title) {
        if (title == null || !notesMap.containsKey(title)) return;
        
        NoteData data = notesMap.get(title);
        data.content = noteContentArea.getText(); // Accessing public field
        
        // Save selected category
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        if (selectedCategory != null) {
            data.category = selectedCategory; // Accessing public field
        }
        
        data.todos.clear(); // Accessing public field

        for (Component c : todoPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c; // Cast explicitly
                if (p.getComponentCount() == 3) { // Check count after casting
                    try {
                        JCheckBox check = (JCheckBox) p.getComponent(0);
                        JTextField field = (JTextField) p.getComponent(1);
                        if (!field.getText().trim().isEmpty()) {
                             // Use model.TodoItem constructor
                             data.todos.add(new TodoItem(field.getText(), check.isSelected())); // Accessing public field
                        }
                    } catch (ClassCastException | ArrayIndexOutOfBoundsException ex) {
                        System.err.println("Error processing checklist item panel component: " + ex.getMessage());
                    }
                }
            }
        }
    }

    // --- Database Operations using DatabaseManager --- 
    
    // Loads all notes for the current user from the database into the notesMap and UI list
    private void loadFromDatabase() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        notesMap = dbManager.loadNotes(userId);
        
        // Update UI list model
        noteListModel.clear();
        List<String> sortedTitles = new ArrayList<>(notesMap.keySet());
        Collections.sort(sortedTitles);
        for (String title : sortedTitles) {
            noteListModel.addElement(title);
        }
        System.out.println("Loaded " + notesMap.size() + " notes from database for user " + userId);
    }

    // Saves a specific note (identified by title) and its todos to the database
    private void saveToDatabase(String title) {
        if (title == null || !notesMap.containsKey(title)) {
             System.err.println("Attempted to save null or non-existent note title: " + title);
             return;
        }
        NoteData dataToSave = notesMap.get(title);
        DatabaseManager dbManager = DatabaseManager.getInstance();
        boolean success = dbManager.saveNoteAndTodos(userId, title, dataToSave);
        if (success) {
            System.out.println("Note '" + title + "' saved successfully to database.");
            // Optionally show a status message to the user
            // JOptionPane.showMessageDialog(this, "Note saved.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save note '" + title + "' to database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete note from database
    private boolean deleteNoteFromDatabase(String title) {
        if (title == null || title.trim().isEmpty()) {
            return false;
        }
        DatabaseManager dbManager = DatabaseManager.getInstance();
        return dbManager.deleteNote(userId, title);
    }
    
    // Set initial category (used when opened from CategoryPage)
    public void setInitialCategory(String category) {
        this.initialCategory = category;
        
        // If UI is already initialized, set the category now
        if (categoryComboBox != null) {
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                if (category.equals(categoryComboBox.getItemAt(i))) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
            initialCategory = null; // Reset after use
        }
    }
}
