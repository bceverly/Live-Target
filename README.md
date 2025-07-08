# Live Target

[![iOS CI/CD Pipeline](https://github.com/bceverly/Live-Target/actions/workflows/ios-ci.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/ios-ci.yml)
[![SwiftLint](https://github.com/bceverly/Live-Target/actions/workflows/swiftlint.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/swiftlint.yml)
[![Dependency Security Check](https://github.com/bceverly/Live-Target/actions/workflows/dependency-security-check.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/dependency-security-check.yml)
[![Release Pipeline](https://github.com/bceverly/Live-Target/actions/workflows/release.yml/badge.svg)](https://github.com/bceverly/Live-Target/actions/workflows/release.yml)

[![Platform](https://img.shields.io/badge/platform-iOS%2018.5%2B-blue.svg)](https://developer.apple.com/ios/)
[![Swift](https://img.shields.io/badge/Swift-5.0-orange.svg)](https://swift.org/)
[![Xcode](https://img.shields.io/badge/Xcode-16.4-blue.svg)](https://developer.apple.com/xcode/)
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
| **iOS** | ✅ **Active Development** | Camera, Detection, Settings, Watch Integration |
| **watchOS** | ✅ **Active Development** | Impact Display, History, Haptic Feedback |
| **Android** | 📋 **Planned** | Camera, Detection, Settings (No Watch) |

## Repository Structure

```
Live-Target/
├── ios/                    # iOS & watchOS implementation
│   ├── Live Target/        # Main iOS app
│   ├── Live Target Watch App/   # Watch app
│   └── Live Target.xcodeproj
├── android/                # Android implementation (planned)
│   └── README.md          # Implementation roadmap
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

### Core Functionality
- **Real-time camera feed** with live impact detection
- **Automatic bullet impact detection** using computer vision
- **Visual impact indicators** with numbered circles
- **Configurable detection sensitivity** for different bullet calibers
- **Photo saving** with impact annotations
- **Zoom controls** for precise targeting

### iOS-Specific Features
- **Apple Watch integration** with impact notifications
- **Haptic feedback** on Watch for new impacts  
- **SwiftUI native interface** following iOS design guidelines
- **Background processing** for optimal performance

### Planned Android Features
- **Material Design 3 interface** following Android guidelines
- **CameraX integration** for modern camera handling
- **OpenCV acceleration** for improved performance
- **Flexible file system access** for image management

## Getting Started

### iOS Development

#### Prerequisites
- Xcode 15.0 or later
- iOS 17.0+ / watchOS 10.0+
- Apple Developer account (for device testing)
- Physical iPhone with camera

#### Setup
```bash
# Navigate to iOS project
cd ios/

# Open in Xcode
open "Live Target.xcodeproj"

# Or build from command line
../tools/scripts/build-ios.sh
```

### Android Development (Future)
```bash
# Navigate to Android project (when available)
cd android/

# Build with Gradle
../tools/scripts/build-android.sh
```

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

### iOS (Current Focus)
- [x] Core bullet detection algorithm
- [x] Real-time camera integration
- [x] Apple Watch companion app
- [x] Settings and customization
- [x] Photo saving functionality
- [x] Zoom controls
- [ ] Advanced calibration options
- [ ] Multiple target support
- [ ] Shot grouping analysis

### Android (Planned)
- [ ] Project setup and basic camera integration
- [ ] Core detection algorithm port
- [ ] Material Design 3 interface
- [ ] Settings and preferences
- [ ] Photo gallery integration
- [ ] Performance optimization

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