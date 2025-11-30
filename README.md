#  MyQuill

**Offline journaling Android application built using Kotlin, XML, and Room.**

MyQuill allows users to write personal diary entries with media attachments, GPS-based location tags, and friend companions — all stored locally without requiring internet access.

Developed by:

* George Kassar
* Ethan McLeod
* Abinav Binukumar

Repository:
[https://github.com/abinav-binukumarr/MyQuill](https://github.com/abinav-binukumarr/MyQuill)

---

## Features

* Local **User Authentication** (Sign up & Login)
* **Diary Entries CRUD**
* **Photo & Video Attachments** using persisted URIs
* **Location Tagging**

  * Auto GPS location
  * Manual map selection
* **Audio Feedback** for common actions
* Fully **Offline Friend System**

  * Search users
  * Friend requests
  * Accept/reject friends
  * Companion tagging in entries
* **User Profiles** with photo, age, gender & bio
* RecyclerView-based **Home Feed**

---

## Tech Stack

* **Language:** Kotlin
* **UI:** XML + Activities
* **Database:** Room (SQLite local storage)
* **Media Access:** Android Storage Access Framework
* **Location:** Fused Location Provider + Geocoder + Google Maps
* **Audio:** MediaPlayer API

---

## Build & Run

1. Clone the repository:

   ```
   git clone https://github.com/abinav-binukumarr/MyQuill.git
   ```
2. Open project in **Android Studio**
3. Sync Gradle
4. Run on an emulator or physical Android device

---

## Permissions

MyQuill requests the following permissions during runtime:

* **Location** — for GPS tagging
* **Storage access** — for selecting photos/videos
* **Media access** — persistent URI permissions via SAF
* **Internet (Maps only)** — for Google Maps rendering (all other features remain offline)

---

## Data Storage

All application data is stored locally using **Room**:

* **Users** — authentication & profiles
* **Entries** — journal entries linked to users
* **Friend Requests** — manages social connections

No remote servers or cloud services are used. All features function offline after installation.

---

## Media Handling

Media (photos & videos) is accessed using the **Android Storage Access Framework** rather than copying files into app storage.

* Media files remain in their original locations
* Persistable URI permissions ensure access across app restarts
* URIs are stored in the database
* Media is loaded directly when displaying entries

This approach prevents crashes caused by lost permissions and keeps the app lightweight.

---

##  Location System

Users can attach a location to an entry using:

### • Current Location

Uses device GPS and Geocoder to generate a readable address.

### • Map Selection

Allows manual location selection on an interactive map if GPS results are unavailable or unreliable.

If a location cannot be resolved, the app gracefully displays:

```
"Location: Not Set"
```

---

##  Friend System

The app includes a simple offline social system built entirely using Room:

* Search by username or email
* Send friend requests
* Accept or reject requests
* Remove friends
* View profiles
* Attach companions to entries

This system mirrors a basic online friend workflow while operating completely offline.

---

##  Architecture

MyQuill uses a **simple Activity + DAO architecture**:

```
Activities → DAOs → Room Database
```

There is a clear separation between:

* UI Screens (Activities)
* Data Access (DAOs)
* Data Models (Entities)

Media and location functionality are handled independently through Android system APIs.

---

## Project Status

All major project requirements and features have been fully implemented.

The application is stable, functional, and suitable for offline journaling use with integrated media and location support.

---

## License

This project is for educational use only.

---

---

This version is:

* Simple
* Professional
* Code-focused
* Not bloated
* Not an essay
* Not a tutorial dump

This is the exact style **real repos and portfolios use.**

If you want, next we could also:

* Add a **CONTRIBUTING.md**
* Create a **docs folder** for detailed explanations
* Or format screenshots and usage sections for public presentation.
