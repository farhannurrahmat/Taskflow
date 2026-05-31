# TaskFlow - Aplikasi Manajemen Proyek Tim

## Cara Menjalankan

### Prasyarat
- Java JDK 17 atau lebih baru
- Gradle 9.x (atau gunakan `./gradlew`)
- Koneksi internet (untuk mengunduh dependensi pertama kali)

### Langkah Menjalankan

**Windows:**
```
gradlew.bat run
```

**Linux / macOS:**
```bash
chmod +x gradlew
./gradlew run
```

### Akun Demo
| Username | Password | Role |
|----------|----------|------|
| manager | manager123 | Manager (akses penuh) |
| budi | budi123 | Anggota Tim |
| citra | citra123 | Anggota Tim |
| dani | dani123 | Anggota Tim |

## Struktur Proyek
```
app/src/main/java/com/taskflow/
├── Main.java                    # Entry point aplikasi
├── config/
│   └── DatabaseConfig.java      # Koneksi & inisialisasi SQLite
├── model/
│   ├── User.java                 # Superclass user (Encapsulation)
│   ├── Manager.java              # Subclass Manager (Inheritance)
│   ├── BaseTask.java             # Abstract class (Abstraction)
│   └── Task.java                 # Subclass Task + isOverdue() (Polymorphism)
├── dao/
│   ├── UserDAO.java              # Query autentikasi & data user
│   └── TaskDAO.java              # CRUD tugas + query statistik
├── service/
│   ├── AuthService.java          # Logika bisnis autentikasi
│   └── TaskService.java          # Logika bisnis tugas
├── controller/
│   ├── LoginController.java      # Controller halaman login
│   ├── DashboardController.java  # Controller dashboard & CRUD
│   └── StatisticsController.java # Controller grafik statistik
└── util/
    └── SceneManager.java         # Manajemen perpindahan scene
```

## Fitur
- Login dengan role-based access (Manager / Anggota)
- Dashboard tugas dengan TableView berwarna prioritas
- CRUD lengkap: Tambah, Edit, Hapus, Ubah Status
- Validasi deadline tidak boleh sebelum hari ini
- Filter & pencarian tugas real-time
- Dashboard statistik: KPI Cards, PieChart, StackedBarChart
- Auto-record tanggal selesai saat status = Done
- Deteksi tugas overdue otomatis
