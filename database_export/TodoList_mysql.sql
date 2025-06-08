-- TodoList_mysql.sql
-- Database schema for TodoList application using MySQL

-- Users table to store authentication information
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Increased length for potential hashing
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL DEFAULT NULL -- Allow NULL for last login
);

-- Todo items table
CREATE TABLE IF NOT EXISTS todo_items (
    todo_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'pending', -- pending, in_progress, completed
    priority INT DEFAULT 0, -- 0: low, 1: medium, 2: high
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NULL DEFAULT NULL,
    completed_at TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Notes table for storing user notes
CREATE TABLE IF NOT EXISTS notes (
    note_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_note_title (user_id, title)
);

-- Note todos table for storing checklist items within notes
CREATE TABLE IF NOT EXISTS note_todos (
    todo_id INT PRIMARY KEY AUTO_INCREMENT,
    note_id INT NOT NULL,
    description TEXT NOT NULL,
    completed TINYINT(1) DEFAULT 0,
    FOREIGN KEY (note_id) REFERENCES notes(note_id) ON DELETE CASCADE
);

-- Pomodoro sessions table to track timer usage (Potentially unused)
CREATE TABLE IF NOT EXISTS pomodoro_sessions (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    todo_id INT,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL DEFAULT NULL,
    duration_minutes INT NOT NULL,
    session_type VARCHAR(20) NOT NULL, -- work, short_break, long_break
    completed TINYINT(1) DEFAULT 0, -- Use TINYINT(1) for boolean
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (todo_id) REFERENCES todo_items(todo_id) ON DELETE SET NULL
);

-- User settings table for application preferences
CREATE TABLE IF NOT EXISTS user_settings (
    setting_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    work_duration INT DEFAULT 25,
    short_break_duration INT DEFAULT 5,
    long_break_duration INT DEFAULT 15,
    pomodoros_until_long_break INT DEFAULT 4,
    auto_start_breaks TINYINT(1) DEFAULT 0,
    auto_start_pomodoros TINYINT(1) DEFAULT 0,
    notification_sound VARCHAR(50) DEFAULT 'bell',
    dark_mode TINYINT(1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert a test user (Use INSERT IGNORE for safety)
INSERT IGNORE INTO users (full_name, email, username, password)
VALUES ('Test User', 'test@example.com', 'testuser', 'password123'); -- Remember to hash passwords in production!

-- Insert some sample todo items for the test user (Use INSERT IGNORE)
-- Note: This assumes the test user gets user_id = 1. This might not be reliable.
-- A better approach would be to get the user_id after insertion.
INSERT IGNORE INTO todo_items (user_id, title, description, priority, status)
SELECT user_id, 'Complete Java Project', 'Finish the login and register application with MySQL integration', 2, 'in_progress' FROM users WHERE username = 'testuser';

INSERT IGNORE INTO todo_items (user_id, title, description, priority, status)
SELECT user_id, 'Study for Exam', 'Review chapters 1-5 for upcoming test', 1, 'pending' FROM users WHERE username = 'testuser';

INSERT IGNORE INTO todo_items (user_id, title, description, priority, status)
SELECT user_id, 'Exercise', 'Go for a 30-minute run', 0, 'pending' FROM users WHERE username = 'testuser';

-- Insert default settings for the test user (Use INSERT IGNORE)
INSERT IGNORE INTO user_settings (user_id)
SELECT user_id FROM users WHERE username = 'testuser';

-- Insert sample notes for the test user
INSERT IGNORE INTO notes (user_id, title, content)
SELECT user_id, 'Welcome Note', 'Welcome to the Notes & Checklist feature! You can create and manage notes with checklist items here.' FROM users WHERE username = 'testuser';

INSERT IGNORE INTO notes (user_id, title, content)
SELECT user_id, 'Project Ideas', 'List of project ideas to work on in the future:' FROM users WHERE username = 'testuser';

-- Insert sample note todos (checklist items) for the sample notes
INSERT IGNORE INTO note_todos (note_id, description, completed)
SELECT note_id, 'Try adding a new note', 0 FROM notes WHERE title = 'Welcome Note' AND user_id = (SELECT user_id FROM users WHERE username = 'testuser');

INSERT IGNORE INTO note_todos (note_id, description, completed)
SELECT note_id, 'Create a checklist item', 0 FROM notes WHERE title = 'Welcome Note' AND user_id = (SELECT user_id FROM users WHERE username = 'testuser');

INSERT IGNORE INTO note_todos (note_id, description, completed)
SELECT note_id, 'Mobile app with React Native', 0 FROM notes WHERE title = 'Project Ideas' AND user_id = (SELECT user_id FROM users WHERE username = 'testuser');

INSERT IGNORE INTO note_todos (note_id, description, completed)
SELECT note_id, 'Web dashboard with Spring Boot', 0 FROM notes WHERE title = 'Project Ideas' AND user_id = (SELECT user_id FROM users WHERE username = 'testuser');

INSERT IGNORE INTO note_todos (note_id, description, completed)
SELECT note_id, 'IoT home automation system', 0 FROM notes WHERE title = 'Project Ideas' AND user_id = (SELECT user_id FROM users WHERE username = 'testuser');
