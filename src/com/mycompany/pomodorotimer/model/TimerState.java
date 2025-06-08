package com.mycompany.pomodorotimer.model;

/**
 * Represents the current state of the Pomodoro timer.
 */
public class TimerState {

    public enum SessionType {
        FOCUS,
        SHORT_BREAK,
        LONG_BREAK
    }

    private SessionType currentSessionType = SessionType.FOCUS;
    private int secondsRemaining;
    private int currentCycle = 1;
    private boolean isRunning = false;

    // Constructor - initializes with focus duration
    public TimerState(PomodoroConfig config) {
        this.secondsRemaining = config.getFocusDuration();
    }

    // Getters
    public SessionType getCurrentSessionType() {
        return currentSessionType;
    }

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
        if (currentSessionType == SessionType.FOCUS) {
            if (currentCycle >= config.getCyclesBeforeLongBreak()) {
                currentSessionType = SessionType.LONG_BREAK;
                secondsRemaining = config.getLongBreakDuration();
                currentCycle = 1; // Reset cycle count after long break
            } else {
                currentSessionType = SessionType.SHORT_BREAK;
                secondsRemaining = config.getShortBreakDuration();
            }
        } else { // If current session was SHORT_BREAK or LONG_BREAK
            currentSessionType = SessionType.FOCUS;
            secondsRemaining = config.getFocusDuration();
            if (currentSessionType != SessionType.LONG_BREAK) { // Only increment cycle after short break
                 currentCycle++;
            }
        }
        isRunning = false; // Timer stops between sessions
    }

    public void resetCurrentSession(PomodoroConfig config) {
         switch (currentSessionType) {
            case FOCUS:
                secondsRemaining = config.getFocusDuration();
                break;
            case SHORT_BREAK:
                secondsRemaining = config.getShortBreakDuration();
                break;
            case LONG_BREAK:
                secondsRemaining = config.getLongBreakDuration();
                break;
        }
        isRunning = false;
    }
    
    public void resetToFocus(PomodoroConfig config) {
        currentSessionType = SessionType.FOCUS;
        currentCycle = 1;
        secondsRemaining = config.getFocusDuration();
        isRunning = false;
    }
}

