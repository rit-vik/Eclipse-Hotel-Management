#!/bin/bash
# ============================================================
# Hotel Management System — Build & Run Script
# Requires: JDK 11+ and JavaFX SDK
# ============================================================

set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
OUT_DIR="$PROJECT_DIR/out"
JAR_NAME="HotelManagementSystem.jar"

echo "=========================================="
echo "  Hotel Management System — Build Script  "
echo "=========================================="

# --- Step 1: Compile ---
echo ""
echo "[1/3] Compiling Java sources..."
mkdir -p "$OUT_DIR"

# Find JavaFX SDK (common locations)
JAVAFX_PATH=""
for path in \
    "/usr/share/openjfx/lib" \
    "/opt/javafx-sdk/lib" \
    "$HOME/javafx-sdk/lib" \
    "/usr/local/lib/javafx/lib"; do
    if [ -d "$path" ]; then
        JAVAFX_PATH="$path"
        break
    fi
done

if [ -z "$JAVAFX_PATH" ]; then
    echo ""
    echo "  JavaFX SDK not found automatically."
    echo "  Please set JAVAFX_PATH manually:"
    echo "    export JAVAFX_PATH=/path/to/javafx-sdk/lib"
    echo "  Then re-run this script."
    echo ""
    echo "  Download JavaFX SDK from:"
    echo "    https://openjfx.io"
    echo ""
    echo "  Or install on Ubuntu/Debian:"
    echo "    sudo apt install openjfx"
    echo ""
    exit 1
fi

echo "  JavaFX SDK found at: $JAVAFX_PATH"

# Find all .java files
find "$SRC_DIR" -name "*.java" > "$OUT_DIR/sources.txt"
SOURCE_COUNT=$(wc -l < "$OUT_DIR/sources.txt")
echo "  Found $SOURCE_COUNT source files."

javac \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.fxml \
    -d "$OUT_DIR/classes" \
    @"$OUT_DIR/sources.txt"

echo "  Compilation successful!"

# --- Step 2: Create JAR ---
echo ""
echo "[2/3] Creating JAR..."
mkdir -p "$OUT_DIR/classes"

# Create MANIFEST
cat > "$OUT_DIR/MANIFEST.MF" << EOF
Manifest-Version: 1.0
Main-Class: hotel.ui.HotelApp
Class-Path: .
EOF

jar cfm "$OUT_DIR/$JAR_NAME" "$OUT_DIR/MANIFEST.MF" -C "$OUT_DIR/classes" .
echo "  JAR created: $OUT_DIR/$JAR_NAME"

# --- Step 3: Run ---
echo ""
echo "[3/3] Launching application..."
echo ""
cd "$PROJECT_DIR"
java \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.fxml \
    -jar "$OUT_DIR/$JAR_NAME"
