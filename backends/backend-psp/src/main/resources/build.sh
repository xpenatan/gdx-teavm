#!/bin/bash
set -e

# Detect OS and ensure proper environment
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    echo "Error: build.sh must be run in WSL on Windows. Use build.bat instead."
    read -p "Press any key to continue..." -n 1
    exit 1
fi

# Set PSPDEV

if [ -z "$PSPDEV" ]; then
    export PSPDEV="/mnt/e/Dev/Env/Ubuntu/pspdev"
fi

export PATH="$PSPDEV/bin:$PATH"

echo ""
echo "===================================="
echo "  Building PSP EBOOT..."
echo "===================================="
echo ""

# Create build directory
mkdir -p build
cd build

# Configure with psp-cmake
# -DENC_PRX=1 // None Modified PSP
psp-cmake -DBUILD_PRX=1 ..

# Build with make
make

# Ensure c/release directory exists and copy EBOOT
mkdir -p ../c/release
cp EBOOT.PBP ../c/release/
cp ${PROJECT_NAME}.prx ../c/release/
rm EBOOT.PBP
rm ${PROJECT_NAME}.prx

echo ""
echo "===================================="
echo "  SUCCESS!"
echo "===================================="
echo ""
echo "EBOOT.PBP created at $(wslpath -w ../c/release/EBOOT.PBP)"
echo ""