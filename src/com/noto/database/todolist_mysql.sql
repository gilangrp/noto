-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jun 11, 2025 at 05:11 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `todolist_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `notes`
--

CREATE TABLE `notes` (
  `note_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `category_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notes`
--

INSERT INTO `notes` (`note_id`, `user_id`, `title`, `content`, `created_at`, `updated_at`, `category_id`) VALUES
(10, 2, 'koko', 'test', '2025-06-10 08:58:19', '2025-06-10 08:58:19', 5),
(11, 2, 'sistem operasi p7', '', '2025-06-11 03:11:44', '2025-06-11 13:42:11', 4),
(15, 2, 'lo', '', '2025-06-11 04:13:42', '2025-06-11 13:53:15', 4);

-- --------------------------------------------------------

--
-- Table structure for table `note_categories`
--

CREATE TABLE `note_categories` (
  `category_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `color` varchar(20) DEFAULT '#FFFFFF',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `note_categories`
--

INSERT INTO `note_categories` (`category_id`, `user_id`, `name`, `color`, `created_at`) VALUES
(2, 1, 'Uncategorized', '#FFFFFF', '2025-06-08 08:31:19'),
(4, 2, 'tugas', '#ccff99', '2025-06-08 08:33:22'),
(5, 2, 'Uncategorized', '#FFFFFF', '2025-06-08 08:34:49'),
(6, 3, 'TEST', '#ff99cc', '2025-06-10 08:17:59');

-- --------------------------------------------------------

--
-- Table structure for table `note_todos`
--

CREATE TABLE `note_todos` (
  `todo_id` int(11) NOT NULL,
  `note_id` int(11) NOT NULL,
  `description` text NOT NULL,
  `completed` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `note_todos`
--

INSERT INTO `note_todos` (`todo_id`, `note_id`, `description`, `completed`) VALUES
(19, 11, 'test kl', 0),
(21, 15, 'nulis', 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL,
  `is_admin` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `full_name`, `email`, `username`, `password`, `created_at`, `last_login`, `is_admin`) VALUES
(1, 'Test User', 'test@example.com', 'testuser', 'password123', '2025-06-07 15:00:52', NULL, 0),
(2, 'septiawan', 'septi@gmail.com', 'septi', '1234', '2025-06-07 15:01:35', '2025-06-11 14:44:52', 0),
(3, 'gilang', 'gilang@gmail.com', 'gilang', '123456', '2025-06-10 08:07:09', '2025-06-10 08:09:38', 1);

-- --------------------------------------------------------

--
-- Table structure for table `user_settings`
--

CREATE TABLE `user_settings` (
  `setting_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `work_duration` int(11) DEFAULT 25,
  `short_break_duration` int(11) DEFAULT 5,
  `long_break_duration` int(11) DEFAULT 15,
  `pomodoros_until_long_break` int(11) DEFAULT 4,
  `auto_start_breaks` tinyint(1) DEFAULT 0,
  `auto_start_pomodoros` tinyint(1) DEFAULT 0,
  `notification_sound` varchar(50) DEFAULT 'bell',
  `dark_mode` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_settings`
--

INSERT INTO `user_settings` (`setting_id`, `user_id`, `work_duration`, `short_break_duration`, `long_break_duration`, `pomodoros_until_long_break`, `auto_start_breaks`, `auto_start_pomodoros`, `notification_sound`, `dark_mode`) VALUES
(1, 1, 25, 5, 15, 4, 0, 0, 'bell', 0),
(2, 2, 25, 5, 15, 4, 0, 0, 'bell', 0),
(3, 3, 25, 5, 15, 4, 0, 0, 'bell', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `notes`
--
ALTER TABLE `notes`
  ADD PRIMARY KEY (`note_id`),
  ADD UNIQUE KEY `unique_user_note_title` (`user_id`,`title`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `note_categories`
--
ALTER TABLE `note_categories`
  ADD PRIMARY KEY (`category_id`),
  ADD UNIQUE KEY `unique_user_category` (`user_id`,`name`);

--
-- Indexes for table `note_todos`
--
ALTER TABLE `note_todos`
  ADD PRIMARY KEY (`todo_id`),
  ADD KEY `note_id` (`note_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `user_settings`
--
ALTER TABLE `user_settings`
  ADD PRIMARY KEY (`setting_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `notes`
--
ALTER TABLE `notes`
  MODIFY `note_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `note_categories`
--
ALTER TABLE `note_categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `note_todos`
--
ALTER TABLE `note_todos`
  MODIFY `todo_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `user_settings`
--
ALTER TABLE `user_settings`
  MODIFY `setting_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `notes`
--
ALTER TABLE `notes`
  ADD CONSTRAINT `notes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `notes_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `note_categories` (`category_id`) ON DELETE SET NULL;

--
-- Constraints for table `note_categories`
--
ALTER TABLE `note_categories`
  ADD CONSTRAINT `note_categories_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `note_todos`
--
ALTER TABLE `note_todos`
  ADD CONSTRAINT `note_todos_ibfk_1` FOREIGN KEY (`note_id`) REFERENCES `notes` (`note_id`) ON DELETE CASCADE;

--
-- Constraints for table `user_settings`
--
ALTER TABLE `user_settings`
  ADD CONSTRAINT `user_settings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
