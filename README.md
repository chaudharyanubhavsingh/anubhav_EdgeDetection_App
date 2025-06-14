# Edge Detection App

## Overview
This Android application demonstrates real-time edge detection using OpenCV. The app allows users to capture images from their device's camera and apply various edge detection algorithms to identify and highlight edges in the images.

## Features
- Real-time camera preview with edge detection
- Multiple edge detection algorithms support
- User-friendly interface
- High-performance image processing using OpenCV
- Native C++ implementation for better performance

## Technical Details
- Built with Android Studio
- Uses OpenCV SDK for image processing
- Implements native C++ code for core image processing
- Supports Android API level 21 and above

## Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── cpp/           # Native C++ implementation
│   │   ├── java/          # Java/Kotlin source code
│   │   ├── jniLibs/       # Native libraries
│   │   └── res/           # Resources
│   └── AndroidManifest.xml
├── build.gradle           # App-level build configuration
└── proguard-rules.pro     # ProGuard rules
```

## Prerequisites
- Android Studio Arctic Fox or newer
- OpenCV SDK
- Android SDK with minimum API level 21
- NDK (Native Development Kit)
- CMake

## Setup Instructions
1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Ensure OpenCV SDK is properly linked
5. Build and run the application

## Building the Project
1. Open Android Studio
2. Click on "Build" -> "Make Project"
3. Wait for the build process to complete
4. Run the app on an emulator or physical device

## Usage
1. Launch the application
2. Grant camera permissions when prompted
3. Point the camera at the subject
4. The app will automatically detect and highlight edges in real-time

## Dependencies
- OpenCV Android SDK
- AndroidX libraries
- CMake for native build support

## Contributing
Feel free to submit issues and enhancement requests.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Author
Anubhav

## Acknowledgments
- OpenCV community for their excellent documentation and support
- Android development community for various resources and guidance 