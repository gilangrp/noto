-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 08 Jun 2025 pada 10.38
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `todolist_mysql`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `notes`
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
-- Dumping data untuk tabel `notes`
--

INSERT INTO `notes` (`note_id`, `user_id`, `title`, `content`, `created_at`, `updated_at`, `category_id`) VALUES
(1, 2, 'tugas', 'yang semangat yang semangat', '2025-06-07 15:02:05', '2025-06-08 08:36:17', 4);

-- --------------------------------------------------------

--
-- Struktur dari tabel `note_categories`
--

CREATE TABLE `note_categories` (
  `category_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `color` varchar(20) DEFAULT '#FFFFFF',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `note_categories`
--

INSERT INTO `note_categories` (`category_id`, `user_id`, `name`, `color`, `created_at`) VALUES
(2, 1, 'Uncategorized', '#FFFFFF', '2025-06-08 08:31:19'),
(4, 2, 'tugas', '#ccff99', '2025-06-08 08:33:22'),
(5, 2, 'Uncategorized', '#FFFFFF', '2025-06-08 08:34:49');

-- --------------------------------------------------------

--
-- Struktur dari tabel `note_todos`
--

CREATE TABLE `note_todos` (
  `todo_id` int(11) NOT NULL,
  `note_id` int(11) NOT NULL,
  `description` text NOT NULL,
  `completed` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `note_todos`
--

INSERT INTO `note_todos` (`todo_id`, `note_id`, `description`, `completed`) VALUES
(13, 1, 'pbo', 0),
(14, 1, 'pemweb', 0),
(15, 1, 'ppl', 0);

-- --------------------------------------------------------

--
-- Struktur dari tabel `pomodoro_sessions`
--

CREATE TABLE `pomodoro_sessions` (
  `session_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `todo_id` int(11) DEFAULT NULL,
  `start_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `end_time` timestamp NULL DEFAULT NULL,
  `duration_minutes` int(11) NOT NULL,
  `session_type` varchar(20) NOT NULL,
  `completed` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktur dari tabel `todo_items`
--

CREATE TABLE `todo_items` (
  `todo_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `status` varchar(20) DEFAULT 'pending',
  `priority` int(11) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `due_date` timestamp NULL DEFAULT NULL,
  `completed_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `todo_items`
--

INSERT INTO `todo_items` (`todo_id`, `user_id`, `title`, `description`, `status`, `priority`, `created_at`, `due_date`, `completed_at`) VALUES
(1, 1, 'Complete Java Project', 'Finish the login and register application with MySQL integration', 'in_progress', 2, '2025-06-07 15:00:52', NULL, NULL),
(2, 1, 'Study for Exam', 'Review chapters 1-5 for upcoming test', 'pending', 1, '2025-06-07 15:00:52', NULL, NULL),
(3, 1, 'Exercise', 'Go for a 30-minute run', 'pending', 0, '2025-06-07 15:00:52', NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`user_id`, `full_name`, `email`, `username`, `password`, `created_at`, `last_login`) VALUES
(1, 'Test User', 'test@example.com', 'testuser', 'password123', '2025-06-07 15:00:52', NULL),
(2, 'septiawan', 'septi@gmail.com', 'septi', 'septiii123', '2025-06-07 15:01:35', '2025-06-08 08:32:57');

-- --------------------------------------------------------

--
-- Struktur dari tabel `user_settings`
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
-- Dumping data untuk tabel `user_settings`
--

INSERT INTO `user_settings` (`setting_id`, `user_id`, `work_duration`, `short_break_duration`, `long_break_duration`, `pomodoros_until_long_break`, `auto_start_breaks`, `auto_start_pomodoros`, `notification_sound`, `dark_mode`) VALUES
(1, 1, 25, 5, 15, 4, 0, 0, 'bell', 0),
(2, 2, 25, 5, 15, 4, 0, 0, 'bell', 0);

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `notes`
--
ALTER TABLE `notes`
  ADD PRIMARY KEY (`note_id`),
  ADD UNIQUE KEY `unique_user_note_title` (`user_id`,`title`),
  ADD KEY `category_id` (`category_id`);

--
-- Indeks untuk tabel `note_categories`
--
ALTER TABLE `note_categories`
  ADD PRIMARY KEY (`category_id`),
  ADD UNIQUE KEY `unique_user_category` (`user_id`,`name`);

--
-- Indeks untuk tabel `note_todos`
--
ALTER TABLE `note_todos`
  ADD PRIMARY KEY (`todo_id`),
  ADD KEY `note_id` (`note_id`);

--
-- Indeks untuk tabel `pomodoro_sessions`
--
ALTER TABLE `pomodoro_sessions`
  ADD PRIMARY KEY (`session_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `todo_id` (`todo_id`);

--
-- Indeks untuk tabel `todo_items`
--
ALTER TABLE `todo_items`
  ADD PRIMARY KEY (`todo_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indeks untuk tabel `user_settings`
--
ALTER TABLE `user_settings`
  ADD PRIMARY KEY (`setting_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `notes`
--
ALTER TABLE `notes`
  MODIFY `note_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT untuk tabel `note_categories`
--
ALTER TABLE `note_categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `note_todos`
--
ALTER TABLE `note_todos`
  MODIFY `todo_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT untuk tabel `pomodoro_sessions`
--
ALTER TABLE `pomodoro_sessions`
  MODIFY `session_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT untuk tabel `todo_items`
--
ALTER TABLE `todo_items`
  MODIFY `todo_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT untuk tabel `user_settings`
--
ALTER TABLE `user_settings`
  MODIFY `setting_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `notes`
--
ALTER TABLE `notes`
  ADD CONSTRAINT `notes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `notes_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `note_categories` (`category_id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `note_categories`
--
ALTER TABLE `note_categories`
  ADD CONSTRAINT `note_categories_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `note_todos`
--
ALTER TABLE `note_todos`
  ADD CONSTRAINT `note_todos_ibfk_1` FOREIGN KEY (`note_id`) REFERENCES `notes` (`note_id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `pomodoro_sessions`
--
ALTER TABLE `pomodoro_sessions`
  ADD CONSTRAINT `pomodoro_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `pomodoro_sessions_ibfk_2` FOREIGN KEY (`todo_id`) REFERENCES `todo_items` (`todo_id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `todo_items`
--
ALTER TABLE `todo_items`
  ADD CONSTRAINT `todo_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Ketidakleluasaan untuk tabel `user_settings`
--
ALTER TABLE `user_settings`
  ADD CONSTRAINT `user_settings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
