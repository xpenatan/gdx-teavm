#!/usr/bin/env bash
set -euo pipefail

CURL_VERSION="${CURL_VERSION:-8.5.0}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_ROOT_DEFAULT="$SCRIPT_DIR/build/libcurl-wss-macos"
BUILD_ROOT_INPUT="${1:-$BUILD_ROOT_DEFAULT}"
mkdir -p "$(dirname "$BUILD_ROOT_INPUT")"
BUILD_ROOT="$(cd "$(dirname "$BUILD_ROOT_INPUT")" && pwd)/$(basename "$BUILD_ROOT_INPUT")"
SOURCE_ARCHIVE="curl-${CURL_VERSION}.tar.xz"
SOURCE_URL="https://curl.se/download/${SOURCE_ARCHIVE}"
SOURCE_ROOT="$BUILD_ROOT/src"
INSTALL_ROOT="$BUILD_ROOT/install"
TARBALL_PATH="$BUILD_ROOT/${SOURCE_ARCHIVE}"
EXTRACTED_DIR="$SOURCE_ROOT/curl-${CURL_VERSION}"
OUTPUT_LIBRARY="$INSTALL_ROOT/lib/libcurl.4.dylib"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

if [[ "$(uname -s)" != "Darwin" ]]; then
  echo "This helper only supports macOS." >&2
  exit 1
fi

require_command curl
require_command tar
require_command make
require_command clang
require_command xcrun

mkdir -p "$BUILD_ROOT" "$SOURCE_ROOT"

if [[ ! -f "$TARBALL_PATH" ]]; then
  echo "Downloading ${SOURCE_ARCHIVE}..."
  curl -L --fail "$SOURCE_URL" -o "$TARBALL_PATH"
fi

if [[ ! -d "$EXTRACTED_DIR" ]]; then
  echo "Extracting ${SOURCE_ARCHIVE}..."
  tar -xf "$TARBALL_PATH" -C "$SOURCE_ROOT"
fi

SDKROOT="${SDKROOT:-$(xcrun --sdk macosx --show-sdk-path)}"
export SDKROOT
export CC="${CC:-clang}"

pushd "$EXTRACTED_DIR" >/dev/null

if [[ ! -f lib/curl_config.h ]]; then
  echo "Configuring curl ${CURL_VERSION} with WebSocket + Secure Transport support..."
  ./configure \
    --prefix="$INSTALL_ROOT" \
    --with-secure-transport \
    --enable-websockets \
    --disable-static \
    --disable-manual \
    --disable-ldap \
    --without-libpsl \
    --without-brotli \
    --without-zstd \
    --without-libidn2 \
    --without-nghttp2
fi

echo "Building curl ${CURL_VERSION}..."
make -j"$(sysctl -n hw.logicalcpu)"

echo "Installing into ${INSTALL_ROOT}..."
make install

popd >/dev/null

if [[ ! -f "$OUTPUT_LIBRARY" ]]; then
  echo "Expected runtime was not produced: $OUTPUT_LIBRARY" >&2
  exit 1
fi

echo
echo "Built ws/wss-capable macOS libcurl runtime:"
echo "  ${OUTPUT_LIBRARY}"
echo
echo "Use it with the desktop-c example:"
echo "  ./gradlew -PgdxTeaVMMacCurlPath=${OUTPUT_LIBRARY} \\"
echo "    :examples:websockets:desktop-c:websockets_desktop_c_debug_run"
