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
    
    // Custom duration input fields
    private JSpinner focusSpinner;
    private JSpinner shortBreakSpinner;
    private JSpinner longBreakSpinner;
    private JSpinner cyclesSpinner;
    private JPanel customPanel;
    private JButton applyButton;

    public PomodoroView() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Pomodoro Timer");
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null); // Center the window
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Preset ComboBox
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        presetComboBox = new JComboBox<>(new String[]{"Belajar", "Kerja", "Membaca", "Menulis", "Custom"});
        add(presetComboBox, gbc);

        // Custom settings panel (initially hidden)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        customPanel = createCustomPanel();
        customPanel.setVisible(false);
        add(customPanel, gbc);

        // Time Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.ipady = 20; // Make label taller
        timeLabel = new JLabel("25:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        add(timeLabel, gbc);

        // Status Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.ipady = 0; // Reset padding
        statusLabel = new JLabel("Status: Siap (Fokus 1 dari 4)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(statusLabel, gbc);

        // Buttons
        gbc.gridy = 4;
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
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        add(progressBar, gbc);

        pack(); // Adjust window size to fit components
        setMinimumSize(getSize()); // Prevent resizing smaller than packed size
    }

    private JPanel createCustomPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Pengaturan Custom"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        // Focus duration
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Fokus (menit):"), gbc);
        gbc.gridx = 1;
        focusSpinner = new JSpinner(new SpinnerNumberModel(25, 1, 120, 1));
        focusSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(focusSpinner, gbc);

        // Short break duration
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Istirahat Singkat (menit):"), gbc);
        gbc.gridx = 1;
        shortBreakSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
        shortBreakSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(shortBreakSpinner, gbc);

        // Long break duration
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Istirahat Panjang (menit):"), gbc);
        gbc.gridx = 1;
        longBreakSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 120, 1));
        longBreakSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(longBreakSpinner, gbc);

        // Cycles before long break
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Siklus sebelum istirahat panjang:"), gbc);
        gbc.gridx = 1;
        cyclesSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 10, 1));
        cyclesSpinner.setPreferredSize(new Dimension(80, 25));
        panel.add(cyclesSpinner, gbc);

        // Apply button
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        applyButton = new JButton("Terapkan Pengaturan");
        panel.add(applyButton, gbc);

        return panel;
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

    public void showCustomPanel(boolean show) {
        customPanel.setVisible(show);
        pack(); // Resize window to accommodate the panel
        repaint();
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

    public void addApplyCustomSettingsListener(ActionListener listener) {
        applyButton.addActionListener(listener);
    }

    // --- Getters for Controller --- 
    public String getSelectedPreset() {
        return (String) presetComboBox.getSelectedItem();
    }

    public int getCustomFocusDuration() {
        return (Integer) focusSpinner.getValue();
    }

    public int getCustomShortBreakDuration() {
        return (Integer) shortBreakSpinner.getValue();
    }

    public int getCustomLongBreakDuration() {
        return (Integer) longBreakSpinner.getValue();
    }

    public int getCustomCycles() {
        return (Integer) cyclesSpinner.getValue();
    }

    public void setCustomValues(int focus, int shortBreak, int longBreak, int cycles) {
        focusSpinner.setValue(focus);
        shortBreakSpinner.setValue(shortBreak);
        longBreakSpinner.setValue(longBreak);
        cyclesSpinner.setValue(cycles);
    }

    // Main method for testing the view independently (optional)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PomodoroView view = new PomodoroView();
            view.setVisible(true);
        });
    }
}