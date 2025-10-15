# lil - Cardano Swiss Army Knife

A command-line tool for Cardano utilities built with JDK 24, Kotlin, and GraalVM.

## Features

- JDK 24 + Kotlin 2.2.0
- GraalVM native image support for fast, standalone executables
- PicoCLI for elegant command-line interface
- Docker support (both JVM and native image)

## Requirements

### Development
- GraalVM JDK 24 (for native image compilation)
- Gradle (included via wrapper)

### Running
- Native binary: No dependencies required
- JVM version: Java 24 runtime
- Docker: Docker or Podman

## Quick Start

### Development Mode (JVM)

Run the application via Gradle (fastest for development):

```bash
./gradlew run --args="--help"
./gradlew run --args="hello"
./gradlew run --args="hello Cardano"
```

Or use the convenience script:

```bash
./run.sh --help
./run.sh hello
./run.sh hello Cardano
```

### Native Image (Production)

Build a native executable for production use:

```bash
./native-compile.sh
```

This creates a standalone `./lil` binary with no JVM required:

```bash
./lil --help
./lil hello
./lil hello Cardano
```

### Docker

Build and run with Docker:

**Native Image (smallest size, fastest startup):**
```bash
docker build -f Dockerfile.native -t lil:native .
docker run --rm lil:native --help
docker run --rm lil:native hello Cardano
```

**JVM Version:**
```bash
docker build -f Dockerfile.jvm -t lil:jvm .
docker run --rm lil:jvm --help
docker run --rm lil:jvm hello Cardano
```

## Build Targets

- **Development**: `./gradlew run` - JVM mode, fast iteration
- **Production**: Native image via `./native-compile.sh` - Fast startup, low memory
- **Distribution**: Docker images for both JVM and native

## Project Structure

```
.
├── src/
│   └── main/
│       └── kotlin/
│           └── org/
│               └── cardano/
│                   └── lil/
│                       ├── Main.kt          # Main CLI entry point
│                       └── HelloCommand.kt  # Example command
├── build.gradle.kts                         # Gradle build configuration
├── settings.gradle.kts                      # Gradle settings
├── gradle.properties                        # Gradle properties
├── native-compile.sh                        # Native image build script
├── run.sh                                   # Development run script
├── Dockerfile.native                        # Native image Docker build
└── Dockerfile.jvm                           # JVM Docker build
```

## Adding New Commands

1. Create a new command class in `src/main/kotlin/org/cardano/lil/`:

```kotlin
package org.cardano.lil

import picocli.CommandLine.Command
import java.util.concurrent.Callable

@Command(
    name = "mycommand",
    description = ["Description of my command"]
)
class MyCommand : Callable<Int> {
    override fun call(): Int {
        println("My command executed!")
        return 0
    }
}
```

2. Register it in `Main.kt`:

```kotlin
@Command(
    // ...
    subcommands = [
        HelloCommand::class,
        MyCommand::class  // Add your command here
    ]
)
```

## Build Configuration

- **JDK Version**: 24
- **Kotlin Version**: 2.2.0
- **GraalVM Native Image**: 0.10.4
- **PicoCLI**: 4.7.7

No additional libraries are linked to keep the native image simple and fast.

## Performance

The native image provides:
- Fast startup (< 100ms)
- Low memory footprint
- No JVM required
- Single executable file

Perfect for CLI tools and automation scripts.

## License

TBD
