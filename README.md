# csak - Cardano Swiss Army Knife

A command-line tool for Cardano utilities built with JDK 24, Kotlin, and GraalVM.

## Features

- **Cardano Client Library Integration**: Full integration with cardano-client-lib 0.7.0
- **Wallet Generation**: Generate HD wallets with mnemonic phrases, addresses, and keys
- **JDK 24 + Kotlin 2.2.0**: Modern language features and performance
- **GraalVM Native Image**: Fast startup, low memory footprint, single executable
- **PicoCLI**: Elegant command-line interface with help and auto-completion
- **Docker Support**: Both JVM and native image containers
- **CI/CD**: Automated builds and releases via GitHub Actions

## Commands

- `wallet-generate` - Generate a 24-word mnemonic with Cardano wallet details (base address, stake address, private key, public key)

For detailed usage examples, see [USAGE.md](USAGE.md).

## Quick Start

### Installation

**Download Pre-built Binary** (recommended):
```bash
# Download latest release from GitHub
wget https://github.com/Cardano-Fans/cardano-swiss-army-knife/releases/latest/download/csak-linux-x64.tar.gz
tar -xzf csak-linux-x64.tar.gz
chmod +x csak
./csak --help
```

**Build from Source**:
```bash
# Clone repository
git clone git@github.com:Cardano-Fans/cardano-swiss-army-knife.git
cd cardano-swiss-army-knife

# Build native image (requires GraalVM JDK 24)
./native-compile.sh

# Or build JVM version
./gradlew build
```

### Development

Run in JVM mode for fast iteration:
```bash
./gradlew run --args="--help"
./gradlew run --args="wallet-generate"
```

Or use the convenience script:
```bash
./run.sh --help
./run.sh wallet-generate
```

## Build Targets

- **Development**: `./gradlew run` - JVM mode, fast iteration
- **Production**: `./native-compile.sh` - Native image, fast startup, low memory
- **Docker JVM**: `docker build -f Dockerfile.jvm -t csak:jvm .`
- **Docker Native**: `docker build -f Dockerfile.native -t csak:native .`

## Project Structure

```
.
├── src/main/kotlin/org/cardano/csak/
│   ├── Main.kt                  # Main CLI entry point
│   └── WalletGenerateCommand.kt # Wallet generation command
├── build.gradle.kts             # Gradle build configuration
├── settings.gradle.kts          # Gradle settings
├── gradle.properties            # Gradle properties
├── native-compile.sh            # Native image build script
├── run.sh                       # Development run script
├── Dockerfile.native            # Native image Docker build
├── Dockerfile.jvm               # JVM Docker build
└── .github/workflows/build.yml  # CI/CD workflow
```

## Technology Stack

- **JDK**: 24
- **Kotlin**: 2.2.0
- **Cardano Client Library**: 0.7.0
- **GraalVM Native Image**: 0.10.4
- **PicoCLI**: 4.7.7
- **SLF4J**: 2.0.16

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

Apache License 2.0 - see [LICENSE](LICENSE) for details.
