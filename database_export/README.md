## Instruksi Penggunaan Database MySQL

### Mengimpor Database
1. Pastikan MySQL server sudah terinstal dan berjalan di komputer Anda
2. Buka terminal atau command prompt
3. Masuk ke MySQL sebagai user dengan hak akses yang cukup:
   ```
   mysql -u [username] -p
   ```
4. Impor database dengan perintah:
   ```
   source /path/to/todolist_db_dump.sql
   ```
   atau dari luar MySQL:
   ```
   mysql -u [username] -p < /path/to/todolist_db_dump.sql
   ```

### Konfigurasi Aplikasi
1. Buka file `src/database/DatabaseManager.java`
2. Ubah konfigurasi database sesuai dengan pengaturan MySQL Anda:
   ```java
   private static final String DB_HOST = "localhost";
   private static final String DB_PORT = "3306";
   private static final String DB_NAME = "todolist_db"; 
   private static final String DB_USER = "your_mysql_username"; 
   private static final String DB_PASSWORD = "your_mysql_password";
   ```

### Akun Demo
Username: testuser
Password: password123

### Struktur Database
Database terdiri dari tabel-tabel berikut:
- `users`: Menyimpan informasi pengguna
- `todo_items`: Menyimpan daftar tugas utama
- `notes`: Menyimpan catatan pengguna
- `note_todos`: Menyimpan item checklist dalam catatan
- `pomodoro_sessions`: Menyimpan sesi pomodoro
- `user_settings`: Menyimpan pengaturan pengguna

### Catatan Penting
- Database ini sudah berisi data sampel untuk pengujian
- Pastikan MySQL server Anda mendukung foreign key constraints
- Jika terjadi error saat impor, pastikan database `todolist_db` belum ada atau kosong
