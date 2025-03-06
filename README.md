# Step Counter App

# Overview
Step Counter is an Android application that utilizes the device's built-in step counter sensor to track the number of steps taken by the user. It calculates the distance traveled and features a built-in countdown timer to monitor workout sessions.

# Features
- Step counting using the device's built-in **Step Counter Sensor**.
- Displays total steps taken and estimated distance traveled.
- Adjustable step count goal with a progress bar.
- **Activity Recognition Permission** request for devices running Android 10+.
- Start/Pause functionality with a **countdown timer**.
- **Automatic reset** of step count when the timer starts.

# Upload Demo video 
https://github.com/user-attachments/assets/5147b7e4-f934-46b2-8950-26f20d1cda9b

# Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/step-counter-app.git
   ```
2. Open the project in **Android Studio**.
3. Connect an Android device or use an emulator.
4. Build and run the application.

# Permissions
The app requires the following permissions:
- **Activity Recognition** (for Android 10 and above) to access step counting sensors.

# Usage
1. Open the app and grant **Activity Recognition** permission if prompted.
2. Start walking, and the app will automatically count your steps.
3. Press the **Start Timer** button to begin a 1-minute workout session.
4. Steps and distance will be tracked during the session.
5. Press **Stop Timer** to pause tracking.

# Code Structure
- `MainActivity.java`: Handles sensor events, step counting, UI updates, and timer logic.
- `activity_main.xml`: Defines the user interface.

# Requirements
- **Android 6.0 (API 23) or higher**
- Device with a **Step Counter Sensor**


