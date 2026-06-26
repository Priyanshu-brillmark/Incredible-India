# Incredible India Blogs

Incredible India Blogs is a premium, visually stunning travel and culture blogging platform designed to showcase the rich heritage, diverse cuisines, spectacular festivals, and hidden gems of India. 

The application is built using modern Android development practices, powered by **Kotlin**, **Jetpack Compose**, **Material Design 3 (Bento Grid layout patterns)**, and a local **Room database** for seamless offline capability.

---

## 🌟 Key Features

- **Bento Grid Design Theme**: A premium, highly structured visual theme using Material Design 3 guidelines, custom card ratios, and vibrant traditional accents (**Saffron Primary, Ashoka Navy, and India Green**).
- **Permanently Enforced Light Theme**: An eye-friendly, clean, high-contrast light theme implemented consistently across all screens, cards, and components.
- **Offline Reading & Bookmark Manager**: Download travel stories, images, and categories locally to read and manage them anywhere, even without an active internet connection.
- **Interactive Review & Rating System**: Submit 1-to-5 star ratings and detailed written feedback on travel blogs with real-time average calculation and review listings.
- **Scroll Progress Indicator**: A clean visual progress bar at the top of long-form articles to help readers track their reading journey.
- **Social Sharing Module**: Quick-action buttons to copy the blog link or share immediately to popular messaging and social platforms.
- **Weekly Newsletter Subscription**: A beautifully styled subscription block in the home feed footer with a simulated secure backend highlight delivery service.
- **Admin Dashboard**: A secure back-office suite to add, publish, edit, or delete travel blogs and manage categories dynamically.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Database (Persistence)**: Room SQLite DB
- **Asynchronous Flow**: Kotlin Coroutines & StateFlow
- **Image Loading**: Coil (with caching & crossfade)
- **Navigation**: Jetpack Navigation Compose (Type-safe)

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio Koala (or newer)**
- **JDK 17** or higher
- **Android SDK Platform 34** (Upside Down Cake)

### Installation Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/aistudio/incredible-india-blogs.git
   cd incredible-india-blogs
   ```

2. **Open the Project**:
   - Open Android Studio and select **Open an Existing Project**.
   - Navigate to the cloned folder and click **OK**.
   - Let Gradle sync and download dependencies.

3. **Running the Application**:
   - Connect an Android Device (with USB Debugging enabled) or start an Emulator.
   - Click the **Run** button (green play icon) in the Android Studio toolbar, or execute:
     ```bash
     gradle assembleDebug
     ```

---

## ⚙️ Configuration Details

### API Keys and Secrets

The application supports optional cloud-hosted or AI integrations. Any required API keys are configured securely via the **Secrets Gradle Plugin** and local `.env` variables:

1. Create a `.env` file in the project root:
   ```env
   GEMINI_API_KEY=your_key_here
   ```
2. The key will be securely generated inside `BuildConfig` during compilation:
   ```kotlin
   val apiKey = BuildConfig.GEMINI_API_KEY
   ```

---

## 📖 Basic Usage

### Exploring Stories
- Swipe through **Curated Categories** on the Home screen to filter destinations.
- Tap any **Bento Grid Card** to enter the immersive reader view.

### Offline Reading
- Tap the **Download Icon** at the top-right of any blog post to download it for offline access.
- Access your downloaded stories under **Profile** -> **Offline Saved Blogs**.

### Ratings & Feedback
- Scroll to the bottom of any blog post to rate it and leave your review. Your feedback will be instantly recorded and listed.

---

## 📄 License

This project is licensed under the Apache License 2.0. Feel free to use and extend it to promote travel and tourism.
