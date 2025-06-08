-- TodoList.sql
-- Database schema for Login/Register and Todo List application with Pomodoro Timer integration

-- Users table to store authentication information
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Todo items table
CREATE TABLE IF NOT EXISTS todo_items (
    todo_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'pending', -- pending, in_progress, completed
    priority INTEGER DEFAULT 0, -- 0: low, 1: medium, 2: high
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Pomodoro sessions table to track timer usage
CREATE TABLE IF NOT EXISTS pomodoro_sessions (
    session_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    todo_id INTEGER,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    duration_minutes INTEGER NOT NULL,
    session_type VARCHAR(20) NOT NULL, -- work, short_break, long_break
    completed BOOLEAN DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (todo_id) REFERENCES todo_items(todo_id) ON DELETE SET NULL
);

-- User settings table for application preferences
CREATE TABLE IF NOT EXISTS user_settings (
    setting_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL UNIQUE,
    work_duration INTEGER DEFAULT 25,
    short_break_duration INTEGER DEFAULT 5,
    long_break_duration INTEGER DEFAULT 15,
    pomodoros_until_long_break INTEGER DEFAULT 4,
    auto_start_breaks BOOLEAN DEFAULT 0,
    auto_start_pomodoros BOOLEAN DEFAULT 0,
    notification_sound VARCHAR(50) DEFAULT 'bell',
    dark_mode BOOLEAN DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert a test user
INSERT OR IGNORE INTO users (full_name, email, username, password)
VALUES ('Test User', 'test@example.com', 'testuser', 'password123');

-- Insert some sample todo items for the test user
INSERT OR IGNORE INTO todo_items (user_id, title, description, priority, status)
VALUES 
    (1, 'Complete Java Project', 'Finish the login and register application with Pomodoro integration', 2, 'in_progress'),
    (1, 'Study for Exam', 'Review chapters 1-5 for upcoming test', 1, 'pending'),
    (1, 'Exercise', 'Go for a 30-minute run', 0, 'pending');

-- Insert default settings for the test user
INSERT OR IGNORE INTO user_settings (user_id)
VALUES (1);
