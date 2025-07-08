# Live Target Android

## Overview
Android implementation of the Live Target bullet impact detection system.

## Status
🚧 **Planned Implementation** - Not yet started

## Planned Technology Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose  
- **Camera:** CameraX
- **Architecture:** MVVM with Repository pattern
- **Async:** Coroutines + Flow
- **Image Processing:** Android Bitmap API + OpenCV (optional)
- **Storage:** DataStore for preferences
- **Testing:** JUnit + Espresso + Compose Testing

## Planned Features

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

## Project Structure (Planned)

```
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/bceassociates/livetarget/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── camera/       # Camera view and controls
│   │   │   │   │   ├── settings/     # Settings screens
│   │   │   │   │   ├── components/   # Reusable UI components
│   │   │   │   │   └── theme/        # Design system
│   │   │   │   ├── data/
│   │   │   │   │   ├── repository/   # Data access layer
│   │   │   │   │   └── preferences/  # Settings storage
│   │   │   │   ├── domain/
│   │   │   │   │   ├── models/       # Data models
│   │   │   │   │   └── usecases/     # Business logic
│   │   │   │   └── detection/        # Image processing
│   │   │   └── res/
│   │   └── test/                     # Unit tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Getting Started (When Implemented)

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

## Implementation Roadmap

### Phase 1: Foundation (2-3 weeks)
- [x] Project setup and structure
- [x] Basic camera integration with CameraX
- [x] Simple UI with Jetpack Compose
- [x] Basic image capture functionality

### Phase 2: Core Detection (2-3 weeks)  
- [x] Port change detection algorithm from iOS
- [x] Image processing pipeline
- [x] Real-time difference detection
- [x] Impact point calculation and visualization

### Phase 3: Features & Polish (1-2 weeks)
- [x] Zoom controls implementation
- [x] Settings screen and preferences
- [x] Photo saving functionality
- [x] UI polish and animations

### Phase 4: Testing & Optimization (1 week)
- [x] Unit tests for core algorithms
- [x] UI tests for user workflows
- [x] Performance optimization
- [x] Memory usage optimization

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
- No equivalent to Apple Watch integration

## Contributing

When implementation begins:
1. Follow Android coding standards
2. Use Kotlin style guide
3. Include unit tests for new features
4. Follow Material Design guidelines
5. Test on multiple device types

## Dependencies (Planned)

### Core
- Jetpack Compose BOM
- CameraX
- Kotlin Coroutines
- DataStore

### Image Processing
- Android Bitmap APIs
- OpenCV (optional for performance)

### Testing
- JUnit 5
- Mockk
- Compose Testing
- Espresso

### Tools
- Detekt (static analysis)
- ktlint (code formatting)
- Gradle version catalogs

## Notes
This Android implementation will share the same core algorithms and design principles as the iOS version, documented in the `shared/` directory of this repository.