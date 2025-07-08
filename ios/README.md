# Live Target iOS

## Overview
iOS and watchOS implementation of the Live Target bullet impact detection system.

## Current Status
✅ **Active Development** - Fully functional with comprehensive features

## Features

### iOS App
- **Real-time camera feed** with live bullet impact detection
- **Automatic impact detection** using computer vision algorithms
- **Visual impact indicators** with numbered circles and customizable colors
- **Zoom controls** with hardware camera zoom (1x to 10x)
- **Settings interface** for calibration and customization
- **Photo saving** with impact annotations to Photos library
- **Apple Watch integration** for impact notifications

### watchOS App
- **Latest impact display** with zoomed images
- **Impact history** with scrollable timeline
- **Haptic feedback** on new bullet impacts
- **Connection status** indicators
- **Optimized UI** for Watch display

## Requirements

### Development
- **Xcode:** 15.0 or later
- **iOS Deployment Target:** 17.0+
- **watchOS Deployment Target:** 10.0+
- **Swift:** 5.9+

### Runtime
- **iOS Device:** iPhone with camera (physical device required)
- **Apple Watch:** Series 4 or later (optional)
- **Permissions:** Camera access, Photos library access

## Project Structure

```
ios/
├── Live Target/                    # Main iOS app
│   ├── ContentView.swift          # Main UI interface
│   ├── CameraView.swift           # Camera integration
│   ├── ChangeDetector.swift       # Core detection algorithm
│   ├── SettingsView.swift         # Configuration interface
│   ├── ZoomControl.swift          # Camera zoom controls
│   ├── WatchConnectivityManager.swift  # Watch communication
│   └── Supporting Files/
├── Live Target Watch App Watch App/  # Watch app implementation
│   ├── ContentView.swift          # Watch main interface
│   ├── ImpactHistoryView.swift    # Impact timeline
│   ├── ImpactStore.swift          # Watch data storage
│   └── WatchConnectivityManager.swift  # Watch-side communication
├── Live TargetTests/               # Unit tests
│   ├── ChangeDetectorTests.swift  # Detection algorithm tests
│   ├── ColorExtensionTests.swift  # Color utility tests
│   ├── UIImageExtensionTests.swift # Image processing tests
│   └── WatchConnectivityTests.swift # Communication tests
├── Live TargetUITests/             # UI integration tests
└── Live Target.xcodeproj           # Xcode project
```

## Getting Started

### Setup
1. **Clone the repository** and navigate to iOS directory:
   ```bash
   cd ios/
   ```

2. **Open in Xcode:**
   ```bash
   open "Live Target.xcodeproj"
   ```

3. **Configure signing:**
   - Select your development team in project settings
   - Ensure bundle identifiers are unique for your account

4. **Run on device:**
   - Select your iPhone as the target device
   - Build and run (⌘+R)

### Watch App Setup
1. **Pair Apple Watch** with your iPhone
2. **Select Watch target** in Xcode
3. **Build and install** Watch app to paired device
4. **Launch app** on both iPhone and Watch

## Configuration

### Detection Settings
Access via Settings button in main app:

- **Circle Color:** Color of impact detection circles
- **Number Color:** Color of impact sequence numbers  
- **Check Frequency:** Time between detection checks (0.5-10.0 seconds)
- **Bullet Caliber:** Affects minimum detection size (17-70 caliber)

### Camera Settings
- **Zoom Factor:** Hardware zoom level (1x-10x)
- **Zoom Persistence:** Zoom level saved between sessions

## Testing

### Unit Tests
```bash
# Run all tests
xcodebuild test -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16"

# Run specific test class
xcodebuild test -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16" -only-testing:Live_TargetTests/ChangeDetectorTests
```

### Test Coverage
- **ChangeDetectorTests:** Core detection algorithm validation
- **ColorExtensionTests:** Color parsing and conversion utilities
- **UIImageExtensionTests:** Image rotation and processing
- **WatchConnectivityTests:** Watch communication functionality
- **ChangePointTests:** Data model validation

