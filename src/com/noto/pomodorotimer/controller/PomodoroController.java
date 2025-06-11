package com.noto.pomodorotimer.controller;

import javax.swing.Timer;

import com.noto.pomodorotimer.model.PomodoroConfig;
import com.noto.pomodorotimer.model.TimerState;
import com.noto.pomodorotimer.view.PomodoroView;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Import sound libraries
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

/**
 * Controller for the Pomodoro Timer application.
 * Connects the View (GUI) and the Model (Config, State).
 */
public class PomodoroController {

    private PomodoroConfig config;
    private TimerState state;
    private PomodoroView view;
    private Timer swingTimer;

    public PomodoroController(PomodoroConfig config, TimerState state, PomodoroView view) {
        this.config = config;
        this.state = state;
        this.view = view;

        // Initialize the timer
        swingTimer = new Timer(1000, new TimerActionListener()); // Fires every 1000ms (1 second)

        // Add listeners to the view components
        this.view.addStartButtonListener(new StartButtonListener());
        this.view.addPauseButtonListener(new PauseButtonListener());
        this.view.addResetButtonListener(new ResetButtonListener());
        this.view.addPresetComboBoxListener(new PresetComboBoxListener());
        this.view.addApplyCustomSettingsListener(new ApplyCustomSettingsListener());

        // Initial view setup
        updateView();
    }

    private void updateView() {
        int totalSeconds = state.getSecondsRemaining();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        view.updateTime(String.format("%02d:%02d", minutes, seconds));

        String statusText = String.format("Status: Sesi %d dari %d",
                                        state.getCurrentCycle(),
                                        config.getTotalCycles());
        view.updateStatus(statusText);

        int maxDuration = config.getFocusDuration();
        view.updateProgress(maxDuration, state.getSecondsRemaining());

        // Update button states
        view.setStartButtonEnabled(!state.isRunning());
        view.setPauseButtonEnabled(state.isRunning());
    }

    // Method to play the sound
    private void playSound() {
        try {
            // Try to load from classpath (for JAR and IDE)
            URL soundURL = getClass().getResource("/com/noto/resources/sounds/timer_alarm.wav");
            if (soundURL == null) {
                // Try alternative path (for development)
                soundURL = getClass().getResource("/sounds/timer_alarm.wav");
            }
            if (soundURL == null) {
                // Try loading from file system (for dev/testing)
                java.io.File file = new java.io.File("src/com/noto/resources/sounds/timer_alarm.wav");
                if (file.exists()) {
                    soundURL = file.toURI().toURL();
                }
            }
            if (soundURL == null) {
                System.err.println("Sound file not found: timer_alarm.wav");
                java.awt.Toolkit.getDefaultToolkit().beep();
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + e.getMessage());
            java.awt.Toolkit.getDefaultToolkit().beep();
            e.printStackTrace();
        }
    }

    // --- Inner classes for Action Listeners ---

    class TimerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (state.isRunning()) {
                state.decrementSecond();
                updateView();

                if (state.getSecondsRemaining() <= 0) {
                    swingTimer.stop();
                    state.setRunning(false);
                    playSound(); // Play the custom sound instead of beep
                    
                    String message;
                    if (state.getCurrentCycle() >= config.getTotalCycles()) {
                        message = "Semua sesi selesai!\nAnda telah menyelesaikan " + config.getTotalCycles() + " sesi fokus.";
                        JOptionPane.showMessageDialog(view, message, "Selamat!", JOptionPane.INFORMATION_MESSAGE);
                        // Reset to first cycle
                        state.resetToStart(config);
                    } else {
                        message = "Sesi " + state.getCurrentCycle() + " selesai!\nLanjut ke sesi berikutnya?";
                        int option = JOptionPane.showConfirmDialog(view, message, "Sesi Selesai", 
                                                                 JOptionPane.YES_NO_OPTION, 
                                                                 JOptionPane.INFORMATION_MESSAGE);
                        if (option == JOptionPane.YES_OPTION) {
                            state.nextSession(config);
                        }
                    }
                    updateView();
                }
            }
        }
    }

    class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!state.isRunning()) {
                state.setRunning(true);
                swingTimer.start();
                updateView(); // Update button states
            }
        }
    }

    class PauseButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (state.isRunning()) {
                state.setRunning(false);
                swingTimer.stop();
                updateView(); // Update button states
            }
        }
    }

    class ResetButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            swingTimer.stop();
            state.resetCurrentSession(config);
            updateView();
        }
    }

    class PresetComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            swingTimer.stop();
            String selectedPreset = view.getSelectedPreset();
            
            if ("Custom".equals(selectedPreset)) {
                // Show custom panel and set current values
                view.setCustomValues(
                    config.getFocusDuration() / 60,
                    config.getTotalCycles()
                );
                view.showCustomPanel(true);
            } else {
                // Hide custom panel and load preset
                view.showCustomPanel(false);
                config.loadPreset(selectedPreset);
                state.resetToStart(config); // Reset state completely based on new config
                updateView();
            }
        }
    }

    class ApplyCustomSettingsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                // Get custom values from view
                int focusMinutes = view.getCustomFocusDuration();
                int cycles = view.getCustomCycles();

                // Validate input
                if (focusMinutes < 1 || cycles < 1) {
                    JOptionPane.showMessageDialog(view, 
                        "Semua nilai harus lebih besar dari 0!", 
                        "Input Tidak Valid", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Stop timer if running
                swingTimer.stop();
                
                // Apply custom configuration
                config.setCustomConfig(focusMinutes, cycles);
                
                // Reset state to start with new configuration
                state.resetToStart(config);
                
                // Update view
                updateView();
                
                // Show confirmation
                JOptionPane.showMessageDialog(view, 
                    "Pengaturan custom berhasil diterapkan!\n" +
                    "Durasi Fokus: " + focusMinutes + " menit\n" +
                    "Jumlah Sesi: " + cycles, 
                    "Pengaturan Diterapkan", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, 
                    "Terjadi kesalahan saat menerapkan pengaturan: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}