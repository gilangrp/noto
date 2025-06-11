package com.noto.pomodorotimer.model;

/**
 * Stores Pomodoro configuration settings (durations, cycles).
 */
public class PomodoroConfig {
    // Default values (in seconds)
    private int focusDuration = 25 * 60;
    private int totalCycles = 4;

    public int getFocusDuration() {
        return focusDuration;
    }

    public void setFocusDuration(int focusDuration) {
        this.focusDuration = focusDuration;
    }

    public int getTotalCycles() {
        return totalCycles;
    }

    public void setTotalCycles(int totalCycles) {
        this.totalCycles = totalCycles;
    }
    
    // Method to set custom configuration
    public void setCustomConfig(int focusMinutes, int cycles) {
        this.focusDuration = focusMinutes * 60;
        this.totalCycles = cycles;
    }
    
    // Example preset loading (can be expanded)
    public void loadPreset(String presetName) {
        switch (presetName.toLowerCase()) {
            case "belajar":
                this.focusDuration = 25 * 60;
                this.totalCycles = 4;
                break;
            case "kerja":
                this.focusDuration = 45 * 60;
                this.totalCycles = 6;
                break;
            case "membaca":
                this.focusDuration = 30 * 60;
                this.totalCycles = 4;
                break;
            case "menulis":
                this.focusDuration = 50 * 60;
                this.totalCycles = 3;
                break;
            case "custom":
                // For custom, don't change values - they should be set via setCustomConfig
                break;
            default:
                // Default Pomodoro
                this.focusDuration = 25 * 60;
                this.totalCycles = 4;
                break;
        }
    }
}