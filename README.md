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

Live Target is an iOS application that uses your phone’s camera to monitor a physical target (paper or steel) and automatically registers hits when visual changes are detected. When a hit is detected, Live Target will:

✅ Draw a **red circle** around the detected impact  
✅ Number the hit for tracking shot sequence  
✅ Store and display shot history for review

### Apple Watch Companion

If you have an Apple Watch, Live Target includes a companion app that shows:

- The **latest image** of your target with the latest hit circled
- Shot sequence numbers at a glance on your wrist
- Quick reference while on the range without needing to check your phone

### Features (Planned)

- Live camera monitoring of targets for hit detection
- Hit marking with automatic numbering
- Target image history with hit overlays
- Apple Watch live sync
- Optional shot timer integration
- Local and iCloud history storage

### Tech Stack

- Swift / SwiftUI (iOS + watchOS)
- AVFoundation (camera integration)
- CoreML / Vision (optional advanced hit detection in future)
- CoreData or CloudKit for data persistence

### Status

🚧 **In development.** Contributions, testing, and ideas are welcome.

### Security

This project includes comprehensive security monitoring:
- **Automated dependency scanning** with Dependabot
- **Weekly security audits** for vulnerabilities
- **Secret detection** in code reviews
- **Secure CI/CD pipeline** with proper permissions

For security issues, please contact: security@bceassociates.com

### License

This is a commercial application. All rights reserved.

---

## Copyright

Copyright © 2025 BCEAssociates, Inc. All rights reserved.

This software is proprietary and confidential. Unauthorized copying, distribution, or use is strictly prohibited.

---

Stay tuned for builds, screenshots, and testing instructions as the project progresses.

