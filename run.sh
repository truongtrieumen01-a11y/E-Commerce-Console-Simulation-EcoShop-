#!/bin/bash
# ==============================================================================
# ECOSHOP — SCRIPT BIÊN DỊCH VÀ KHỞI CHẠY (Linux & macOS)
# ==============================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

SRC="src"
OUT="out"

echo "=== Đang biên dịch EcoShop ==="
mkdir -p "$OUT"

find "$SRC" -name "*.java" > sources.txt
javac -encoding UTF-8 -d "$OUT" @sources.txt
rm -f sources.txt

echo "=== Biên dịch thành công ==="
echo "=== Đang khởi động EcoShop ==="
echo ""

java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "$OUT" app.EcoShopApp
