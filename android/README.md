# Live Target Android

## Overview
Android implementation of the Live Target bullet impact detection system.

## Status
✅ **Completed Implementation** - Fully functional with Samsung Galaxy Watch integration

## Technology Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose  
- **Camera:** CameraX
- **Architecture:** MVVM with Repository pattern
- **Async:** Coroutines + Flow
- **Image Processing:** Android Bitmap API + OpenCV (optional)
- **Storage:** DataStore for preferences
- **Watch Integration:** Samsung Accessory SDK for Galaxy Watch
- **Testing:** JUnit + Espresso + Compose Testing

## Features

### Core Functionality
- [x] Camera live preview
- [x] Real-time bullet impact detection  
- [x] Zoom controls (hardware zoom)
- [x] Impact visualization (circles and numbers)
- [x] Photo saving to gallery
- [x] Configurable settings

### Settings & Customization
- [x] Impact circle color selection
- [x] Impact number color selection
- [x] Detection sensitivity adjustment
- [x] Bullet caliber configuration
- [x] Check interval customization

### User Interface
- [x] Modern Material Design 3 UI
- [x] Dark/Light theme support
- [x] Accessibility compliance
- [x] Responsive layout for different screen sizes
- [x] Intuitive controls and navigation

### Samsung Galaxy Watch Integration
- [x] Real-time impact notifications sent to watch
- [x] Watch status monitoring and connectivity
- [x] Samsung Accessory SDK integration
- [x] Impact display with zoomed target images
- [x] Settings toggle for watch integration
- [x] Feature parity with iOS Apple Watch functionality

## Project Structure

```
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bceassociates/livetarget/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screen/       # Main screens (Main, Settings, Help)
│   │   │   │   │   ├── component/    # Reusable UI components
│   │   │   │   │   └── theme/        # Material Design 3 theme
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/        # Data models (ChangePoint, etc.)
│   │   │   │   │   └── preferences/  # DataStore settings storage
│   │   │   │   ├── viewmodel/        # MVVM ViewModels
│   │   │   │   ├── watch/            # Samsung Galaxy Watch integration
│   │   │   │   └── detection/        # Image processing algorithms
│   │   │   └── res/
│   │   └── test/                     # Unit tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9+
- Gradle 8.0+

### Setup Instructions
1. Open `android/` folder in Android Studio
2. Sync Gradle dependencies
3. Run on device or emulator with camera support

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build  
./gradlew assembleRelease

# Run tests
./gradlew test

# Run UI tests
./gradlew connectedAndroidTest
```

## Completed Implementation

### ✅ Phase 1: Foundation (Completed)
- [x] Project setup and Gradle build configuration
- [x] CameraX integration with live preview
- [x] Jetpack Compose Material Design 3 UI
- [x] Basic image capture functionality
- [x] MVVM architecture with ViewModels

### ✅ Phase 2: Core Detection (Completed)  
- [x] Bullet impact detection algorithm implementation
- [x] Real-time image processing pipeline
- [x] Pixel-level difference detection
- [x] Impact point calculation and visualization
- [x] Configurable detection sensitivity

### ✅ Phase 3: Features & Polish (Completed)
- [x] Camera zoom controls (1x to 10x)
- [x] Settings screen with DataStore persistence
- [x] Photo saving to device gallery
- [x] Material Design 3 UI polish
- [x] Help screen with comprehensive guidance

### ✅ Phase 4: Samsung Galaxy Watch Integration (Completed)
- [x] Samsung Accessory SDK integration
- [x] Real-time impact notifications to watch
- [x] Watch status monitoring and connectivity
- [x] Impact display with zoomed target images
- [x] Settings integration for watch controls
- [x] Feature parity with iOS Apple Watch

### ✅ Phase 5: Testing & Optimization (Completed)
- [x] Unit tests for core algorithms
- [x] Comprehensive error handling
- [x] Performance optimization for real-time detection
- [x] Memory usage optimization
- [x] CI/CD pipeline integration

## Key Differences from iOS Version

### Advantages
- More camera control options on Android
- Easier file system access
- More flexible UI customization
- OpenCV integration typically easier

### Considerations
- More device fragmentation to handle
- Different camera APIs across manufacturers
- Permission handling more complex
- Samsung Galaxy Watch integration provides equivalent functionality to Apple Watch

## Contributing

### Development Guidelines
1. Follow Android coding standards and Kotlin style guide
2. Use Material Design 3 guidelines for UI components
3. Include unit tests for new features
4. Test on multiple device types and screen sizes
5. Test Samsung Galaxy Watch integration with physical devices
6. Maintain feature parity with iOS version

## Dependencies

### Core
- Jetpack Compose BOM 2025.01.01
- CameraX 1.3.1
- Kotlin Coroutines 1.7.3
- DataStore Preferences 1.0.0

### Watch Integration
- Samsung Accessory SDK 1.4.0
- Samsung Android SDK

### Image Processing
- Android Bitmap APIs
- Kotlin image processing utilities

### Testing
- JUnit 4.13.2
- Kotlin Test 1.9.22
- Compose Testing 2025.01.01
- AndroidX Test 1.5.0

### Tools
- Android Gradle Plugin 8.2.0
- Kotlin Gradle Plugin 1.9.22
- Gradle version catalogs

## Architecture

### Core Components

#### MainViewModel
- **State management** with Jetpack Compose StateFlow
- **Camera integration** with CameraX APIs
- **Detection algorithm** coordination and processing
- **Watch connectivity** management and status monitoring

#### ChangeDetector
- **Pixel-level image comparison** between camera frames
- **Configurable thresholds** for detection sensitivity
- **Background processing** for real-time performance
- **Memory-efficient** bitmap operations

#### Samsung Watch Integration
- **WatchConnectivityManager** abstract interface
- **SamsungWatchManager** for Galaxy Watch communication
- **Image compression** for efficient data transfer
- **Connection state monitoring** and error handling

### Data Flow
```
Camera → Image Capture → Detection Algorithm → Impact Points
   ↓            ↓              ↓                ↓
Preview → Background Thread → UI Update → Watch Transmission
```

## Performance Optimization

### Detection Performance
- **Throttled processing** based on configurable check interval
- **Efficient bitmap operations** with Android APIs
- **Background coroutines** for non-blocking processing
- **Memory management** for large image processing

### Watch Performance
- **Image compression** for Samsung Accessory SDK limits
- **Selective data transmission** (only new impacts)
- **Efficient serialization** with JSON messages

## Notes
This Android implementation shares the same core algorithms and design principles as the iOS version, documented in the `shared/` directory of this repository. The Samsung Galaxy Watch integration provides complete feature parity with the iOS Apple Watch functionality.