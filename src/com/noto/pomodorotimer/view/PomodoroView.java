package com.noto.pomodorotimer.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener; // Import ActionListener

/**
 * The main view (GUI) for the Pomodoro Timer application using Java Swing.
 */
public class PomodoroView extends JFrame {

    private JLabel timeLabel;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton resetButton;
    private JButton floatButton;
    private JComboBox<String> presetComboBox;
    private JProgressBar progressBar;

    // TODO: Add input fields for custom durations if needed

    public PomodoroView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Pomodoro Timer");
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the window
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Preset ComboBox
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        presetComboBox = new JComboBox<>(new String[]{"Belajar", "Kerja", "Membaca", "Menulis", "Default"}); // Example presets
        add(presetComboBox, gbc);

        // Time Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.ipady = 20; // Make label taller
        timeLabel = new JLabel("25:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        add(timeLabel, gbc);

        // Status Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.ipady = 0; // Reset padding
        statusLabel = new JLabel("Status: Siap (Fokus 1 dari 4)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(statusLabel, gbc);

        // Buttons
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.25;

        gbc.gridx = 0;
        startButton = new JButton("Mulai");
        add(startButton, gbc);

        gbc.gridx = 1;
        pauseButton = new JButton("Jeda");
        pauseButton.setEnabled(false); // Initially disabled
        add(pauseButton, gbc);

        gbc.gridx = 2;
        resetButton = new JButton("Reset");
        add(resetButton, gbc);

        // Float Button
        gbc.gridx = 3;
        floatButton = new JButton("Float");
        add(floatButton, gbc);
        floatButton.addActionListener(e -> toggleFloatWindow());

        // Progress Bar
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        add(progressBar, gbc);

        pack(); // Adjust window size to fit components
        setMinimumSize(getSize()); // Prevent resizing smaller than packed size
    }

    private void toggleFloatWindow() {
        boolean isFloating = isAlwaysOnTop();
        setAlwaysOnTop(!isFloating);
        floatButton.setText(isFloating ? "Float" : "Unfloat");
    }

    // --- Methods to update the view --- 

    public void updateTime(String timeString) {
        timeLabel.setText(timeString);
    }

    public void updateStatus(String statusString) {
        statusLabel.setText(statusString);
    }

    public void updateProgress(int maxSeconds, int remainingSeconds) {
        int totalDuration = maxSeconds;
        if (totalDuration <= 0) totalDuration = 1; // Avoid division by zero
        int progress = 100 - (int) (((double) remainingSeconds / totalDuration) * 100);
        progressBar.setValue(progress);
        progressBar.setString(String.format("%d%%", progress));
    }
    
    public void setStartButtonEnabled(boolean enabled) {
        startButton.setEnabled(enabled);
    }
    
    public void setPauseButtonEnabled(boolean enabled) {
        pauseButton.setEnabled(enabled);
    }

    // --- Methods to add listeners (called by Controller) ---

    public void addStartButtonListener(ActionListener listener) {
        startButton.addActionListener(listener);
    }

    public void addPauseButtonListener(ActionListener listener) {
        pauseButton.addActionListener(listener);
    }

    public void addResetButtonListener(ActionListener listener) {
        resetButton.addActionListener(listener);
    }

    public void addPresetComboBoxListener(ActionListener listener) {
        presetComboBox.addActionListener(listener);
    }

    // --- Getters for Controller --- 
    public String getSelectedPreset() {
        return (String) presetComboBox.getSelectedItem();
    }

    // Main method for testing the view independently (optional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PomodoroView view = new PomodoroView();
            view.setVisible(true);
        });
    }
}

