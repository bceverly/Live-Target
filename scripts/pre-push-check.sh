#!/bin/bash

#
#  pre-push-check.sh
#  Live Target Pre-Push Validation
#
#  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
#

set -e  # Exit on any error

echo "🚀 Live Target Pre-Push Validation"
echo "=================================="
echo ""

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[PRE-PUSH]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_status "Running comprehensive local CI/CD validation..."
echo ""

# Check if we're in the right directory
if [ ! -f "README.md" ] || [ ! -d "android" ] || [ ! -d "ios" ]; then
    print_error "Please run this script from the root of the Live Target project"
    exit 1
fi

# Check for uncommitted changes
if ! git diff-index --quiet HEAD --; then
    print_error "You have uncommitted changes. Please commit them before pushing."
    echo "Uncommitted files:"
    git diff-index --name-only HEAD --
    exit 1
fi

print_status "Running full CI/CD validation (this exactly matches GitHub Actions)..."
echo ""

# Run the local CI/CD script
if ./scripts/local-ci.sh; then
    print_success "✅ All local CI/CD checks passed!"
    echo ""
    print_success "🚀 Ready to push to GitHub! Your push should pass all checks."
    echo ""
else
    print_error "❌ Local CI/CD checks failed!"
    echo ""
    print_error "Fix the issues above before pushing to GitHub."
    echo "This will prevent failed builds and save you time."
    exit 1
fi