### Manual Testing
1. **Point camera at target** (paper or steel)
2. **Tap Start** to begin detection
3. **Create impact** (finger tap, pen mark, etc.)
4. **Verify detection** with red circle and number
5. **Check Watch** for impact notification
6. **Test settings** with different caliber and color options

## Architecture

### Core Components

#### ChangeDetector
- **Pixel-level image comparison** between camera frames
- **Flood-fill algorithm** for region grouping
- **Configurable thresholds** for detection sensitivity
- **Background processing** for real-time performance

#### CameraView
- **AVFoundation integration** for camera access
- **Hardware zoom control** with smooth transitions
- **Real-time preview** with overlay rendering
- **Image capture** for detection processing

#### WatchConnectivityManager
- **WCSession management** for iPhone-Watch communication
- **Image compression** for efficient data transfer
- **Error handling** and retry logic
- **Connection state monitoring**

### Data Flow
```
Camera → Image Capture → Detection Algorithm → Impact Points
   ↓            ↓              ↓                ↓
Preview → Background Thread → UI Update → Watch Transmission
```

## Performance Optimization

### Detection Performance
- **Throttled processing** based on check interval
- **Image downscaling** for faster comparison
- **Background queue** processing
- **Memory management** for large images

### Camera Performance
- **Hardware zoom** instead of digital zoom
- **Optimized preview** rendering
- **Efficient image capture** pipeline

### Watch Performance
- **Image compression** (60KB limit for WatchConnectivity)
- **Selective data transmission** (only new impacts)
- **Low-power display** optimization

## Troubleshooting

### Common Issues

#### Camera Not Working
- Ensure **camera permissions** are granted
- Test on **physical device** (camera not available in simulator)
- Check for **camera access conflicts** with other apps

#### Watch App Not Receiving Data
- Verify **Watch is paired** and unlocked
- Check **WatchConnectivity session** status
- Ensure **Watch app is installed** and launched
- Test **iPhone-Watch proximity** (Bluetooth range)

#### Detection Not Working
- Adjust **bullet caliber** setting for impact size
- Increase **check frequency** for faster detection
- Ensure **adequate lighting** conditions
- Verify **target contrast** for better detection

#### Performance Issues
- Lower **check frequency** to reduce CPU usage
- Reduce **zoom level** if experiencing lag
- Close **other camera apps** before use
- Restart app if memory usage is high

### Debug Settings
- **SwiftLint** integration for code quality
- **Console logging** for debugging detection algorithm
- **Performance metrics** in Xcode Instruments

## Code Quality

### SwiftLint Configuration
Located in `.swiftlint.yml`:
- **Disabled rules:** trailing_whitespace, todo, line_length
- **Custom rules:** copyright_notice validation
- **Excluded paths:** Test files, Xcode project files

### Testing Standards
- **Unit tests** for all core algorithms
- **UI tests** for critical user workflows
- **Performance tests** for real-time components
- **Code coverage** tracking

## Contributing

### Development Guidelines
1. **Follow iOS Human Interface Guidelines**
2. **Use SwiftUI** for new UI components
3. **Add tests** for new functionality
4. **Update documentation** for API changes
5. **Test on physical device** before submitting

### Code Style
- **SwiftLint** compliance required
- **Clear naming** for functions and variables
- **Comprehensive documentation** for public APIs
- **Error handling** for all failure cases

## Known Limitations

### Technical Limitations
- **Physical device required** for camera functionality
- **Good lighting conditions** needed for reliable detection
- **Stable camera position** recommended during detection
- **Watch image size limited** by WatchConnectivity (60KB)

### Future Improvements
- **Multiple target support** for complex ranges
- **Shot grouping analysis** for accuracy metrics
- **Advanced calibration** for different distances
- **Cloud sync** for impact history

## Support

- **Issues:** Report bugs via GitHub Issues
- **Documentation:** See `../shared/docs/` for detailed architecture
- **Testing:** Comprehensive test suite in `Live TargetTests/`
- **Performance:** Use Xcode Instruments for profiling