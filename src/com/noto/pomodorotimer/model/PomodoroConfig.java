package com.noto.pomodorotimer.model;

/**
 * Stores Pomodoro configuration settings (durations, cycles).
 */
public class PomodoroConfig {
    // Default values (in seconds)
    private int focusDuration = 25 * 60;
    private int shortBreakDuration = 5 * 60;
    private int longBreakDuration = 15 * 60;
    private int cyclesBeforeLongBreak = 4;

    public int getFocusDuration() {
        return focusDuration;
    }

    public void setFocusDuration(int focusDuration) {
        this.focusDuration = focusDuration;
    }

    public int getShortBreakDuration() {
        return shortBreakDuration;
    }

    public void setShortBreakDuration(int shortBreakDuration) {
        this.shortBreakDuration = shortBreakDuration;
    }

    public int getLongBreakDuration() {
        return longBreakDuration;
    }

    public void setLongBreakDuration(int longBreakDuration) {
        this.longBreakDuration = longBreakDuration;
    }

    public int getCyclesBeforeLongBreak() {
        return cyclesBeforeLongBreak;
    }

    public void setCyclesBeforeLongBreak(int cyclesBeforeLongBreak) {
        this.cyclesBeforeLongBreak = cyclesBeforeLongBreak;
    }
    
    // Method to set custom configuration
    public void setCustomConfig(int focusMinutes, int shortBreakMinutes, int longBreakMinutes, int cycles) {
        this.focusDuration = focusMinutes * 60;
        this.shortBreakDuration = shortBreakMinutes * 60;
        this.longBreakDuration = longBreakMinutes * 60;
        this.cyclesBeforeLongBreak = cycles;
    }
    
    // Example preset loading (can be expanded)
    public void loadPreset(String presetName) {
        switch (presetName.toLowerCase()) {
            case "belajar":
                this.focusDuration = 25 * 60;
                this.shortBreakDuration = 5 * 60;
                this.longBreakDuration = 15 * 60;
                this.cyclesBeforeLongBreak = 4;
                break;
            case "kerja":
                this.focusDuration = 45 * 60;
                this.shortBreakDuration = 10 * 60;
                this.longBreakDuration = 20 * 60;
                this.cyclesBeforeLongBreak = 3;
                break;
            case "membaca":
                this.focusDuration = 30 * 60;
                this.shortBreakDuration = 7 * 60;
                this.longBreakDuration = 20 * 60;
                this.cyclesBeforeLongBreak = 4;
                break;
            case "menulis":
                this.focusDuration = 50 * 60;
                this.shortBreakDuration = 10 * 60;
                this.longBreakDuration = 25 * 60;
                this.cyclesBeforeLongBreak = 3;
                break;
            case "custom":
                // For custom, don't change values - they should be set via setCustomConfig
                break;
            default:
                // Default Pomodoro
                this.focusDuration = 25 * 60;
                this.shortBreakDuration = 5 * 60;
                this.longBreakDuration = 15 * 60;
                this.cyclesBeforeLongBreak = 4;
                break;
        }
    }
}