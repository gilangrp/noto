# Noto: Aplikasi Catatan Digital, Manajemen Tugas, Pomodoro

Aplikasi ini merupakan implementasi antarmuka Login dan Register menggunakan Java Swing dengan tampilan modern, animasi transisi, validasi input real-time, dan integrasi database **MySQL**. Aplikasi juga memiliki fitur Todo List dan Pomodoro Timer yang saling terintegrasi.

> **PENTING:** Aplikasi ini menggunakan Java Swing GUI dan harus dijalankan pada sistem yang memiliki lingkungan desktop grafis (GUI). Aplikasi tidak dapat dijalankan pada server headless atau lingkungan tanpa GUI.

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
- Panel Pomodoro Timer untuk manajemen waktu
- Integrasi antara Todo List dan Pomodoro Timer
- Penyimpanan data tugas dan sesi Pomodoro ke database

### Database

- Menggunakan **MySQL** sebagai database utama
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
3. Arahkan ke folder **NotoApp** yang telah diekstrak
4. Klik tombol **Open Project**
5. NetBeans akan secara otomatis mendeteksi struktur proyek

## Cara Menjalankan Aplikasi

1. Pastikan **MySQL** sudah berjalan dan database sudah dibuat sesuai kebutuhan aplikasi
2. Konfigurasikan koneksi MySQL di file aplikasi (pastikan username, password, dan nama database benar)
3. Setelah proyek terbuka di NetBeans, klik kanan pada proyek **NotoApp** di panel Projects
4. Pilih **Run** atau tekan tombol F6
5. Aplikasi akan dikompilasi dan dijalankan secara otomatis

## Akun Demo

Aplikasi ini sudah dilengkapi dengan akun demo yang dapat digunakan untuk testing:

- **Username:** testuser
- **Email:** test@example.com
- **Password:** password123

## Struktur Proyek
NotoApp/
├── bin/
├── build/
├── dist/
├── lib/
├── nbproject/
├── src/
│ └── com/noto/
│ ├── auth/ # Login & Register
│ ├── database/ # Koneksi & operasi MySQL
│ ├── home/ # Dashboard utama
│ ├── pomodorotimer/ # Komponen Pomodoro (MVC)
│ ├── resources/ # Font, suara, dll
│ ├── todolist/ # Manajemen tugas
│ └── utility/ # Animasi, ikon, validasi
├── loginapp.db # Database lokal (jika pakai SQLite)
├── Main.java # Entry point aplikasi
├── build.sh # Skrip build
├── run.sh # Skrip run
├── build.xml # Ant build config
└── README.md # Dokumentasi proyek ini

## Persyaratan Sistem

- Java Development Kit (JDK) 8 atau lebih tinggi
- NetBeans IDE 8.2 atau lebih tinggi
- Sistem operasi yang mendukung Java Swing (Windows, macOS, Linux)
- MySQL Server & MySQL Connector/J (JDBC driver)


