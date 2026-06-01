# TaskFlow: Aplikasi Manajemen Tugas Berbasis JavaFX dengan Pelacakan Prioritas Cerdas dan Kategorisasi Dinamis

TaskFlow adalah aplikasi manajemen tugas dan proyek tim berbasis JavaFX yang dirancang untuk membantu pengguna mengelola tugas akademik maupun operasional, memantau progres pekerjaan, serta melihat statistik penyelesaian tugas melalui antarmuka desktop yang minimalis dan interaktif.

---

## 🎯 Latar Belakang & Alasan Pemilihan Proyek

Proyek ini dipilih untuk memecahkan masalah tumpang tindihnya manajemen beban kerja antara urusan akademik dan operasional kolaborasi tim. TaskFlow dikembangkan sebagai ekosistem *personal OS* minimalis yang tidak sekadar mencatat tugas, melainkan secara aktif memprioritaskan pekerjaan penggunanya. 

Secara teknis, aplikasi ini menonjolkan implementasi *Object-Oriented Programming* (OOP) yang efisien, memadukan logika pelacakan tenggat waktu cerdas (*smart overdue tracking*) dengan manipulasi data dinamis pada tingkat *Data Access Object* (DAO). Pendekatan ini memungkinkan klasifikasi tugas secara fleksibel tanpa memerlukan skema *database* yang rumit, menjadikannya sebuah sistem produktivitas yang ringan, tepat sasaran, dan sangat relevan dengan kebutuhan mahasiswa masa kini.

---

## 🌿 Alur Kerja & Kolaborasi (Git Workflow)

Pengembangan proyek ini mengadopsi standar industri perangkat lunak dengan memisahkan riwayat kerja ke dalam dua *branch* utama:

- **`develop` (Pengembangan Aktif):** *Branch* ini adalah ruang kerja utama di mana tim kami berkolaborasi. Semua pengujian fitur baru, perombakan antarmuka (UI/UX), perbaikan *bug*, dan penyatuan kode dari masing-masing anggota tim dilakukan di sini.
- **`main` (Rilis Final):** *Branch* ini berisi hasil jadi (*production-ready*) aplikasi. Kode yang masuk ke *branch* ini telah melalui tahap pengujian di `develop` dan merupakan versi aplikasi yang paling stabil serta siap didemonstrasikan.

---

## 🚀 Fitur Utama

- **Autentikasi Aman** — Sistem registrasi dan login dengan validasi input pengguna.
- **Kategorisasi Multi-Konteks** — Mengklasifikasikan tugas dengan detail dan memisahkannya berdasarkan tipe penugasan (Personal atau Tim).
- **Pelacakan Prioritas Cerdas** — Deteksi otomatis (*automated overdue tracking*). Tugas dengan tenggat waktu hari ini atau yang sudah terlambat akan otomatis dievaluasi dan dimasukkan ke dalam antrean "Fokus Hari Ini" (Urgent).
- **Pemantauan Status Dinamis** — Lacak status pengerjaan secara berkala: *To Do*, *In Progress*, dan *Done*.
- **Statistik Visual Real-Time** — Memantau produktivitas melalui *Pie Chart* (distribusi status penyelesaian) dan *Bar Chart* (distribusi tugas per kategori) yang responsif dan proporsional.
- **Penyimpanan Lokal Terintegrasi** — Seluruh data diamankan di dalam database SQLite lokal.

---

## 🛠️ Cara Menjalankan

### Prasyarat
- Java JDK 21 atau lebih baru
- Gradle 9.x (atau gunakan eksekutor `./gradlew` bawaan)
- Koneksi internet (hanya untuk mengunduh dependensi pada saat pertama kali dijalankan)

### Langkah Menjalankan

**Windows:**
```bash
./gradlew run
Linux / macOS:

Bash
chmod +x gradlew
./gradlew run
📂 Struktur Proyek
Plaintext
app/src/main/java/com/taskflow/
│
├── Main.java                     # Entry point aplikasi
│
├── config/
│   └── DatabaseConfig.java       # Konfigurasi dan koneksi database SQLite
│
├── model/
│   ├── User.java                 # Encapsulation data pengguna
│   ├── BaseTask.java             # Kelas abstrak dasar untuk tugas
│   ├── PersonalTask.java         # Inheritance utama dari BaseTask
│   └── TeamTask.java             # Inheritance sekunder untuk operasional tim (Polymorphism)
│
├── dao/
│   ├── UserDAO.java              # Operasi database untuk data pengguna
│   └── PersonalTaskDAO.java      # CRUD dan eksekusi query data tugas
│
├── service/
│   └── AuthService.java          # Logika bisnis autentikasi pengguna
│
├── controller/
│   ├── LoginController.java      # Controller halaman login
│   ├── DashboardController.java  # Controller manajemen tugas utama
│   └── StatisticsController.java # Controller visualisasi metrik data
│
├── util/
│   └── SceneManager.java         # Utilitas manajemen perpindahan antar scene (UI)
│
└── view/
    ├── LoginView.java            # Layout halaman login
    ├── DashboardView.java        # Layout dashboard utama
    └── StatisticsView.java       # Layout analitik dan statistik
💻 Teknologi yang Digunakan
Bahasa Pemrograman: Java

Framework UI: JavaFX

Database: SQLite & JDBC

Build Tool: Gradle

🧠 Konsep OOP yang Diterapkan
Aplikasi ini dibangun menggunakan prinsip Object-Oriented Programming (OOP) yang solid untuk memastikan skalabilitas dan efisiensi memori:

Encapsulation: Data krusial pengguna dan tugas dilindungi dalam atribut private dan dikelola melalui metode getter dan setter yang ketat.

Abstraction: Penggunaan BaseTask.java sebagai cetak biru abstrak untuk mendefinisikan struktur fundamental dari sebuah entitas tugas.

Inheritance & Polymorphism: PersonalTask.java mewarisi BaseTask. Kehadiran TeamTask.java melengkapi arsitektur ini dengan menunjukkan Polimorfisme, di mana sistem dapat membedakan penanganan tugas individual dan tugas delegasi operasional secara otomatis pada level DAO dan UI (seperti penggabungan string kategori) tanpa perlu memodifikasi arsitektur database inti.

Separation of Concerns (MVC-based): Logika aplikasi dipecah menjadi layer Model, View, Controller, DAO, dan Service agar siklus aliran data mudah dilacak dan dimodifikasi.

Proyek ini dikembangkan sebagai bagian dari pembelajaran akademik dan implementasi praktik kolaborasi pengembangan perangkat lunak modern.