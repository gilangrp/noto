# Aplikasi Login dan Register dengan Todo List dan Pomodoro Timer

Aplikasi ini merupakan implementasi antarmuka Login dan Register menggunakan Java Swing dengan tampilan modern, animasi transisi, validasi input real-time, dan integrasi database SQLite. Aplikasi ini juga dilengkapi dengan fitur Todo List dan Pomodoro Timer untuk meningkatkan produktivitas.

> **PENTING:** Aplikasi ini menggunakan Java Swing GUI dan harus dijalankan pada sistem yang memiliki lingkungan desktop grafis (GUI). Aplikasi tidak dapat dijalankan pada server headless atau lingkungan tanpa display X11.

## Fitur Utama

### Halaman Login
- Latar belakang gradasi warna biru ke ungu
- Panel transparan dengan bayangan lembut (soft shadow)
- Input field untuk username/email dengan ikon FontAwesome
- Input field untuk password dengan ikon FontAwesome
- Tombol Login dengan sudut melengkung dan efek hover
- Link navigasi ke halaman Register
- Animasi fade-in saat halaman dibuka
- Validasi input dan autentikasi dengan database

### Halaman Register
- Desain yang senada dengan halaman login
- Input field untuk Nama Lengkap, Email, Username, Password, dan Konfirmasi Password
- Ikon FontAwesome pada setiap input field
- Validasi input secara real-time dengan highlight warna
- Validasi username dan email yang sudah terdaftar
- Checkbox untuk menampilkan/menyembunyikan password
- Tombol Register dengan efek hover
- Link navigasi kembali ke halaman Login
- Animasi fade-in/fade-out saat transisi halaman

### Halaman Home
- Header dengan nama pengguna dan tombol logout
- Panel Todo List untuk mengelola daftar tugas
- Fitur tambah tugas baru dengan prioritas
- Fitur update status tugas (Menunggu, Dikerjakan, Selesai)
- Panel Pomodoro Timer untuk manajemen waktu
- Integrasi antara Todo List dan Pomodoro Timer
- Penyimpanan data tugas dan sesi Pomodoro ke database

### Database
- Menggunakan SQLite sebagai database lokal
- Tabel users untuk autentikasi dan informasi pengguna
- Tabel todo_items untuk menyimpan daftar tugas
- Tabel pomodoro_sessions untuk tracking penggunaan timer
- Tabel user_settings untuk preferensi aplikasi

### Efek Visual dan Animasi
- Animasi fade-in dan fade-out saat transisi antar halaman
- Efek hover pada tombol (perubahan ukuran dan warna)
- Animasi shake pada input yang tidak valid
- Highlight warna pada validasi real-time
- Efek bayangan pada panel utama

## Cara Membuka Proyek di NetBeans

1. Buka NetBeans IDE
2. Pilih menu **File > Open Project**
3. Arahkan ke folder **LoginRegisterApp** yang telah diekstrak
4. Klik tombol **Open Project**
5. NetBeans akan secara otomatis mendeteksi struktur proyek

## Cara Menjalankan Aplikasi

1. Setelah proyek terbuka di NetBeans, klik kanan pada proyek **LoginRegisterApp** di panel Projects
2. Pilih **Run** atau tekan tombol F6
3. Aplikasi akan dikompilasi dan dijalankan secara otomatis
4. Database SQLite akan dibuat secara otomatis saat pertama kali aplikasi dijalankan

## Akun Demo

Aplikasi ini sudah dilengkapi dengan akun demo yang dapat digunakan untuk testing:

- **Username:** testuser
- **Email:** test@example.com
- **Password:** password123

## Struktur Proyek

- `src/` - Direktori utama kode sumber
  - `Main.java` - Entry point aplikasi
  - `LoginFrame.java` - Implementasi halaman login
  - `RegisterFrame.java` - Implementasi halaman register
  - `HomeFrame.java` - Implementasi halaman utama dengan Todo List
  - `AnimationUtils.java` - Utilitas untuk animasi dan efek visual
  - `FontAwesomeUtils.java` - Utilitas untuk ikon FontAwesome
  - `TextPrompt.java` - Implementasi placeholder text pada input field
  - `database/` - Package untuk database
    - `DatabaseManager.java` - Pengelola koneksi dan operasi database
    - `TodoList.sql` - Skema database SQLite
  - `com/mycompany/pomodorotimer/` - Package untuk Pomodoro Timer
    - `controller/` - Controller untuk Pomodoro Timer
    - `model/` - Model untuk Pomodoro Timer
    - `view/` - View untuk Pomodoro Timer
  - `resources/` - Direktori resource
    - `fontawesome-webfont.ttf` - Font untuk ikon FontAwesome
    - `sounds/` - Suara untuk notifikasi Pomodoro Timer

## Persyaratan Sistem

- Java Development Kit (JDK) 8 atau lebih tinggi
- NetBeans IDE 8.2 atau lebih tinggi
- Sistem operasi yang mendukung Java Swing (Windows, macOS, Linux)
- SQLite JDBC Driver (sudah disertakan dalam proyek)

## Pengembangan Lebih Lanjut

Aplikasi ini dapat dikembangkan lebih lanjut dengan:
- Menambahkan enkripsi password untuk keamanan yang lebih baik
- Menambahkan fitur lupa password
- Menambahkan statistik penggunaan Pomodoro Timer
- Menambahkan fitur ekspor/impor data
- Menambahkan tema gelap/terang
- Menambahkan dukungan multi-bahasa
- Migrasi ke database server seperti MySQL atau PostgreSQL untuk penggunaan multi-user
