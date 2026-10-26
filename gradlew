#!/usr/bin/env sh
# Lightweight Gradle bootstrapper for this starter repo.
# Downloads the pinned official Gradle distribution if it is not cached.
set -eu
GRADLE_VERSION="8.13"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
DIST_DIR="$GRADLE_USER_HOME/wrapper/dists/gradle-$GRADLE_VERSION-bin/autoscroll"
GRADLE_BIN="$DIST_DIR/gradle-$GRADLE_VERSION/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$DIST_DIR"
  ZIP_FILE="$DIST_DIR/gradle-$GRADLE_VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl --fail --location --silent --show-error "$URL" --output "$ZIP_FILE"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$URL" -O "$ZIP_FILE"
  else
    echo "curl or wget is required to download Gradle." >&2
    exit 1
  fi
  if ! command -v unzip >/dev/null 2>&1; then
    echo "unzip is required to extract Gradle." >&2
    exit 1
  fi
  unzip -q -o "$ZIP_FILE" -d "$DIST_DIR"
  rm -f "$ZIP_FILE"
fi

exec "$GRADLE_BIN" "$@"
