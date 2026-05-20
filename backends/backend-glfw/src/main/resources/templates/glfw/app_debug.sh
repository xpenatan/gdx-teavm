#!/usr/bin/env bash
set -euo pipefail

# Build ${BUILD_CONFIG} Configuration
NAME="${PROJECT_NAME}"
BUILD_CONFIG="${BUILD_CONFIG}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if ! command -v cmake >/dev/null 2>&1; then
    echo "cmake could not be found. Please install CMake or add it to PATH."
    exit 1
fi

echo "Generating CMake build files for $BUILD_CONFIG..."
cmake -S . -B build/cmake -DCMAKE_BUILD_TYPE="$BUILD_CONFIG"

echo "Building $NAME $BUILD_CONFIG configuration..."
cmake --build build/cmake --config "$BUILD_CONFIG"

echo "Build completed successfully for $BUILD_CONFIG configuration."
