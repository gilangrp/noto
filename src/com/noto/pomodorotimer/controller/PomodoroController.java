package com.noto.pomodorotimer.controller;

import javax.swing.Timer;

import com.noto.pomodorotimer.model.PomodoroConfig;
import com.noto.pomodorotimer.model.TimerState;
import com.noto.pomodorotimer.view.PomodoroView;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

/**
- Controller for the Pomodoro Timer application.
- Connects the View (GUI) and the Model (Config, State).
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

        // Initial view setup
        updateView();
    }

    private void updateView() {
        int totalSeconds = state.getSecondsRemaining();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        view.updateTime(String.format("%02d:%02d", minutes, seconds));

        String statusText = String.format("Status: %s (%d dari %d)",
                                        getSessionName(state.getCurrentSessionType()),
                                        state.getCurrentCycle(),
                                        config.getCyclesBeforeLongBreak());
        view.updateStatus(statusText);

        int maxDuration = getMaxDurationForCurrentSession();
        view.updateProgress(maxDuration, state.getSecondsRemaining());

        // Update button states
        view.setStartButtonEnabled(!state.isRunning());
        view.setPauseButtonEnabled(state.isRunning());
    }

    private int getMaxDurationForCurrentSession() {
        switch (state.getCurrentSessionType()) {
            case FOCUS:
                return config.getFocusDuration();
            case SHORT_BREAK:
                return config.getShortBreakDuration();
            case LONG_BREAK:
                return config.getLongBreakDuration();
            default:
                return 1; // Should not happen
        }
    }

    private String getSessionName(TimerState.SessionType type) {
        switch (type) {
            case FOCUS: return "Fokus";
            case SHORT_BREAK: return "Istirahat Singkat";
            case LONG_BREAK: return "Istirahat Panjang";
            default: return "Unknown";
        }
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
                    String message = getSessionName(state.getCurrentSessionType()) + " selesai!";
                    state.nextSession(config); // Transition to the next session state
                    message += "\nMulai " + getSessionName(state.getCurrentSessionType()) + "?";
                    // Update view immediately to show next session info before dialog
                    updateView();
                    JOptionPane.showMessageDialog(view, message, "Waktu Habis", JOptionPane.INFORMATION_MESSAGE);
                    // View is already updated, user needs to press Start again
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
            config.loadPreset(selectedPreset);
            state.resetToFocus(config); // Reset state completely based on new config
            updateView();
        }
    }
}

