#!/bin/bash

# Build script for Android Live Target app (when implemented)
# Usage: ./build-android.sh [variant]

set -e

VARIANT=${1:-"debug"}
PROJECT_DIR="$(dirname "$0")/../../android"

echo "🤖 Building Android Live Target..."
echo "Variant: $VARIANT"
echo "Project Directory: $PROJECT_DIR"

cd "$PROJECT_DIR"

# Check if Android project exists
if [ ! -f "build.gradle.kts" ] && [ ! -f "build.gradle" ]; then
    echo "❌ Error: Android project not found in $PROJECT_DIR"
    echo "📝 Android implementation is planned but not yet started"
    echo "See android/README.md for implementation roadmap"
    exit 1
fi

# Run lint if available
if [ -f "gradlew" ]; then
    echo "🔍 Running Android lint..."
    ./gradlew lint || echo "⚠️  Lint warnings found"
else
    echo "⚠️  Gradle wrapper not found, skipping lint"
fi

# Build the specified variant
echo "🔨 Building Android app ($VARIANT)..."
if [ -f "gradlew" ]; then
    ./gradlew "assemble${VARIANT^}"
    echo "✅ Android build completed successfully!"
else
    echo "❌ Error: Gradle wrapper not found"
    exit 1
fi