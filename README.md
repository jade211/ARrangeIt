# ARrangeIt: Augmented Reality (AR) Virtual Home Decor App

<div align="center">
  <img src="res/arrangeit_logo_white.png" alt="ARrangeIt Logo" width="300"/>
</div>

## Project Overview
**ARrangeIt** is an Augmented Reality (AR) mobile application that enables users to visualize furniture and decorative items within their own living spaces before making a purchase. Leveraging AR technology, the app overlays virtual 3D models of furniture into real-world environments using a smartphone camera, helping users make informed home decor decisions.

## Team Members

##### **Jade Hudson** (ID: 21706905) → jade.hudson5@mail.dcu.ie
##### **Sruthi Santhosh** (ID: 21377986) → sruthi.santhosh2@mail.dcu.ie
##### **Supervisor**: Dr. Hyowon Lee

## Key Features

1. **AR Room Visualization**
    - View 3D furniture and decor items overlaid in real-time through a smartphone camera.
    - Supports object positioning, movement and rotation for accurate placement in the room.

2. **Furniture Catalogue Integration**
    - Access a catalogue of 3D furniture models categorized by type (e.g., sofas, tables, decor).
    - Filter items by color, type, and price.
    - View detailed information: dimensions, pricing etc.

3. **Save Options**
    - Save room configurations for later viewing.
    - Maintain a shopping list for potential purchases.

4. **AR Measurement Tool**
    - Measure distance between two selected points using the smartphone camera.

## Tech Stack

| Category           | Technology                        |
|--------------------|------------------------------------|
| **Language**       | Java                              |
| **IDE**            | Android Studio                    |
| **AR Framework**   | ARCore SDK                        |
| **3D Rendering**   | Sceneform                         |
| **Backend**        | Firebase Authentication, Realtime Database, Cloud Storage |
| **UI**             | Android Fragments, XML Layouts    |
| **Data Format**    | GLB (for 3D Models)               |
| **Version Control**| Git, GitLab                       |
| **Testing Devices**| Pixel 6a, Samsung Galaxy A22   

## Learning Challenges

- Integrating AR functionality with accurate real-world scale.
- Creating responsive and intuitive 3D model interactions.
- Backend development to manage user sessions, configurations, and catalogue data.
- Designing a scalable and user-friendly interface.

---

### ARrangeIt Documentation
Documentation on the application can be found here:
- [Project Proposal](https://github.com/jade211/ARrangeIt/blob/main/docs/proposal/proposal.md)
- [Functional Specification](https://github.com/jade211/ARrangeIt/blob/main/docs/functional-spec/FS_Info.pdf)
- [User Manual](https://github.com/jade211/ARrangeIt/blob/main/docs/documentation/ARrangeIt_User_Manual.pdf)
- [Technical Specification](https://github.com/jade211/ARrangeIt/blob/main/docs/documentation/ARrangeIt_Technical_Specification.pdf)
- [Video Walk-through](https://drive.google.com/file/d/1Z7Z2yvAfXOvY6KjRts3hYgNeO1T90mlp/view)
- [Expo Poster](https://github.com/jade211/ARrangeIt/blob/main/docs/poster/ARrangeIt%20Expo%20poster.pdf)

---


## Installation

### Prerequisites

- Android Studio (latest)
- Java SDK 11+
- ARCore-supported Android device (e.g. Pixel 6a, Samsung A22)
- Firebase project setup

### Clone Repository

```bash
git clone git@github.com:jade211/ARrangeIt.git

```

Once the repository is cloned and opened in Android Studio, make sure that the following Packages are installed:

#### SDK Platform
- **Android 15.0 (“VanillaCream”)**
    - Android SDK Platform 35
    - Sources for Android 35
    - Google APIs Intel x86_64 Atom System Image
    - Google Play Intel x86_64 Atom System Image

- **Android 14.0 (“UpsideDownCake”)**
    - Android SDK Platform 34
    - Sources for Android 34

#### SDK Tools
- Android SDK Build-Tools 36
- Android Emulator
- Android SDK Platform-Tools

### Running the Application
- Sync Gradle and resolve dependencies using “Sync Project with Gradle files” button.
- Deploy APK to Android device using “Run app” button.

### Installation Steps via APK file (Download Method)
1. On your device, download the ARrangeIt APK from the repository [here](https://github.com/jade211/ARrangeIt/tree/main/res) by clicking the download icon on this file.
2. Go to the device's system settings and enable “Install unknown apps” in Settings > Apps & notifications > Special app access. Allow Chrome and File Manager to have access enabled.
3. Navigate to your file manager and locate the downloaded APK file. It should be listed under the name ARrangeIt.apk
4. Tap the APK file to begin the installation.
5. Follow the on-screen instructions and navigate to the downloaded ARrangeIt application after
