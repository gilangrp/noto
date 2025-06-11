package com.noto.database;

import java.sql.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.noto.todolist.model.NoteData;
import com.noto.todolist.model.TodoItem;

/**
 * Database manager for handling all database operations using MySQL.
 */
public class DatabaseManager implements IDatabaseManager {
    // --- MySQL Configuration ---
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "todolist_db";
    private static final String DB_USER = "root"; // Default MySQL username for testing
    private static final String DB_PASSWORD = ""; // Empty password for testing
    private static final String DB_URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?user=" + DB_USER
            + "&password=" + DB_PASSWORD + "&serverTimezone=UTC&createDatabaseIfNotExist=true";
    // --- End MySQL Configuration ---

    private static final String SCHEMA_FILE = "src/database/TodoList_mysql.sql";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connecting to database: " + DB_URL.replace(DB_PASSWORD, "********"));
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase(); // Initialize core tables (users, etc.)
            System.out.println("MySQL Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error connecting to MySQL database: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        try {
            if (instance.connection == null || instance.connection.isClosed()) {
                System.out.println("Database connection lost or not established. Re-initializing...");
                instance = new DatabaseManager();
            }
        } catch (SQLException e) {
            System.err.println("Error checking database connection status: " + e.getMessage());
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        if (tablesExist("users")) { // Check for a core table
            System.out.println("Core database tables already exist. Checking for notes tables...");
            // Even if core tables exist, we still need to check for notes tables
            if (!tablesExist("notes") || !tablesExist("note_todos") || !tablesExist("note_categories")) {
                System.out.println("Some required tables missing. Running schema script to ensure all tables exist...");
                executeSchemaScript(SCHEMA_FILE);
            } else {
                System.out.println("All required tables exist. Skipping schema initialization.");
            }
            return;
        }
        System.out.println("Initializing database schema from: " + SCHEMA_FILE);
        executeSchemaScript(SCHEMA_FILE);
    }

