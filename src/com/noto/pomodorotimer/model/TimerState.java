package com.noto.pomodorotimer.model;

/**
 * Represents the current state of the Pomodoro timer.
 */
public class TimerState {

    private int secondsRemaining;
    private int currentCycle = 1;
    private boolean isRunning = false;

    // Constructor - initializes with focus duration
    public TimerState(PomodoroConfig config) {
        this.secondsRemaining = config.getFocusDuration();
    }

    // Getters
    public int getSecondsRemaining() {
        return secondsRemaining;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }

    public boolean isRunning() {
        return isRunning;
    }

    // Setters
    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setSecondsRemaining(int secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    // Logic methods
    public void decrementSecond() {
        if (secondsRemaining > 0) {
            secondsRemaining--;
        }
    }

    public void nextSession(PomodoroConfig config) {
        if (currentCycle < config.getTotalCycles()) {
            currentCycle++;
            secondsRemaining = config.getFocusDuration();
        } else {
            // All cycles completed, reset to first cycle
            currentCycle = 1;
            secondsRemaining = config.getFocusDuration();
        }
        isRunning = false; // Timer stops between sessions
    }

    public void resetCurrentSession(PomodoroConfig config) {
        secondsRemaining = config.getFocusDuration();
        isRunning = false;
    }
    
    public void resetToStart(PomodoroConfig config) {
        currentCycle = 1;
        secondsRemaining = config.getFocusDuration();
        isRunning = false;
    }
}
