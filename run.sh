#!/bin/bash
# Development run script - executes the application via Gradle (JVM mode)

# Properly escape arguments for Gradle --args
ARGS=""
for arg in "$@"; do
    # Escape any existing quotes and wrap in quotes
    ARGS="$ARGS \"${arg//\"/\\\"}\""
done

./gradlew run --args="$ARGS" --quiet --console=plain
