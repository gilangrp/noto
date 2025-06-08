-- MySQL dump for ToDoList application
-- This file contains the complete database schema and sample data
-- for the ToDoList application with Notes & Checklist feature

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS todolist_db;
USE todolist_db;

-- Drop tables if they exist to ensure clean installation
-- Note: Order matters due to foreign key constraints
DROP TABLE IF EXISTS note_todos;
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS pomodoro_sessions;
DROP TABLE IF EXISTS user_settings;
DROP TABLE IF EXISTS todo_items;
DROP TABLE IF EXISTS users;

-- Users table to store authentication information
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Increased length for potential hashing
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL DEFAULT NULL -- Allow NULL for last login
);

-- Todo items table
CREATE TABLE todo_items (
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
CREATE TABLE notes (
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
CREATE TABLE note_todos (
    todo_id INT PRIMARY KEY AUTO_INCREMENT,
    note_id INT NOT NULL,
    description TEXT NOT NULL,
    completed TINYINT(1) DEFAULT 0,
    FOREIGN KEY (note_id) REFERENCES notes(note_id) ON DELETE CASCADE
);

-- Pomodoro sessions table to track timer usage
CREATE TABLE pomodoro_sessions (
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
CREATE TABLE user_settings (
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

-- Insert sample data

-- Insert test user
INSERT INTO users (full_name, email, username, password)
VALUES ('Test User', 'test@example.com', 'testuser', 'password123');

-- Get the user_id for the test user
SET @test_user_id = LAST_INSERT_ID();

-- Insert sample todo items
INSERT INTO todo_items (user_id, title, description, priority, status)
VALUES 
(@test_user_id, 'Complete Java Project', 'Finish the login and register application with MySQL integration', 2, 'in_progress'),
(@test_user_id, 'Study for Exam', 'Review chapters 1-5 for upcoming test', 1, 'pending'),
(@test_user_id, 'Exercise', 'Go for a 30-minute run', 0, 'pending'),
(@test_user_id, 'Read Documentation', 'Review Java Swing documentation for UI improvements', 1, 'completed');

-- Insert default settings for the test user
INSERT INTO user_settings (user_id)
VALUES (@test_user_id);

-- Insert sample notes
INSERT INTO notes (user_id, title, content)
VALUES 
(@test_user_id, 'Welcome Note', 'Welcome to the Notes & Checklist feature! You can create and manage notes with checklist items here.'),
(@test_user_id, 'Project Ideas', 'List of project ideas to work on in the future:'),
(@test_user_id, 'Meeting Notes', 'Team meeting on June 5th:\n- Discussed project timeline\n- Assigned tasks to team members\n- Set next meeting for June 12th');

-- Get note_ids for the sample notes
SET @welcome_note_id = (SELECT note_id FROM notes WHERE title = 'Welcome Note' AND user_id = @test_user_id);
SET @project_ideas_id = (SELECT note_id FROM notes WHERE title = 'Project Ideas' AND user_id = @test_user_id);
SET @meeting_notes_id = (SELECT note_id FROM notes WHERE title = 'Meeting Notes' AND user_id = @test_user_id);

-- Insert sample note todos (checklist items) for the sample notes
INSERT INTO note_todos (note_id, description, completed)
VALUES 
(@welcome_note_id, 'Try adding a new note', 0),
(@welcome_note_id, 'Create a checklist item', 0),
(@welcome_note_id, 'Save your changes', 0),
(@project_ideas_id, 'Mobile app with React Native', 0),
(@project_ideas_id, 'Web dashboard with Spring Boot', 0),
(@project_ideas_id, 'IoT home automation system', 0),
(@meeting_notes_id, 'Send meeting minutes to team', 0),
(@meeting_notes_id, 'Prepare presentation for next meeting', 0),
(@meeting_notes_id, 'Follow up with design team', 1);

-- Insert a sample pomodoro session
INSERT INTO pomodoro_sessions (user_id, todo_id, duration_minutes, session_type, completed)
VALUES 
(@test_user_id, (SELECT todo_id FROM todo_items WHERE title = 'Complete Java Project' AND user_id = @test_user_id), 
25, 'work', 1);
