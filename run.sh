#!/bin/bash
# Development run script - executes the application via Gradle (JVM mode)

./gradlew run --args="$*" --quiet --console=plain
