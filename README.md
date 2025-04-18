# 📰 News & User Profile App (Android)

An Android application built using modern development principles and architecture. It delivers a personalized news experience with Google Sign-In, 
offline caching, location access, and camera/gallery image upload. Developed with **clean architecture (MVVM)**, **Hilt** for dependency injection, and **Jetpack libraries**.

---

## 🔧 Tech Stack

- Kotlin, MVVM Architecture  
- Firebase Authentication (Google Sign-In)  
- Retrofit + OkHttp  
- Paging 3 + Room Database  
- WorkManager  
- ViewBinding  
- Glide  
- Fused Location Provider API  
- Shimmer + SwipeRefresh  
- Permissions + Camera/Gallery Integration  
- Dark Mode + SharedPreferences  

---

## 📱 Features

### ✅ Splash Screen  
- Plays `.mp4` video on launch.  
- Navigates based on login state.

### ✅ Google Sign-In  
- Firebase-based login.  
- Fetches user name, email, profile picture.

### ✅ Bottom Navigation  
- `HomeFragment`: Full-screen news, swipe up/down.  
- `SearchFragment`: Grid/List view with search.  
- `ProfileFragment`: Profile details, live location, image update.

### ✅ Profile Picture Update  
- Pick from Camera & Gallery.  
- Handles permissions for all Android versions.  
- Updates in Home & Profile Fragments.

### ✅ News API Integration  
- Fetches top headlines via [NewsAPI.org](https://newsapi.org/).  
- Uses Retrofit + Paging 3 for infinite scroll.

### ✅ Offline & Advanced  
- Caches news in Room DB.  
- Swipe to Refresh + Swipe Gestures.  
- Dark Mode support.  
- Background refresh via WorkManager (every 30 mins).  
- Shimmer loading UI.

---

## 📂 Project Structure

