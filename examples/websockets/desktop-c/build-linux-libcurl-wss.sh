#!/usr/bin/env bash
set -euo pipefail

CURL_VERSION="${CURL_VERSION:-8.5.0}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_ROOT_DEFAULT="$SCRIPT_DIR/build/libcurl-wss"
BUILD_ROOT="${1:-$BUILD_ROOT_DEFAULT}"
SOURCE_ARCHIVE="curl-${CURL_VERSION}.tar.xz"
SOURCE_URL="https://curl.se/download/${SOURCE_ARCHIVE}"
SOURCE_ROOT="$BUILD_ROOT/src"
INSTALL_ROOT="$BUILD_ROOT/install"
TARBALL_PATH="$BUILD_ROOT/${SOURCE_ARCHIVE}"
EXTRACTED_DIR="$SOURCE_ROOT/curl-${CURL_VERSION}"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

require_header() {
  if [[ ! -f "$1" ]]; then
    echo "Missing required development header: $1" >&2
    echo "Install the matching development package first." >&2
    exit 1
  fi
}

require_command curl
require_command tar
require_command make
require_command gcc

require_header /usr/include/openssl/ssl.h
require_header /usr/include/zlib.h

mkdir -p "$BUILD_ROOT" "$SOURCE_ROOT"

if [[ ! -f "$TARBALL_PATH" ]]; then
  echo "Downloading ${SOURCE_ARCHIVE}..."
  curl -L --fail "$SOURCE_URL" -o "$TARBALL_PATH"
fi

if [[ ! -d "$EXTRACTED_DIR" ]]; then
  echo "Extracting ${SOURCE_ARCHIVE}..."
  tar -xf "$TARBALL_PATH" -C "$SOURCE_ROOT"
fi

pushd "$EXTRACTED_DIR" >/dev/null

if [[ ! -f lib/curl_config.h ]]; then
  echo "Configuring curl ${CURL_VERSION} with WebSocket + OpenSSL support..."
  ./configure \
    --prefix="$INSTALL_ROOT" \
    --with-openssl \
    --enable-websockets \
    --disable-static
fi

echo "Building curl ${CURL_VERSION}..."
make -j"$(getconf _NPROCESSORS_ONLN)"

echo "Installing into ${INSTALL_ROOT}..."
make install

popd >/dev/null

echo
echo "Built ws/wss-capable libcurl runtime:"
echo "  ${INSTALL_ROOT}/lib/libcurl.so.4"
echo
echo "Use it with the desktop-c example:"
echo "  ./gradlew -PgdxTeaVMLinuxCurlPath=${INSTALL_ROOT}/lib/libcurl.so.4 \\"
echo "    :examples:websockets:desktop-c:websockets_desktop_c_debug_run"
