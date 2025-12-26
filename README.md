# ⚡ ClipboardSync: Universal Clipboard Manager

ClipboardSync is a seamless cross-platform productivity tool that instantly synchronizes your clipboard between your Android phone and Windows laptop. Copy text on one device, and it appears on the other instantly using the power of the Cloud.

🚀 Live Demo & Download

💻[Open Web Dashboard (Windows Interface)](https://rpraneet32-ops.github.io/ClipboardSync/)

📲[📥 Download Android App (APK)](./VSync.apk)
Download and install this file directly on your Android phone to get started.

✨ Key Features
* ⚡ Real-Time Cloud Sync: Uses Google Firebase to push data instantly from Phone to PC.
* 💾 Smart History: Keeps a local backup of your clipboard history on Android (Room Database).
* 🖱️ Force Paste Mode: A manual override button to bypass Android 10+ background privacy restrictions.
* 🗑️ Two-Way Deletion: Deleting a clip from your phone automatically removes it from the Cloud and your PC screen.
* 🔒 Secure: Data is stored using your private Firebase credentials.

📸 Screenshots

| Android App | Windows Web Dashboard |
|![Screenshot_20251226_221409_ClipboardSync](https://github.com/user-attachments/assets/7cab426b-916b-448d-a4e1-0a5b2e3b8ab1)|
|<img width="1920" height="1080" alt="Screenshot (165)" src="https://github.com/user-attachments/assets/0e32a606-c434-4eb3-99d9-29a4789a30d1" />
|
| Clean Jetpack Compose UI | Live updating Web Interface |

🛠️ Tech Stack

📱 Android (Kotlin)
* UI: Jetpack Compose (Modern UI Toolkit)
* Database: Room (Local SQLite)
* Backend: Firebase Firestore (NoSQL Cloud DB)
* Architecture: MVVM (Model-View-ViewModel)
* Concurrency: Kotlin Coroutines & Flows

💻 Web (Dashboard)
* Hosting: GitHub Pages
* Core: HTML5, CSS3
* Logic: Vanilla JavaScript with Firebase Web SDK v9

⚙️ How to Build from Source

If you are a developer and want to modify the code, follow these steps:

1. Android Setup

1.  Clone this repository.
2.  Create a Firebase Project and download `google-services.json`.
3.  Place the json file in the `app/` folder.
4.  Open in Android Studio and run.

2. Web Setup

1.  Open `index.html`.
2.  Replace the `firebaseConfig` constant with your own Firebase Project keys.
3.  Push to a GitHub repository and enable GitHub Pages in settings.


📜 License
This project was created as a personal productivity tool. Feel free to fork it and add your own features!

Made with ❤️ by Praneet
