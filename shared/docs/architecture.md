# Live Target Architecture

## Overview
Multi-platform bullet impact detection system with iOS/watchOS primary implementation and planned Android support.

## High-Level Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   iOS App       │    │  watchOS App    │    │  Android App    │
│                 │◄──►│                 │    │   (Future)      │
│ - Camera        │    │ - Impact View   │    │ - Camera        │
│ - Detection     │    │ - History       │    │ - Detection     │
│ - Settings      │    │ - Connectivity  │    │ - Settings      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │ Shared Algorithms│
                    │ & Documentation  │
                    └─────────────────┘
```

## iOS Implementation

### Core Components

#### 1. ChangeDetector
**Responsibility:** Core bullet impact detection
- Image comparison algorithms
- Change point identification
- State management (detecting/stopped)
- Performance optimization

**Key Methods:**
- `detectChanges(in: UIImage)` - Main detection algorithm
- `startDetection()` / `stopDetection()` - State control
- `clearChanges()` - Reset detected impacts

#### 2. CameraView (UIViewRepresentable)
**Responsibility:** Camera integration and control
- Live camera feed display
- Hardware zoom control
- Image capture for processing
- Real-time preview

**Key Features:**
- AVFoundation integration
- Zoom factor management
- Orientation handling
- Performance optimization

#### 3. WatchConnectivityManager
**Responsibility:** iOS-Watch communication
- Impact data transmission
- Connection state management
- Image processing for Watch display
- Error handling and retry logic

#### 4. ContentView (Main UI)
**Responsibility:** Primary user interface
- Camera view integration
- Impact visualization (circles/numbers)
- Control buttons (start/stop/save/clear)
- Settings navigation

#### 5. SettingsView
**Responsibility:** Configuration management
- Color customization
- Detection parameters
- Bullet caliber settings
- User preferences persistence

### Data Flow

```
Camera Feed → Image Capture → Change Detection → Impact Points
     │              │              │               │
     │              │              │               ├─→ UI Overlay
     │              │              │               │
     │              │              │               ├─→ Watch Transmission
     │              │              │               │
     │              │              │               └─→ Photo Library Save
     │              │              │
     │              │              └─→ State Updates (Published)
     │              │
     │              └─→ Background Processing
     │
     └─→ Real-time Display
```

### State Management
- **@StateObject** for view models (ChangeDetector, WatchConnectivity)
- **@Published** properties for reactive UI updates
- **@AppStorage** for user preferences
- **UserDefaults** for settings persistence

## watchOS Implementation

### Core Components

#### 1. ContentView (Watch)
**Responsibility:** Main watch interface
- Latest impact display
- Impact history navigation
- Connection status indication
- Haptic feedback coordination

#### 2. ImpactStore
**Responsibility:** Impact data management
- Local impact storage
- Data persistence
- History management
- Thread-safe access

#### 3. WatchConnectivityManager (Watch)
**Responsibility:** Phone-Watch communication
- Message reception from iPhone
- Connection state monitoring
- Error handling
- Session management

### Watch-Specific Features
- **Haptic feedback** on new impacts
- **Optimized UI** for small screen
- **Low power consumption** design
- **Offline viewing** of impact history

## Data Models

### ChangePoint
```swift
struct ChangePoint {
    let id: UUID
    let location: CGPoint  // Normalized coordinates (0-1)
    let number: Int        // Sequential impact number
}
```

### WatchImpact
```swift
struct WatchImpact {
    let number: Int
    let timestamp: Date
    let zoomedImage: UIImage
}
```

## Android Implementation (Planned)

### Architecture Principles
- **MVVM pattern** with ViewModels
- **Jetpack Compose** for modern UI
- **Kotlin Coroutines** for async operations
- **Flow/StateFlow** for reactive programming

### Core Components (Planned)

#### 1. ChangeDetector (ViewModel)
```kotlin
class ChangeDetector : ViewModel() {
    private val _detectedChanges = MutableStateFlow<List<ChangePoint>>(emptyList())
    val detectedChanges = _detectedChanges.asStateFlow()
    
    private val _isDetecting = MutableStateFlow(false)
    val isDetecting = _isDetecting.asStateFlow()
}
```

#### 2. CameraView (Composable)
```kotlin
@Composable
fun CameraView(
    onImageCaptured: (Bitmap) -> Unit,
    zoomFactor: Float
) {
    // CameraX integration
    // Real-time preview
    // Zoom control
}
```

### Technology Stack (Android)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Camera:** CameraX
- **Image Processing:** Android Bitmap API + OpenCV (optional)
- **Architecture:** MVVM with Repository pattern
- **Async:** Coroutines + Flow
- **Storage:** DataStore for preferences
- **Testing:** JUnit + Espresso

## Shared Components

### Algorithm Documentation
- Change detection algorithm specifications
- Image processing pipeline documentation
- Performance optimization guidelines
- Platform-specific implementation notes

### Design System
- Color palette and theming
- Typography specifications
- Component design patterns
- Accessibility guidelines

### Testing Strategy
- Unit tests for core algorithms
- Integration tests for camera functionality
- UI tests for user workflows
- Performance benchmarks

## Performance Considerations

### iOS Optimizations
- Background thread processing
- Image downscaling for analysis
- Throttled detection intervals
- Memory management for large images

### Android Optimizations (Planned)
- Coroutine-based async processing
- Bitmap recycling for memory efficiency
- OpenCV integration for performance
- Background service for detection

## Security & Privacy

### Data Handling
- No cloud storage of images
- Local-only processing
- User consent for photo library access
- Secure local storage of preferences

### Permissions
- **iOS:** Camera access, Photo Library access
- **Android:** Camera, Storage permissions
- **Minimal data collection** - only necessary for functionality

## Deployment & CI/CD

### iOS Deployment
- Xcode Cloud or GitHub Actions
- TestFlight for beta distribution
- App Store distribution
- SwiftLint code quality checks

### Android Deployment (Planned)
- GitHub Actions for builds
- Google Play Console for distribution
- Internal testing track
- Lint and code quality checks

### Multi-Platform CI
- Separate build pipelines per platform
- Shared documentation validation
- Cross-platform algorithm testing
- Automated release management