import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.awt.event.*;
// Removed java.sql.* as direct connection is no longer used here
import java.util.*;
import java.util.List; // Explicit import for List
// Import the new NotesToDo class
import todolistapp.NotesToDo;
// Import CategoryPage and CategoryButton
import todolistapp.CategoryPage;
import todolistapp.CategoryButton;
// Import DatabaseManager for stats/notifications
import database.DatabaseManager;

import com.mycompany.pomodorotimer.controller.PomodoroController;
import com.mycompany.pomodorotimer.model.PomodoroConfig;
import com.mycompany.pomodorotimer.model.TimerState;
import com.mycompany.pomodorotimer.view.*;

/**
 * Redesigned Home frame displaying statistics, notifications, and navigation.
 */
public class HomeFrame extends JFrame {
    private int userId;
    private String fullName;
    private JPanel mainPanel;
    // Panels for new layout
    private JPanel statsPanel;
    private JPanel notificationPanel;
    private JPanel navigationPanel;
    // UI Components to update
    private JTextArea statsArea;
    private JTextArea notificationArea;

    // Style constants from NotesToDo
    private static final Font UI_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color HEADER_BG_COLOR = new Color(65, 105, 225); // Keep original header color for now
    private static final Color BUTTON_NOTES_COLOR = new Color(186, 225, 255); // Blue soft
    private static final Color BUTTON_POMODORO_COLOR = new Color(255, 223, 186); // Peach soft
    private static final Color PANEL_BORDER_COLOR = new Color(200, 200, 225);

    public HomeFrame(int userId, String fullName) {
        this.userId = userId;
        this.fullName = fullName;

        // Apply global styles
        applyGlobalStyles();

        // Set up the frame
        setTitle("Dashboard - " + fullName);
        setSize(700, 500); // Adjusted size for new layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel
        mainPanel = new JPanel(new BorderLayout(10, 10)); // Add gaps
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        setContentPane(mainPanel);

        // Create header panel (reuse existing style for now)
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create center content panel (Stats & Notifications)
        JPanel centerContentPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Side-by-side layout
        centerContentPanel.setOpaque(false);

        statsPanel = createStatsPanel();
        notificationPanel = createNotificationPanel();

        centerContentPanel.add(statsPanel);
        centerContentPanel.add(notificationPanel);

        mainPanel.add(centerContentPanel, BorderLayout.CENTER);

        // Create bottom navigation panel
        navigationPanel = createNavigationPanel();
        mainPanel.add(navigationPanel, BorderLayout.SOUTH);

        // Load data for stats and notifications
        loadDashboardData();
    }

    private void applyGlobalStyles() {
        UIManager.put("Label.font", UI_FONT);
        UIManager.put("Button.font", UI_FONT);
        UIManager.put("TextArea.font", UI_FONT);
        UIManager.put("TextField.font", UI_FONT);
        UIManager.put("List.font", BOLD_FONT);
        UIManager.put("CheckBox.font", UI_FONT);
        UIManager.put("Panel.background", BACKGROUND_COLOR);
        UIManager.put("ScrollPane.background", BACKGROUND_COLOR);
        UIManager.put("Viewport.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", Color.BLACK);
    }

    // Header Panel - Reusing previous style, might need adjustment later
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + fullName);
        welcomeLabel.setFont(HEADER_FONT);
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255, 100, 100)); // Reddish color
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(logoutButton.getBackground().darker(), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                setVisible(false);
                dispose();
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });

        headerPanel.add(logoutButton, BorderLayout.EAST);
        return headerPanel;
    }

    // Panel for Task Statistics
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(createStyledPanelBorder("Task Statistics"));
        panel.setOpaque(false);

        statsArea = new JTextArea("Loading statistics...");
        statsArea.setEditable(false);
        statsArea.setOpaque(false);
        statsArea.setFont(UI_FONT);
        statsArea.setLineWrap(true);
        statsArea.setWrapStyleWord(true);
        statsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Panel for Pending Task Notifications
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(createStyledPanelBorder("Pending Tasks"));
        panel.setOpaque(false);

        notificationArea = new JTextArea("Loading pending tasks...");
        notificationArea.setEditable(false);
        notificationArea.setOpaque(false);
        notificationArea.setFont(UI_FONT);
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);
        notificationArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(notificationArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Panel for Navigation Buttons
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);

        JButton notesButton = createStyledButton("Notes & ToDo", BUTTON_NOTES_COLOR);
        JButton pomodoroButton = createStyledButton("Pomodoro Timer", BUTTON_POMODORO_COLOR);
        CategoryButton categoryButton = new CategoryButton(userId);

        notesButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                NotesToDo notesFrame = new NotesToDo(userId);
                notesFrame.setVisible(true);
            });
        });

        pomodoroButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                PomodoroConfig config = new PomodoroConfig();
                TimerState state = new TimerState(config);
                PomodoroView pomodoroFrame = new PomodoroView();
                PomodoroController controller = new PomodoroController(config, state, pomodoroFrame);
                pomodoroFrame.setVisible(true);
            });
        });

        panel.add(notesButton);
        panel.add(categoryButton);
        panel.add(pomodoroButton);
        return panel;
    }

    // Helper to create styled buttons (consistent with NotesToDo)
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(BOLD_FONT); // Make nav buttons bold
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        return button;
    }

    // Helper to create styled panel borders
    private Border createStyledPanelBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PANEL_BORDER_COLOR, 1, true),
                        " " + title + " ",
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        BOLD_FONT,
                        PANEL_BORDER_COLOR.darker()),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    // Method to load data for stats and notifications using DatabaseManager API
    private void loadDashboardData() {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        // Get task counts
        Map<String, Integer> counts = dbManager.getTaskCounts(userId);
        int totalTasks = counts.getOrDefault("total", 0);
        int completedTasks = counts.getOrDefault("completed", 0);
        int pendingTasks = counts.getOrDefault("pending", 0);

        // Get pending task notifications (limit to 5 for display)
        List<String> notifications = dbManager.getPendingTaskNotifications(userId, 5);

        // Update UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            statsArea.setText(String.format("Total Tasks: %d\nCompleted: %d\nPending: %d",
                    totalTasks, completedTasks, pendingTasks));

            if (notifications.isEmpty()) {
                notificationArea.setText("No pending tasks. Great job!");
            } else {
                notificationArea.setText(String.join("\n", notifications));
            }
        });
    }
}
