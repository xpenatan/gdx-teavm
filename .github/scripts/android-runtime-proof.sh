#!/usr/bin/env bash
set -euo pipefail

collect_diagnostics() {
  adb shell dumpsys activity activities > "$PROOF_DIR/activities.txt" 2>&1 || true
  adb logcat -d > "$PROOF_DIR/logcat.txt" 2>&1 || true
}
trap collect_diagnostics EXIT

adb install -r "$APK_PATH" | tee "$PROOF_DIR/install.log"
adb logcat -c
adb shell am force-stop "$APP_ID"
adb shell am start -W -n "$APP_ID/$ACTIVITY" | tee "$PROOF_DIR/launch.log"

sleep 12
APP_PROCESS="$(adb shell pidof "$APP_ID" | tr -d '\r')"
if [ -z "$APP_PROCESS" ]; then
  echo "$EXAMPLE_LABEL Android process exited before proof capture."
  exit 1
fi
echo "$APP_PROCESS" > "$PROOF_DIR/process.txt"

adb exec-out screencap -p > "$PROOF_DIR/screenshot.png"
test -s "$PROOF_DIR/screenshot.png"