    // Helper to execute SQL statements from a file (basic version)
    private void executeSchemaScript(String filePath) {
        try {
            String schema = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] statements = schema.split(";(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"); // Basic split handling
                                                                                               // double quotes

            try (Statement stmt = connection.createStatement()) {
                for (String statement : statements) {
                    String trimmedStatement = statement.trim();
                    if (!trimmedStatement.isEmpty()) {
                        System.out.println("Executing Schema: "
                                + trimmedStatement.substring(0, Math.min(trimmedStatement.length(), 100)) + "...");
                        stmt.execute(trimmedStatement);
                    }
                }
            }
            System.out.println("Schema script executed successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading schema file [" + filePath + "]: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error executing schema script [" + filePath + "]: " + e.getMessage());
        }
    }

    private boolean tablesExist(String tableName) {
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking if table \t" + tableName + "\t exists: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("MySQL Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing MySQL database connection: " + e.getMessage());
        }
    }

    // --- User Management (Existing Methods - check for correctness) ---
    public boolean registerUser(String fullName, String email, String username, String password) {
        String sql = "INSERT INTO users (full_name, email, username, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, username);
            pstmt.setString(4, password); // HASHING IS CRITICAL
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                int userId = getUserIdByUsername(username);
                if (userId > 0)
                    createDefaultSettings(userId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }

    public int authenticateUser(String usernameOrEmail, String password) {
        String sql = "SELECT user_id FROM users WHERE (username = ? OR email = ?) AND password = ?"; // HASHING!
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, usernameOrEmail);
            pstmt.setString(2, usernameOrEmail);
            pstmt.setString(3, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                updateLastLogin(userId);
                return userId;
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return -1;
    }

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }

    public String getUserFullName(int userId) {
        String sql = "SELECT full_name FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user full name: " + e.getMessage());
        }
        return "";
    }

    private int getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user ID by username: " + e.getMessage());
        }
        return -1;
    }

    // --- Settings Management (Existing Methods) ---
    public ResultSet getUserSettings(int userId) {
        String sql = "SELECT * FROM user_settings WHERE user_id = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, userId);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting user settings: " + e.getMessage());
            return null;
        }
    }

    public void createDefaultSettings(int userId) {
        String sql = "INSERT IGNORE INTO user_settings (user_id) VALUES (?)";
        if (userId <= 0) {
            System.err.println("Attempted to create settings for invalid userId: " + userId);
            return;
        }
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating default settings: " + e.getMessage());
        }
    }

    // --- Category Management (New Methods) ---
    /**
     * Gets all categories for a user.
     */
    public List<Map<String, Object>> getUserCategories(int userId) {
        List<Map<String, Object>> categories = new ArrayList<>();
        String sql = "SELECT category_id, name, color FROM note_categories WHERE user_id = ? ORDER BY name ASC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> category = new HashMap<>();
                category.put("id", rs.getInt("category_id"));
                category.put("name", rs.getString("name"));
                category.put("color", rs.getString("color"));
                categories.add(category);
            }
            System.out.println("Loaded " + categories.size() + " categories for user ID: " + userId);
        } catch (SQLException e) {
            System.err.println("Error getting user categories: " + e.getMessage());
        }

        return categories;
    }

    /**
     * Gets a category ID by name, creating it if it doesn't exist.
     */
    public int getCategoryIdByName(int userId, String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            categoryName = "Uncategorized";
        }

        String selectSql = "SELECT category_id FROM note_categories WHERE user_id = ? AND name = ?";
        String insertSql = "INSERT INTO note_categories (user_id, name) VALUES (?, ?)";

        try {
            // First try to find existing category
            try (PreparedStatement pstmt = connection.prepareStatement(selectSql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, categoryName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("category_id");
                }
            }

            // If not found, create new category
            try (PreparedStatement pstmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, categoryName);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int newCategoryId = generatedKeys.getInt(1);
                            System.out.println("Created new category '" + categoryName + "' with ID: " + newCategoryId);
                            return newCategoryId;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting/creating category: " + e.getMessage());
        }

        return -1; // Failed to get or create category
    }

    /**
     * Creates a new category for a user.
     */
    public int createCategory(int userId, String name, String color) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Cannot create category with empty name");
            return -1;
        }

        String sql = "INSERT INTO note_categories (user_id, name, color) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, color != null ? color : "#FFFFFF");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int categoryId = generatedKeys.getInt(1);
                        System.out.println("Created category '" + name + "' with ID: " + categoryId);
                        return categoryId;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating category: " + e.getMessage());
        }

        return -1;
    }

    /**
     * Updates an existing category.
     */
    public boolean updateCategory(int categoryId, String name, String color) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Cannot update category with empty name");
            return false;
        }

        String sql = "UPDATE note_categories SET name = ?, color = ? WHERE category_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, color != null ? color : "#FFFFFF");
            pstmt.setInt(3, categoryId);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;
            if (success) {
                System.out.println("Updated category ID " + categoryId + " to '" + name + "'");
            } else {
                System.err.println("No category found with ID: " + categoryId);
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a category and updates all notes in that category to Uncategorized.
     */
    public boolean deleteCategory(int userId, int categoryId) {
        // First, get the Uncategorized category ID (or create it if it doesn't exist)
        int uncategorizedId = getCategoryIdByName(userId, "Uncategorized");
        if (uncategorizedId == -1) {
            System.err.println("Failed to get/create Uncategorized category");
            return false;
        }

        // Don't allow deleting the Uncategorized category
        if (categoryId == uncategorizedId) {
            System.err.println("Cannot delete the Uncategorized category");
            return false;
        }

        Connection conn = this.connection;
        PreparedStatement psUpdateNotes = null;
        PreparedStatement psDeleteCategory = null;
        boolean success = false;

        try {
            conn.setAutoCommit(false); // Start transaction

            // Update notes to use Uncategorized category
            String updateNotesSql = "UPDATE notes SET category_id = ? WHERE category_id = ?";
            psUpdateNotes = conn.prepareStatement(updateNotesSql);
            psUpdateNotes.setInt(1, uncategorizedId);
            psUpdateNotes.setInt(2, categoryId);
            psUpdateNotes.executeUpdate();

            // Delete the category
            String deleteCategorySql = "DELETE FROM note_categories WHERE category_id = ? AND user_id = ?";
            psDeleteCategory = conn.prepareStatement(deleteCategorySql);
            psDeleteCategory.setInt(1, categoryId);
            psDeleteCategory.setInt(2, userId);
            int affectedRows = psDeleteCategory.executeUpdate();

            if (affectedRows > 0) {
                conn.commit();
                success = true;
                System.out.println("Deleted category ID " + categoryId + " and moved notes to Uncategorized");
            } else {
                conn.rollback();
                System.err.println("No category found with ID: " + categoryId + " for user ID: " + userId);
            }
        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            System.err.println("Error deleting category: " + e.getMessage());
        } finally {
            try {
                if (psUpdateNotes != null)
                    psUpdateNotes.close();
            } catch (SQLException se) {
                /* ignore */ }
            try {
                if (psDeleteCategory != null)
                    psDeleteCategory.close();
            } catch (SQLException se) {
                /* ignore */ }
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (SQLException se) {
                /* ignore */ }
        }

        return success;
    }

    /**
     * Gets the count of notes in each category for a user.
     */
    public Map<Integer, Integer> getCategoryNoteCounts(int userId) {
        Map<Integer, Integer> counts = new HashMap<>();
        String sql = "SELECT category_id, COUNT(*) as count FROM notes WHERE user_id = ? GROUP BY category_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                int count = rs.getInt("count");
                counts.put(categoryId, count);
            }
        } catch (SQLException e) {
            System.err.println("Error getting category note counts: " + e.getMessage());
        }

        return counts;
    }

    // --- Notes & Note_Todos Management (Updated for Categories) ---

    /**
     * Saves or updates a note and its associated todo items.
     * Uses a transaction to ensure atomicity.
     * Updated to support categories.
     */
    public boolean saveNoteAndTodos(int userId, String title, NoteData data) {
        if (title == null || title.trim().isEmpty()) {
            System.err.println("Cannot save note with empty title");
            return false;
        }

        // Get or create the category
        int categoryId = getCategoryIdByName(userId, data.category);
        if (categoryId == -1) {
            System.err.println("Failed to get/create category: " + data.category);
            return false;
        }

        String upsertNoteSQL = "INSERT INTO notes (user_id, category_id, title, content) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE content = VALUES(content), category_id = VALUES(category_id), updated_at = CURRENT_TIMESTAMP";
        String selectNoteIdSQL = "SELECT note_id FROM notes WHERE user_id = ? AND title = ?";
        String deleteTodosSQL = "DELETE FROM note_todos WHERE note_id = ?";
        String insertTodoSQL = "INSERT INTO note_todos (note_id, description, completed) VALUES (?, ?, ?)";

        Connection conn = this.connection; // Use the managed connection
        PreparedStatement psUpsertNote = null;
        PreparedStatement psSelectNoteId = null;
        PreparedStatement psDeleteTodos = null;
        PreparedStatement psInsertTodo = null;
        ResultSet generatedKeys = null;
        boolean success = false;

        try {
            conn.setAutoCommit(false); // Start transaction

            psUpsertNote = conn.prepareStatement(upsertNoteSQL, Statement.RETURN_GENERATED_KEYS);
            psSelectNoteId = conn.prepareStatement(selectNoteIdSQL);
            psDeleteTodos = conn.prepareStatement(deleteTodosSQL);
            psInsertTodo = conn.prepareStatement(insertTodoSQL);

            // Upsert Note
            psUpsertNote.setInt(1, userId);
            psUpsertNote.setInt(2, categoryId);
            psUpsertNote.setString(3, title);
            psUpsertNote.setString(4, data.content); // Accessing public field
            int affectedRows = psUpsertNote.executeUpdate();

            int noteId = -1;
            if (affectedRows == 1) { // Row was inserted
                generatedKeys = psUpsertNote.getGeneratedKeys();
                if (generatedKeys != null && generatedKeys.next()) {
                    noteId = generatedKeys.getInt(1);
                    System.out.println("Inserted new note with ID: " + noteId);
                }
            } else { // Row was updated or no change, fetch existing ID
                psSelectNoteId.setInt(1, userId);
                psSelectNoteId.setString(2, title);
                ResultSet rs = psSelectNoteId.executeQuery();
                if (rs.next()) {
                    noteId = rs.getInt("note_id");
                    System.out.println("Found existing note with ID: " + noteId);
                }
                if (rs != null)
                    rs.close();
            }

            if (noteId == -1) {
                throw new SQLException("Failed to get note_id for title: " + title);
            }

            // Delete existing todos for this note
            psDeleteTodos.setInt(1, noteId);
            int deletedRows = psDeleteTodos.executeUpdate();
            System.out.println("Deleted " + deletedRows + " existing todo items for note ID: " + noteId);

            // Insert current todos for this note
            int todoCount = 0;
            for (TodoItem todo : data.todos) { // Accessing public field
                if (!todo.text.trim().isEmpty()) { // Accessing public field
                    psInsertTodo.setInt(1, noteId);
                    psInsertTodo.setString(2, todo.text); // Accessing public field
                    psInsertTodo.setInt(3, todo.completed ? 1 : 0); // Accessing public field
                    psInsertTodo.addBatch();
                    todoCount++;
                }
            }

            if (todoCount > 0) {
                int[] batchResults = psInsertTodo.executeBatch();
                System.out.println("Inserted " + batchResults.length + " todo items for note ID: " + noteId);
            }

            conn.commit(); // Commit transaction
            success = true;
            System.out.println("Note and todos saved successfully for title: " + title);

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error");
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
            System.err.println("Error saving note and todos: " + e.getMessage());
        } finally {
            // Clean up resources
            try {
                if (generatedKeys != null)
                    generatedKeys.close();
            } catch (SQLException se) {
                /* ignore */ }
            try {
                if (psUpsertNote != null)
                    psUpsertNote.close();
            } catch (SQLException se) {
                /* ignore */ }
            try {
                if (psSelectNoteId != null)
                    psSelectNoteId.close();
            } catch (SQLException se) {
                /* ignore */ }
            try {
                if (psDeleteTodos != null)
                    psDeleteTodos.close();
            } catch (SQLException se) {
                /* ignore */ }
            try {
                if (psInsertTodo != null)
                    psInsertTodo.close();
            } catch (SQLException se) {
                /* ignore */ }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Restore auto-commit
                    System.out.println("Auto-commit restored");
                }
            } catch (SQLException se) {
                /* ignore */ }
        }
        return success;
    }

    public boolean deleteNote(int userId, String title) {
        if (title == null || title.trim().isEmpty()) {
            System.err.println("Cannot delete note with empty title");
            return false;
        }

        String deleteNoteSQL = "DELETE FROM notes WHERE user_id = ? AND title = ?";
        try (PreparedStatement psDeleteNote = connection.prepareStatement(deleteNoteSQL)) {
            psDeleteNote.setInt(1, userId);
            psDeleteNote.setString(2, title);
            int affectedRows = psDeleteNote.executeUpdate();
            boolean success = affectedRows > 0;
            if (success) {
                System.out.println("Successfully deleted note with title: " + title + " for user ID: " + userId);
            } else {
                System.err.println("No note found to delete with title: " + title + " for user ID: " + userId);
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error deleting note: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads all notes for the current user from the database.
     * Updated to include category information.
     */
    public Map<String, NoteData> loadNotes(int userId) {
        Map<String, NoteData> notesMap = new HashMap<>();
        String selectNotesSQL = "SELECT n.note_id, n.title, n.content, c.name as category_name " +
                "FROM notes n " +
                "LEFT JOIN note_categories c ON n.category_id = c.category_id " +
                "WHERE n.user_id = ? " +
                "ORDER BY n.title ASC";
        String selectTodosSQL = "SELECT description, completed FROM note_todos WHERE note_id = ?";

        try (PreparedStatement psNotes = connection.prepareStatement(selectNotesSQL)) {
            psNotes.setInt(1, userId);
            ResultSet rsNotes = psNotes.executeQuery();

            while (rsNotes.next()) {
                int noteId = rsNotes.getInt("note_id");
                String title = rsNotes.getString("title");
                String content = rsNotes.getString("content");
                String category = rsNotes.getString("category_name");

                // If category is null, use "Uncategorized"
                if (category == null) {
                    category = "Uncategorized";
                }

                NoteData data = new NoteData();
                data.content = content; // Accessing public field
                data.category = category; // Accessing public field
                data.todos = new ArrayList<>(); // Accessing public field

                // Load todos for this note
                try (PreparedStatement psTodos = connection.prepareStatement(selectTodosSQL)) {
                    psTodos.setInt(1, noteId);
                    ResultSet rsTodos = psTodos.executeQuery();
                    while (rsTodos.next()) {
                        String text = rsTodos.getString("description");
                        boolean completed = rsTodos.getInt("completed") == 1;
                        data.todos.add(new TodoItem(text, completed)); // Accessing public field
                    }
                } // psTodos and rsTodos closed automatically

                notesMap.put(title, data);
            }
            if (rsNotes != null)
                rsNotes.close();
            System.out.println("Loaded " + notesMap.size() + " notes for user ID: " + userId);
        } catch (SQLException e) {
            System.err.println("Error loading notes: " + e.getMessage());
            // Return empty map on error
        }
        return notesMap;
    }

    /**
     * Loads notes for a specific category.
     */
    public Map<String, NoteData> loadNotesByCategory(int userId, String categoryName) {
        Map<String, NoteData> notesMap = new HashMap<>();
        String selectNotesSQL = "SELECT n.note_id, n.title, n.content, c.name as category_name " +
                "FROM notes n " +
                "LEFT JOIN note_categories c ON n.category_id = c.category_id " +
                "WHERE n.user_id = ? AND c.name = ? " +
                "ORDER BY n.title ASC";
        String selectTodosSQL = "SELECT description, completed FROM note_todos WHERE note_id = ?";

        try (PreparedStatement psNotes = connection.prepareStatement(selectNotesSQL)) {
            psNotes.setInt(1, userId);
            psNotes.setString(2, categoryName);
            ResultSet rsNotes = psNotes.executeQuery();

            while (rsNotes.next()) {
                int noteId = rsNotes.getInt("note_id");
                String title = rsNotes.getString("title");
                String content = rsNotes.getString("content");
                String category = rsNotes.getString("category_name");

                // If category is null, use "Uncategorized"
                if (category == null) {
                    category = "Uncategorized";
                }

                NoteData data = new NoteData();
                data.content = content; // Accessing public field
                data.category = category; // Accessing public field
                data.todos = new ArrayList<>(); // Accessing public field

                // Load todos for this note
                try (PreparedStatement psTodos = connection.prepareStatement(selectTodosSQL)) {
                    psTodos.setInt(1, noteId);
                    ResultSet rsTodos = psTodos.executeQuery();
                    while (rsTodos.next()) {
                        String text = rsTodos.getString("description");
                        boolean completed = rsTodos.getInt("completed") == 1;
                        data.todos.add(new TodoItem(text, completed)); // Accessing public field
                    }
                } // psTodos and rsTodos closed automatically

                notesMap.put(title, data);
            }
            if (rsNotes != null)
                rsNotes.close();
            System.out.println(
                    "Loaded " + notesMap.size() + " notes for category '" + categoryName + "' and user ID: " + userId);
        } catch (SQLException e) {
            System.err.println("Error loading notes by category: " + e.getMessage());
            // Return empty map on error
        }
        return notesMap;
    }

    // --- Dashboard Data Methods (New) ---

    /**
     * Gets task counts (total, completed, pending) for a user.
     */
    public Map<String, Integer> getTaskCounts(int userId) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("total", 0);
        counts.put("completed", 0);
        counts.put("pending", 0);

        String countSql;
        if (userId == 0) {
            // Admin: hitung semua task
            countSql = "SELECT COUNT(*) AS total, SUM(CASE WHEN completed = 1 THEN 1 ELSE 0 END) AS completed, SUM(CASE WHEN completed = 0 THEN 1 ELSE 0 END) AS pending FROM note_todos nt JOIN notes n ON nt.note_id = n.note_id";
        } else {
            // User biasa: hanya task milik sendiri
            countSql = "SELECT COUNT(*) AS total, SUM(CASE WHEN completed = 1 THEN 1 ELSE 0 END) AS completed, SUM(CASE WHEN completed = 0 THEN 1 ELSE 0 END) AS pending FROM note_todos nt JOIN notes n ON nt.note_id = n.note_id WHERE n.user_id = ?";
        }

        try (PreparedStatement psCount = connection.prepareStatement(countSql)) {
            if (userId != 0) {
                psCount.setInt(1, userId);
            }
            ResultSet rsCount = psCount.executeQuery();
            if (rsCount.next()) {
                int total = rsCount.getInt("total");
                int completed = rsCount.getInt("completed");
                int pending = rsCount.getInt("pending");

                counts.put("total", total);
                counts.put("completed", completed);
                counts.put("pending", pending);
            }
        } catch (SQLException e) {
            System.err.println("Error getting task counts: " + e.getMessage());
            // Return zero counts on error
        }
        return counts;
    }

    /**
     * Gets a list of pending task titles and priorities for the dashboard
     * notification panel.
     */
    public List<String> getPendingTaskNotifications(int userId, int limit) {
        List<String> notifications = new ArrayList<>();

        String pendingSql;
        if (userId == 0) {
            // Admin: semua pending task
            pendingSql = "SELECT nt.description FROM note_todos nt JOIN notes n ON nt.note_id = n.note_id WHERE nt.completed = 0 ORDER BY nt.todo_id ASC LIMIT ?";
        } else {
            // User biasa: hanya pending task milik sendiri
            pendingSql = "SELECT nt.description FROM note_todos nt JOIN notes n ON nt.note_id = n.note_id WHERE n.user_id = ? AND nt.completed = 0 ORDER BY nt.todo_id ASC LIMIT ?";
        }

        try (PreparedStatement psPending = connection.prepareStatement(pendingSql)) {
            if (userId == 0) {
                psPending.setInt(1, limit);
            } else {
                psPending.setInt(1, userId);
                psPending.setInt(2, limit);
            }

            ResultSet rsPending = psPending.executeQuery();
            while (rsPending.next()) {
                String description = rsPending.getString("description");
                notifications.add("- " + description);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending task notifications: " + e.getMessage());
        }
        return notifications;
    }

    /**
     * Creates a new note for a user in a specific category. Only title is required, content and todos can be empty.
     * Returns true if note created successfully, false otherwise.
     */
    public boolean createNote(int userId, String noteTitle, int categoryId) {
        if (noteTitle == null || noteTitle.trim().isEmpty()) {
            System.err.println("Cannot create note with empty title");
            return false;
        }
        String sql = "INSERT INTO notes (user_id, category_id, title, content) VALUES (?, ?, ?, '')";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, categoryId);
            pstmt.setString(3, noteTitle.trim());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Note '" + noteTitle + "' created for user " + userId + " in category " + categoryId);
                return true;
            } else {
                System.err.println("Failed to insert note: " + noteTitle);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error creating note: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get is_admin status for a user
     */
    public boolean isUserAdmin(int userId) {
        String sql = "SELECT is_admin FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("is_admin") == 1;
            }
        } catch (SQLException e) {
            System.err.println("Error checking is_admin: " + e.getMessage());
        }
        return false;
    }

    /**
     * Contoh: Load all notes, jika admin load semua user, jika bukan admin hanya user sendiri
     */
    public Map<String, NoteData> loadNotesWithAdmin(int userId) {
        boolean admin = isUserAdmin(userId);
        Map<String, NoteData> notesMap = new HashMap<>();
        String selectNotesSQL;
        if (admin) {
        System.out.println("this User is adminssss: " );

            selectNotesSQL = "SELECT n.note_id, n.title, n.content, c.name as category_name FROM notes n LEFT JOIN note_categories c ON n.category_id = c.category_id ORDER BY n.title ASC";
        } else {
            selectNotesSQL = "SELECT n.note_id, n.title, n.content, c.name as category_name FROM notes n LEFT JOIN note_categories c ON n.category_id = c.category_id WHERE n.user_id = ? ORDER BY n.title ASC";
        }
        String selectTodosSQL = "SELECT description, completed FROM note_todos WHERE note_id = ?";
        try (PreparedStatement psNotes = connection.prepareStatement(selectNotesSQL)) {
            if (!admin) {
                psNotes.setInt(1, userId);
            }
            ResultSet rsNotes = psNotes.executeQuery();
            while (rsNotes.next()) {
                int noteId = rsNotes.getInt("note_id");
                String title = rsNotes.getString("title");
                String content = rsNotes.getString("content");
                String category = rsNotes.getString("category_name");
                if (category == null) category = "Uncategorized";
                NoteData data = new NoteData();
                data.content = content;
                data.category = category;
                data.todos = new ArrayList<>();
                try (PreparedStatement psTodos = connection.prepareStatement(selectTodosSQL)) {
                    psTodos.setInt(1, noteId);
                    ResultSet rsTodos = psTodos.executeQuery();
                    while (rsTodos.next()) {
                        String text = rsTodos.getString("description");
                        boolean completed = rsTodos.getInt("completed") == 1;
                        data.todos.add(new TodoItem(text, completed));
                    }
                }
                notesMap.put(title, data);
            }
        } catch (SQLException e) {
            System.err.println("Error loading notes (admin-aware): " + e.getMessage());
        }
        return notesMap;
    }
}
