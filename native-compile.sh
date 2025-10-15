#!/bin/bash
set -e

echo "======================================================================="
echo "GraalVM Native Image Compilation"
echo "======================================================================="
echo ""
echo "Requirements:"
echo "   - GraalVM JDK 24 installed"
echo "   - ~2GB RAM available"
echo "   - 1-3 minutes compilation time"
echo ""
echo "Starting native compilation..."
echo ""

# Compile with GraalVM
./gradlew nativeCompile

echo ""
echo "Copying native executable to root folder..."

# Copy executable to root
cp ./build/native/nativeCompile/lil ./lil

# Ensure it's executable
chmod +x ./lil

echo ""
echo "======================================================================="
echo "Native compilation successful!"
echo "======================================================================="
echo ""
echo "Executable location: ./lil"
echo ""
echo "Test it with:"
echo "  ./lil --version"
echo "  ./lil hello"
echo "  ./lil hello Cardano"
echo ""
