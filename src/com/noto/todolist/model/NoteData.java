package com.noto.todolist.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data for a single note, including its content, category, and checklist items.
 * Updated to include category support.
 */
public class NoteData {
    public String content = "";
    public String category = "Uncategorized"; // Default category
    public List<TodoItem> todos = new ArrayList<>();
}
