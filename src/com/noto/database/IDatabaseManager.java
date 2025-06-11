package com.noto.database;

import com.noto.todolist.model.NoteData;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface IDatabaseManager {
    boolean registerUser(String fullName, String email, String username, String password);
    boolean usernameExists(String username);
    boolean emailExists(String email);
    int authenticateUser(String usernameOrEmail, String password);
    String getUserFullName(int userId);
    ResultSet getUserSettings(int userId);
    void createDefaultSettings(int userId);
    List<Map<String, Object>> getUserCategories(int userId);
    int getCategoryIdByName(int userId, String categoryName);
    int createCategory(int userId, String name, String color);
    boolean updateCategory(int categoryId, String name, String color);
    boolean deleteCategory(int userId, int categoryId);
    Map<Integer, Integer> getCategoryNoteCounts(int userId);
    boolean saveNoteAndTodos(int userId, String title, NoteData data);
    boolean deleteNote(int userId, String title);
    Map<String, NoteData> loadNotes(int userId);
    Map<String, NoteData> loadNotesByCategory(int userId, String categoryName);
    Map<String, Integer> getTaskCounts(int userId);
    List<String> getPendingTaskNotifications(int userId, int limit);
    boolean createNote(int userId, String noteTitle, int categoryId);
    boolean isUserAdmin(int userId);
    Map<String, NoteData> loadNotesWithAdmin(int userId);
    void closeConnection();
}
