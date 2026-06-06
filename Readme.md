# FoundIt — Lost & Found Application

FoundIt adalah aplikasi Lost & Found yang terdiri dari:
- `BackEnd/`: Laravel API backend untuk data item, komentar, klaim, kategori, dan otentikasi.
- `FrontEnd/`: Android Kotlin client yang menggunakan Supabase Authentication dan memanggil API backend.

## Fitur Utama

- Pencatatan item hilang dan ditemukan
- Sistem komentar untuk item dengan dukungan balasan
- Klaim item menemukan pemilik yang benar
- Kategori item untuk pengelompokan
- Integrasi otentikasi Supabase di aplikasi Android

## Struktur Proyek

```text
TugasAkhirPAM/
├── BackEnd/           # Laravel API backend
│   ├── app/
│   │   ├── Models/    # Eloquent models
│   │   └── Http/Controllers/Api/
│   ├── routes/        # API routes dan web route dasar
│   ├── database/      # Migrasi, seeders, factory
│   ├── resources/     # Asset frontend Laravel jika diperlukan
│   ├── composer.json  # Dependency PHP dan script Laravel
│   └── package.json   # Dependency frontend build tools
├── FrontEnd/          # Android Kotlin app
│   ├── app/src/main/java/com/example/tugasakhirpam/
│   │   ├── adapter/
│   │   ├── data/
│   │   └── models/
│   ├── app/src/main/res/layout/ # Layout XML
│   ├── build.gradle.kts
│   └── gradle.properties
└── README.md          # Dokumentasi proyek
```

## Backend (Laravel) Setup

1. Masuk ke folder backend:
   ```powershell
   cd "BackEnd"
   ```
2. Install dependency PHP dan Node:
   ```powershell
   composer install
   npm install
   ```
3. Salin file lingkungan dan buat key aplikasi:
   ```powershell
   copy .env.example .env
   php artisan key:generate
   ```
4. Siapkan database dan jalankan migrasi:
   ```powershell
   php artisan migrate
   ```
5. Jalankan server Laravel:
   ```powershell
   php artisan serve
   ```

### Perintah Tambahan

- Jalankan build Vite:
  ```powershell
  npm run build
  ```
- Jalankan development environment:
  ```powershell
  npm run dev
  ```
- Jalankan test Laravel:
  ```powershell
  php artisan test
  ```

## API Endpoints Utama

Backend menyediakan API versioned di `routes/api.php` untuk komentar:

- `GET /api/v1/comments`
- `GET /api/v1/comments/{item_type}/{item_id}`
- `GET /api/v1/comment/{id}`
- `POST /api/v1/comment`
- `PUT /api/v1/comment/{id}`
- `DELETE /api/v1/comment/{id}`

> Catatan: `item_type` dapat digunakan untuk membedakan `lost` dan `found` item.

## Frontend (Android) Setup

1. Buka folder `FrontEnd` di Android Studio.
2. Sinkronkan proyek Gradle.
3. Jalankan aplikasi pada emulator atau perangkat fisik.

### Build dari command line

Dari folder `FrontEnd`:
```powershell
.
\gradlew assembleDebug
```

## Teknologi

- Backend: Laravel 12, PHP 8.2, Laravel Sanctum, Eloquent ORM
- Frontend: Android Kotlin, Gradle
- Autentikasi: Supabase

## Kontribusi

1. Buat branch baru dari `main`.
2. Tambahkan fitur atau perbaikan.
3. Uji perubahan di backend dan/atau frontend.
4. Ajukan pull request dengan deskripsi yang jelas.

## Lisensi

Lisensi mengikuti paket di `BackEnd/composer.json` dan pustaka terkait. Tambahkan file `LICENSE` jika diperlukan.
