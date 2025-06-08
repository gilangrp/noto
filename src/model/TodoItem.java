package model;

/**
 * Represents a single checklist item within a note.
 * Moved to a separate public class for shared access.
 */
public class TodoItem {
    public String text;
    public boolean completed;

    // Constructor for creating new items
    public TodoItem(String text, boolean completed) {
        this.text = text;
        this.completed = completed;
    }
}
