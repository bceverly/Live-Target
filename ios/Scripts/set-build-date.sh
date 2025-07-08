#!/bin/bash

# Set Build Date Script
# This script sets the build date in the app's Info.plist during build time

# Get the current date in a readable format
BUILD_DATE=$(date "+%B %d, %Y")

# Update the build date in the target's Info.plist (if it exists)
if [ -f "${TARGET_BUILD_DIR}/${INFOPLIST_PATH}" ]; then
    /usr/libexec/PlistBuddy -c "Set :BuildDate ${BUILD_DATE}" "${TARGET_BUILD_DIR}/${INFOPLIST_PATH}" 2>/dev/null || \
    /usr/libexec/PlistBuddy -c "Add :BuildDate string ${BUILD_DATE}" "${TARGET_BUILD_DIR}/${INFOPLIST_PATH}" 2>/dev/null
fi

echo "Build date set to: ${BUILD_DATE}"