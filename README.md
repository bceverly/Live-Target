# Live Target

[![Multi-Platform CI/CD](https://github.com/bceverly/Live-Target/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/ci-cd.yml)
[![iOS CI/CD Pipeline](https://github.com/bceverly/Live-Target/actions/workflows/ios-ci.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/ios-ci.yml)
[![Android CI/CD Pipeline](https://github.com/bceverly/Live-Target/actions/workflows/android-ci.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/android-ci.yml)
[![SwiftLint](https://github.com/bceverly/Live-Target/actions/workflows/swiftlint.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/swiftlint.yml)
[![Dependency Security Check](https://github.com/bceverly/Live-Target/actions/workflows/dependency-security-check.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/dependency-security-check.yml)
[![Release Pipeline](https://github.com/bceverly/Live-Target/actions/workflows/release.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/release.yml)

[![Platform](https://img.shields.io/badge/platform-iOS%2018.5%2B%20%7C%20Android%207.0%2B-blue.svg)](https://developer.apple.com/ios/)
[![Swift](https://img.shields.io/badge/Swift-5.0-orange.svg)](https://swift.org/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple.svg)](https://kotlinlang.org/)
[![Xcode](https://img.shields.io/badge/Xcode-16.4-blue.svg)](https://developer.apple.com/xcode/)
[![Android Studio](https://img.shields.io/badge/Android%20Studio-2025.1.1-green.svg)](https://developer.android.com/studio)
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)](#license)

[![Dependabot](https://img.shields.io/badge/Dependabot-enabled-brightgreen.svg)](https://github.com/bceverly/Live-Target/network/dependencies)
[![Code Quality](https://img.shields.io/badge/Code%20Quality-SwiftLint-brightgreen.svg)](https://github.com/realm/SwiftLint)
[![Security](https://img.shields.io/badge/Security-Monitored-green.svg)](#security)

A multi-platform bullet impact detection system for target shooting practice.

## Overview

Live Target helps shooting enthusiasts automatically detect and track bullet impacts on targets using computer vision. The system provides real-time visual feedback and can save annotated target images for analysis.

## Platform Support

| Platform | Status | Features |
|----------|--------|----------|
| **iOS** | ✅ **Completed** | Camera, Detection, Settings, Apple Watch Integration |
| **watchOS** | ✅ **Completed** | Impact Display, History, Haptic Feedback |
| **Android** | ✅ **Completed** | Camera, Detection, Settings, Samsung Galaxy Watch Integration |
| **Samsung Watch** | ✅ **Completed** | Impact Display, Notifications, Status Integration |

## Repository Structure

```
Live-Target/
├── ios/                    # iOS & watchOS implementation
│   ├── Live Target/        # Main iOS app
│   ├── Live Target Watch App/   # Watch app
│   └── Live Target.xcodeproj
├── android/                # Android implementation (completed)
│   ├── app/               # Main Android app (Kotlin + Compose)
│   └── gradle/            # Build system configuration
├── shared/                 # Cross-platform resources
│   ├── algorithms/        # Detection algorithm docs
│   ├── design/           # Design system & UI specs
│   ├── assets/           # Shared assets & icons
│   └── docs/             # Architecture & API docs
├── tools/                 # Build tools & scripts
│   ├── scripts/          # Platform build scripts
│   └── ci/               # CI/CD configurations
└── .github/              # GitHub workflows
```

## Features

### Core Functionality (Available on Both Platforms)
- **Real-time camera feed** with live impact detection
- **Automatic bullet impact detection** using computer vision algorithms
- **Visual impact indicators** with numbered circles and customizable colors
- **Advanced zoom controls** with hardware zoom support (1x to 10x magnification)
- **Configurable detection sensitivity** for different bullet calibers (17-70 caliber)
- **Adjustable check frequency** for optimal performance (0.5-10 seconds)
- **Photo saving** with impact annotations to device gallery
- **Settings persistence** with comprehensive customization options
- **Help system** with detailed usage instructions and troubleshooting

### Cross-Platform Feature Parity
Both iOS and Android versions offer **identical functionality** with platform-optimized implementations:

| Feature | iOS Implementation | Android Implementation |
|---------|-------------------|----------------------|
| **Camera Integration** | AVFoundation with hardware zoom | CameraX with hardware zoom support |
| **Watch Integration** | Apple Watch with WatchConnectivity | Samsung Galaxy Watch with Accessory SDK |
| **Impact Notifications** | Haptic feedback + visual display | Notifications + visual display |
| **Watch Status Icons** | Green/Red/Gray indicators | Green/Red/Gray indicators |
| **Zoom Controls** | Native iOS zoom interface | Material Design zoom controls |
| **Settings Storage** | UserDefaults persistence | DataStore preferences |
| **Photo Saving** | Photos library integration | Android MediaStore gallery |
| **UI Framework** | SwiftUI with iOS design guidelines | Jetpack Compose with Material Design 3 |

### iOS-Specific Implementation Details
- **Apple Watch companion app** with dedicated watchOS interface
- **WatchConnectivity framework** for seamless iPhone-Watch communication
- **SwiftUI native interface** following iOS Human Interface Guidelines
- **Background processing** with AVFoundation optimization
- **Haptic feedback** on Watch for tactile impact notifications
- **Watch app installation** through paired iPhone

### Android-Specific Implementation Details  
- **Samsung Galaxy Watch integration** with dedicated watch notifications
- **Samsung Accessory SDK** for robust Galaxy Watch communication
- **Material Design 3 interface** with dynamic theming support
- **Jetpack Compose UI** with reactive state management
- **CameraX integration** for modern camera handling and optimization
- **DataStore preferences** for efficient settings persistence
- **Flexible file system access** for advanced image management

## Development Environment Setup

### Required Software

#### For iOS Development
| Software | Version | Purpose | Installation |
|----------|---------|---------|--------------|
| **macOS** | 13.0+ | Required for iOS development | System requirement |
| **Xcode** | 15.0+ | iOS/watchOS development IDE | App Store or Apple Developer |
| **Xcode Command Line Tools** | Latest | Build tools and Swift compiler | `xcode-select --install` |
| **Homebrew** | Latest | Package manager for dev tools | `/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"` |
| **SwiftLint** | 0.50+ | Code quality and style checking | `brew install swiftlint` |

#### For Android Development
| Software | Version | Purpose | Installation |
|----------|---------|---------|--------------|
| **Android Studio** | 2023.1+ | Android development IDE | [Download from Google](https://developer.android.com/studio) |
| **Java JDK** | 17+ | Required for Android development | `brew install openjdk@17` |
| **Android SDK** | API 24+ | Android development framework | Installed via Android Studio |
| **Gradle** | 8.0+ | Android build system | Bundled with Android Studio |

#### For Samsung Galaxy Watch Development
| Software | Version | Purpose | Installation |
|----------|---------|---------|--------------|
| **Samsung Accessory SDK** | 1.4.0+ | Galaxy Watch communication | [Samsung Developer Portal](https://developer.samsung.com/galaxy-watch) |
| **Galaxy Watch Studio** | Latest | Watch face and app development | [Download from Samsung](https://developer.samsung.com/galaxy-watch-studio) |
| **Samsung Galaxy Watch** | Series 4+ | Physical device for testing | Required for watch integration testing |
| **Samsung Health SDK** | Optional | Health data integration | [Samsung Developer Portal](https://developer.samsung.com/health) |
| **Tizen Studio** | Optional | Advanced watch development | [Download from Samsung](https://developer.tizen.org/development/tizen-studio) |

#### Optional Development Tools
| Software | Purpose | Installation |
|----------|---------|--------------|
| **Git** | Version control | `brew install git` |
| **GitHub CLI** | GitHub integration | `brew install gh` |
| **VS Code** | Code editing for shared docs | `brew install --cask visual-studio-code` |
| **Simulator** | iOS device simulation | Included with Xcode |

### Platform-Specific Setup

### iOS Development Setup

#### 1. Install Xcode
```bash
# Option 1: Install from App Store (Recommended)
# Search for "Xcode" in App Store and install

# Option 2: Download from Apple Developer Portal
# Visit https://developer.apple.com/download/
```

#### 2. Install Command Line Tools
```bash
# Install Xcode Command Line Tools
xcode-select --install

# Verify installation
xcode-select -p
# Should output: /Applications/Xcode.app/Contents/Developer
```

#### 3. Install Development Dependencies
```bash
# Install Homebrew (if not already installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install SwiftLint for code quality
brew install swiftlint

# Verify SwiftLint installation
swiftlint version
```

#### 4. Setup iOS Project
```bash
# Clone repository
git clone https://github.com/bceverly/Live-Target.git
cd Live-Target

# Navigate to iOS project
cd ios/

# Open in Xcode
open "Live Target.xcodeproj"
```

#### 5. Configure Signing (Required for Device Testing)
1. **Open project in Xcode**
2. **Select "Live Target" project** in navigator
3. **Choose "Live Target" target**
4. **Go to "Signing & Capabilities" tab**
5. **Select your development team**
6. **Ensure bundle identifier is unique**
7. **Repeat for Watch app target**

#### 6. Build and Test
```bash
# Build from command line
cd ios/
xcodebuild -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16" build

# Run tests
xcodebuild test -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16"

# Or use build script
../tools/scripts/build-ios.sh
```

### Android Development Setup

#### 1. Install Java JDK
```bash
# Install OpenJDK 17 via Homebrew
brew install openjdk@17

# Add to PATH (add to ~/.zshrc or ~/.bash_profile)
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc

# Verify installation
java --version
```

#### 2. Install Android Studio
```bash
# Download and install Android Studio
# Visit: https://developer.android.com/studio

# Or install via Homebrew Cask
brew install --cask android-studio
```

#### 3. Setup Android SDK
1. **Launch Android Studio**
2. **Follow setup wizard**
3. **Install Android SDK** (API level 24 minimum)
4. **Install Android Virtual Device (AVD)**
5. **Configure SDK path** in Android Studio preferences

#### 4. Setup Environment Variables
```bash
# Add to ~/.zshrc or ~/.bash_profile
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools

# Reload shell configuration
source ~/.zshrc
```

#### 5. Setup Android Project
```bash
# Navigate to Android project
cd android/

# Build with Gradle
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (runs both locally and in CI)
./gradlew connectedDebugAndroidTest

# Or use build script
../tools/scripts/build-android.sh
```

#### 6. Setup Samsung Galaxy Watch Development (Optional)

**Prerequisites:**
- Samsung Galaxy Watch (Series 4 or later) paired with Android phone
- Samsung Galaxy Wearable app installed on phone
- Developer mode enabled on watch

**Samsung Accessory SDK Setup:**
```bash
# 1. Register as Samsung Developer
# Visit: https://developer.samsung.com/
# Create account and accept terms

# 2. Download Samsung Accessory SDK
# Visit: https://developer.samsung.com/galaxy-watch
# Download latest SDK version (1.4.0+)

# 3. Add SDK to Android project
# The SDK dependency is already included in build.gradle.kts:
# implementation("com.samsung.android:accessory:1.4.0")
```

**Galaxy Watch Setup for Development:**
1. **Enable Developer Mode on Watch:**
   - Go to **Settings > About watch**
   - Tap **Software version** 7 times
   - Developer options will appear in Settings

2. **Enable Debug Mode:**
   - In **Settings > Developer options**
   - Enable **Debugging over Wi-Fi** or **ADB debugging**
   - Enable **Debug applications**

3. **Install Galaxy Watch Studio (Optional):**
   ```bash
   # Download from Samsung Developer Portal
   # Used for watch face development and advanced debugging
   # Not required for Accessory SDK integration
   ```

4. **Test Connection:**
   ```bash
   # Build and run Android app
   cd android/
   ./gradlew assembleDebug
   
   # Install on phone paired with Galaxy Watch
   # Enable watch integration in app settings
   # Test connectivity with "Start" button
   ```

**Samsung Developer Account Requirements:**
- Required for publishing watch apps to Galaxy Store
- Free registration at https://developer.samsung.com/
- Certificate signing for production watch apps
- Not required for testing Accessory SDK integration

**Troubleshooting Galaxy Watch Development:**
- Ensure watch and phone are on same Wi-Fi network
- Check Samsung Galaxy Wearable app permissions
- Verify Bluetooth connection is stable
- Restart both devices if connection issues persist

### Development Workflow

#### Daily Development
```bash
# 1. Update repository
git pull origin main

# 2. iOS development
cd ios/
open "Live Target.xcodeproj"

# 3. Run tests before committing
xcodebuild test -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16"

# 4. Check code quality
swiftlint

# 5. Android development (when available)
cd ../android/
./gradlew test
```

#### Code Quality Checks
```bash
# Run SwiftLint on iOS code
cd ios/
swiftlint lint

# Auto-fix some SwiftLint issues
swiftlint --fix

# Run all tests
cd ios/
xcodebuild test -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16"
```

### Troubleshooting

#### Common iOS Issues

**Xcode Command Line Tools Issues:**
```bash
# Reset command line tools
sudo xcode-select --reset
sudo xcode-select -s /Applications/Xcode.app/Contents/Developer
```

**SwiftLint SourceKit Crashes:**
```bash
# Reinstall SwiftLint
brew uninstall swiftlint
brew install swiftlint

# Or use GitHub Actions for reliable linting
git push  # Triggers CI/CD with SwiftLint
```

**Simulator Issues:**
```bash
# Reset iOS Simulator
xcrun simctl erase all

# List available simulators
xcrun simctl list devices
```

**Build Cache Issues:**
```bash
# Clean Xcode build cache
rm -rf ~/Library/Developer/Xcode/DerivedData

# Clean project in Xcode: Product > Clean Build Folder (⌘+Shift+K)
```

#### Common Android Issues

**Gradle Issues:**
```bash
# Clean Gradle cache
./gradlew clean

# Refresh dependencies
./gradlew --refresh-dependencies
```

**SDK Issues:**
```bash
# Update SDK tools via Android Studio
# Tools > SDK Manager > Update
```

**Samsung Galaxy Watch Issues:**
```bash
# Check Samsung Accessory SDK integration
./gradlew build --info | grep "samsung"

# Verify watch connection
# Check Samsung Galaxy Wearable app on phone
# Ensure watch and phone are paired
# Test with watch integration toggle in app settings
```

**Watch Connection Troubleshooting:**
- Ensure Galaxy Watch is paired and connected via Samsung Galaxy Wearable app
- Check Bluetooth connectivity between phone and watch
- Verify watch is unlocked and within range
- Restart Samsung Accessory Service if connection fails
- Check app permissions for Samsung Accessory framework

### Hardware Requirements

#### iOS Development
- **Mac computer** (MacBook, iMac, Mac Studio, Mac Pro)
- **8GB RAM minimum** (16GB+ recommended for Xcode)
- **50GB free disk space** (for Xcode, iOS SDK, simulators)
- **iPhone device** for camera functionality testing
- **Apple Watch** (optional, for Watch app testing)

#### Android Development
- **Any computer** (Windows, macOS, Linux)
- **8GB RAM minimum** (16GB+ recommended for Android Studio)
- **30GB free disk space** (for Android Studio, SDK, emulators)
- **Android device** for camera functionality testing
- **Samsung Galaxy Watch** (optional, Series 4+ for watch integration testing)

### Getting Started

## How It Works

### Detection Algorithm
1. **Continuous camera capture** at configurable intervals
2. **Image comparison** between consecutive frames
3. **Pixel-level difference detection** with adjustable thresholds  
4. **Region grouping** using flood-fill algorithm
5. **Impact point calculation** from region centroids
6. **Size filtering** based on bullet caliber settings

### Technical Implementation
- **Computer vision** using native platform APIs
- **Background processing** for real-time performance
- **Normalized coordinates** for device-independent accuracy
- **Throttled detection** to balance accuracy and performance

For detailed technical documentation, see [`shared/docs/architecture.md`](shared/docs/architecture.md).

## Configuration

### Detection Settings
- **Bullet Caliber:** 17-70 (affects minimum detection size)
- **Check Frequency:** 0.5-10.0 seconds between comparisons
- **Color Customization:** Impact circle and number colors
- **Zoom Levels:** 1x to 10x camera zoom

### Performance Tuning
- **Lower check frequency** for better battery life
- **Higher caliber settings** to reduce false positives
- **Zoom adjustment** for optimal target visibility

## Testing

### iOS Testing
```bash
cd ios/
xcodebuild test -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16"
```

### Test Coverage
- **Unit tests** for core detection algorithms
- **Integration tests** for camera functionality  
- **UI tests** for user workflows
- **Performance tests** for real-time processing

See test files in `ios/Live TargetTests/` for detailed test coverage.

## Contributing

### Development Workflow
1. **Fork** the repository
2. **Create feature branch** from `main`
3. **Implement changes** following platform guidelines
4. **Add tests** for new functionality
5. **Submit pull request** with detailed description

### Code Standards
- **iOS:** SwiftLint configuration in `ios/.swiftlint.yml`
- **Android:** Detekt + ktlint (when implemented)
- **Documentation:** Update relevant docs in `shared/`
- **Testing:** Maintain test coverage for new features

### Platform Guidelines
- **iOS:** Follow iOS Human Interface Guidelines
- **Android:** Follow Material Design principles  
- **Shared:** Document algorithms and design decisions

## Architecture

The system uses a **shared documentation approach** with **platform-optimized implementations**:

- **Core algorithms** documented in `shared/algorithms/`
- **Design system** specified in `shared/design/`
- **Platform implementations** optimized for each ecosystem
- **Common CI/CD** workflows for both platforms

This approach allows for:
- **Consistent user experience** across platforms
- **Platform-specific optimizations** for best performance
- **Shared learning** and algorithm improvements
- **Independent release cycles** per platform

## Security

This project includes comprehensive security monitoring:
- **Automated dependency scanning** with Dependabot
- **Weekly security audits** for vulnerabilities
- **Secret detection** in code reviews
- **Secure CI/CD pipeline** with proper permissions

For security issues, please contact: security@bceassociates.com

## Roadmap

### iOS (Completed v0.91)
- [x] Core bullet detection algorithm with configurable sensitivity
- [x] Real-time camera integration with AVFoundation
- [x] Apple Watch companion app with dedicated watchOS interface
- [x] Comprehensive settings and customization options
- [x] Photo saving functionality with impact annotations
- [x] Advanced zoom controls (1x-10x hardware zoom)
- [x] Help system with detailed usage instructions
- [x] Complete feature parity with Android version

### Future Enhancements (Both Platforms)
- [ ] Advanced calibration options for different distances
- [ ] Multiple target support for complex ranges
- [ ] Shot grouping analysis with accuracy metrics
- [ ] Cloud sync for impact history
- [ ] Video recording with impact detection overlay

### Android (Completed v0.91)
- [x] Project setup with modern Gradle build system
- [x] CameraX integration with hardware zoom support
- [x] Core detection algorithm implementation with pixel-level analysis
- [x] Material Design 3 interface with Jetpack Compose
- [x] Comprehensive settings and preferences with DataStore
- [x] Samsung Galaxy Watch integration with Accessory SDK
- [x] Watch status indicators and real-time connectivity monitoring
- [x] Impact notifications to watch with visual feedback
- [x] Photo gallery integration with MediaStore
- [x] Advanced zoom controls (1x-10x magnification)
- [x] Help system with detailed usage instructions
- [x] Performance optimization for real-time detection
- [x] Complete feature parity with iOS version

### Shared Infrastructure
- [x] Multi-platform repository structure
- [x] Algorithm documentation
- [x] Design system specification
- [x] Build automation scripts
- [ ] Cross-platform testing framework
- [ ] Automated release workflows

## License

This is a commercial application. All rights reserved.

## Copyright

Copyright © 2025 BCEAssociates, Inc. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.

## Support

- **Issues:** Use GitHub Issues for bug reports and feature requests
- **Discussions:** Use GitHub Discussions for questions and ideas
- **Documentation:** Check `shared/docs/` for technical details
- **Platform-specific:** See platform README files for specific guidance