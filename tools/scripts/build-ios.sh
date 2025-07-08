#!/bin/bash

# Build script for iOS Live Target app
# Usage: ./build-ios.sh [scheme] [configuration]

set -e

SCHEME=${1:-"Live Target"}
CONFIGURATION=${2:-"Debug"}
PROJECT_DIR="$(dirname "$0")/../../ios"

echo "🍎 Building iOS Live Target..."
echo "Scheme: $SCHEME"
echo "Configuration: $CONFIGURATION"
echo "Project Directory: $PROJECT_DIR"

cd "$PROJECT_DIR"

# Check if Xcode project exists
if [ ! -d "Live Target.xcodeproj" ]; then
    echo "❌ Error: Live Target.xcodeproj not found in $PROJECT_DIR"
    exit 1
fi

# Run SwiftLint if available
if command -v swiftlint &> /dev/null; then
    echo "🔍 Running SwiftLint..."
    swiftlint lint || echo "⚠️  SwiftLint warnings found"
else
    echo "⚠️  SwiftLint not found, skipping code analysis"
fi

# Build for iOS Simulator
echo "🔨 Building for iOS Simulator..."
xcodebuild \
    -project "Live Target.xcodeproj" \
    -scheme "$SCHEME" \
    -configuration "$CONFIGURATION" \
    -destination "platform=iOS Simulator,name=iPhone 16" \
    build

echo "✅ iOS build completed successfully!"