#!/bin/bash

#
#  local-ci.sh
#  Live Target Local CI/CD Runner
#
#  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
#

set -e  # Exit on any error

echo "🚀 Live Target Local CI/CD Runner"
echo "================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "README.md" ] || [ ! -d "android" ] || [ ! -d "ios" ]; then
    print_error "Please run this script from the root of the Live Target project"
    exit 1
fi

# Set up Java environment for Android
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
if [ ! -d "$JAVA_HOME" ]; then
    print_error "Android Studio not found at expected location"
    print_error "Please install Android Studio or update JAVA_HOME in this script"
    exit 1
fi

# Android CI/CD Steps
print_status "Starting Android CI/CD checks..."
echo ""

cd android

print_status "📱 Running Android lint..."
./gradlew lintDebug
print_success "Android lint passed"

print_status "📱 Running Android unit tests..."
./gradlew testDebugUnitTest
print_success "Android unit tests passed"

print_status "📱 Building Android debug APK..."
./gradlew assembleDebug
print_success "Android debug APK built successfully"

print_status "📱 Running Android test coverage..."
./gradlew jacocoTestReport
print_success "Android test coverage report generated"

cd ..

# iOS CI/CD Steps (if Xcode is available)
# Check if Xcode is properly configured
if command -v xcodebuild &> /dev/null; then
    # Check if xcode-select is pointing to Command Line Tools instead of Xcode
    if xcodebuild -version &> /dev/null; then
        XCODE_CONFIGURED=true
    else
        print_warning "Xcode detected but not configured properly"
        print_warning "Run: sudo xcode-select --switch /Applications/Xcode.app/Contents/Developer"
        XCODE_CONFIGURED=false
    fi
else
    XCODE_CONFIGURED=false
fi

if [ "$XCODE_CONFIGURED" = true ]; then
    print_status "Starting iOS CI/CD checks..."
    echo ""
    
    cd ios
    
    print_status "🍎 Running iOS build (matching GitHub Actions)..."
    if xcodebuild -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16,OS=18.5" -configuration Debug clean build CODE_SIGNING_ALLOWED=NO; then
        print_success "iOS build passed"
    else
        print_error "iOS build failed - this will fail in GitHub Actions!"
        exit 1
    fi
    
    print_status "🍎 Running iOS unit tests (matching GitHub Actions)..."
    if xcodebuild -project "Live Target.xcodeproj" -scheme "Live Target" -destination "platform=iOS Simulator,name=iPhone 16,OS=18.5" -configuration Debug test CODE_SIGNING_ALLOWED=NO; then
        print_success "iOS unit tests passed"
    else
        print_error "iOS unit tests failed - this will fail in GitHub Actions!"
        exit 1
    fi
    
    print_status "🍎 Running SwiftLint (matching GitHub Actions)..."
    if command -v swiftlint &> /dev/null; then
        cd ..
        # Match GitHub Actions SwiftLint behavior
        cd ios
        if swiftlint lint; then
            print_success "SwiftLint passed (matching GitHub Actions)"
        else
            print_error "SwiftLint found critical errors - this will fail in GitHub Actions!"
            exit 1
        fi
        cd ..
        cd ios
    else
        print_error "SwiftLint not installed - install with: brew install swiftlint"
        exit 1
    fi
    
    cd ..
else
    print_warning "Xcode not properly installed, skipping iOS CI/CD checks"
    print_warning "Install Xcode from the App Store to enable iOS CI/CD"
fi

# Security checks (matching GitHub Actions)
print_status "Running security checks..."
echo ""

print_status "🔒 Running semgrep security scan (matching GitHub Actions)..."
if command -v semgrep &> /dev/null; then
    if semgrep --config=p/security-audit --config=p/secrets --config=p/kotlin --sarif --output=semgrep.sarif .; then
        print_success "Semgrep security scan passed"
    else
        print_error "Semgrep security scan failed - this will fail in GitHub Actions!"
        exit 1
    fi
else
    print_warning "Semgrep not installed - install with: pip3 install semgrep"
fi

print_status "🔒 Checking for hardcoded secrets (matching GitHub Actions)..."
# Android secret detection (matching GitHub Actions pattern)
if grep -r -i "api_key\|password\|secret\|token\|keystore" --include="*.kt" --include="*.java" --include="*.xml" android/app/src/ | grep -v "//.*TODO\|//.*FIXME" | grep -v "samsung/android/sdk/accessory" | grep -v "STUB_" | grep -v "class.*Token" | grep -v "authToken:" | grep -v "onAuth" | grep -v "import.*Token"; then
    print_error "Potential secrets found in Android code - this will fail in GitHub Actions!"
    exit 1
else
    print_success "No secrets detected in Android code"
fi

# iOS secret detection
SECRET_PATTERNS="api_key|password|secret|token|private_key|keystore|certificate"
if find ios/ -name "*.swift" -exec grep -l -i "$SECRET_PATTERNS" {} \; 2>/dev/null | grep -v "Test" | grep -v "\.git"; then
    print_error "Potential secrets found in iOS code - this will fail in GitHub Actions!"
    exit 1
else
    print_success "No secrets detected in iOS code"
fi

echo ""
print_success "✅ All CI/CD checks completed successfully!"
echo ""
echo "📊 Summary:"
echo "  - Android lint: ✅"
echo "  - Android unit tests: ✅"  
echo "  - Android build: ✅"
echo "  - iOS build: ✅ (if Xcode available)"
echo "  - iOS unit tests: ✅ (if Xcode available)"
echo "  - Security scan: ✅"
echo ""
echo "🚀 Ready to push to GitHub!"