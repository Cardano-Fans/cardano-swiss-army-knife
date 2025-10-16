# csak - Cardano Swiss Army Knife

A command-line tool for Cardano utilities built with JDK 24, Kotlin, and GraalVM.

## Features

- **Cardano Client Library Integration**: Full integration with cardano-client-lib 0.7.0
- **HD Wallet Management**: Generate and restore HD wallets with BIP32/BIP39/CIP-1852 derivation paths
- **Key Management**: Convert between private and public keys, support for both hex and CBOR formats
- **Cryptographic Operations**: Blake2b hashing (160, 224, 256-bit) and CIP-30 signature verification
- **Time & Epoch Conversions**: Convert between epochs, slots, and UTC time for all networks
- **Blockchain Information**: View Cardano eras, hard forks, and network genesis data
- **Utility Functions**: String/hex conversions and more
- **JDK 24 + Kotlin 2.2.0**: Modern language features and performance
- **GraalVM Native Image**: Fast startup, low memory footprint, single executable
- **PicoCLI**: Elegant command-line interface with help and auto-completion
- **Docker Support**: Both JVM and native image containers
- **CI/CD**: Automated builds and releases via GitHub Actions

## Commands

### Wallet & Key Management
- `hd-wallet-generate` - Generate HD wallets with mnemonic and derivation paths (CIP-1852)
- `hd-wallet-restore` - Restore and validate HD wallets from 15 or 24-word mnemonics
- `private-to-public-key` - Extract public key and address from private key (hex or CBOR format)

### Cryptographic Operations
- `blake2b-hash` - Calculate Blake2b hashes (160/224/256-bit) from hex input
- `cip30-sign` - Sign data using CIP-30 standard (wallet message signing)
- `cip30-verify` - Verify and parse CIP-30 data signatures (wallet message signing)

### Transaction Operations
- `tx-hash` - Calculate transaction hash from transaction CBOR bytes

### Time & Epoch Conversions
- `conversion-epoch-to-time` - Convert epoch number to UTC time (with start/end)
- `conversion-time-to-epoch` - Convert UTC time to epoch number
- `conversion-slot-to-time` - Convert slot number to UTC time (with epoch context)
- `conversion-time-to-slot` - Convert UTC time to slot number
- `conversion-slot-to-epoch` - Convert slot number to epoch number
- `conversion-epoch-to-slot` - Convert epoch number to slot number

### Blockchain Information
- `cardano-eras` - Display Cardano eras and their transitions
- `cardano-hardforks` - Display hard fork events (including intra-era forks)

### Utilities
- `util-string-to-hex` - Convert UTF-8 strings to hexadecimal format
- `util-hex-to-string` - Convert hexadecimal to UTF-8 strings
- `util-string-to-base64` - Convert UTF-8 strings to Base64 format
- `util-base64-to-string` - Convert Base64 to UTF-8 strings

For detailed usage examples and workflows, see [USAGE.md](USAGE.md).

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

### Quick Examples

```bash
# Generate a testnet HD wallet (single account)
./csak hd-wallet-generate --network testnet

# Generate mainnet wallet with 5 derivation paths
./csak hd-wallet-generate --network mainnet --count 5

# Convert private key to public key (CBOR format)
./csak private-to-public-key 5840... --network testnet

# Convert private key to public key (plain hex format)
./csak private-to-public-key <64-byte-hex> --format hex --network mainnet

# Calculate Blake2b hashes
./csak blake2b-hash 48656c6c6f20576f726c64

# Calculate transaction hash from CBOR
./csak tx-hash 84a8008482582003c5d1951fa6e1aa...

# Convert string to hex
./csak util-string-to-hex "Hello Cardano"

# Convert string to Base64
./csak util-string-to-base64 "Hello Cardano"
```

See [USAGE.md](USAGE.md) for comprehensive examples and workflows.

### Development

Run in JVM mode for fast iteration:
```bash
./gradlew run --args="--help"
./gradlew run --args="hd-wallet-generate -n testnet"
./gradlew run --args="private-to-public-key <key> -f hex"
```

Or use the convenience script:
```bash
./run.sh --help
./run.sh hd-wallet-generate --network testnet
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
│   ├── Main.kt                      # Main CLI entry point
│   ├── HdWalletGenerateCommand.kt   # HD wallet generation
│   ├── PrivateToPublicKeyCommand.kt # Key conversion
│   ├── Blake2bHashCommand.kt        # Hashing utilities
│   └── StringToHexCommand.kt        # String conversion
├── build.gradle.kts                 # Gradle build configuration
├── settings.gradle.kts              # Gradle settings
├── gradle.properties                # Gradle properties
├── native-compile.sh                # Native image build script
├── run.sh                           # Development run script
├── Dockerfile.native                # Native image Docker build
├── Dockerfile.jvm                   # JVM Docker build
├── .github/workflows/build.yml      # CI/CD workflow
├── README.md                        # This file
└── USAGE.md                         # Detailed usage guide
```

## Technology Stack

- **JDK**: 24
- **Kotlin**: 2.2.0
- **Cardano Client Library**: 0.7.0 (bloxbean)
- **GraalVM Native Image**: 0.10.4
- **PicoCLI**: 4.7.7
- **SLF4J**: 2.0.16

## Use Cases

### Wallet Management
- Generate HD wallets for production or testing
- Derive multiple accounts from a single mnemonic
- Understand Cardano's CIP-1852 derivation paths

### Key Operations
- Convert between key formats (hex/CBOR)
- Derive public keys from private keys
- Generate Cardano addresses from keys

### Cryptographic Operations
- Hash data with Blake2b (required for many Cardano operations)
- Prepare metadata for on-chain storage
- Verify hashes and addresses

### Development & Testing
- Quick wallet generation for testnet development
- Script-friendly output for automation
- Educational tool for understanding HD wallets

## Security Considerations

- **Never share private keys or mnemonics** - Anyone with access can control your funds
- **Use testnet for testing** - Always test with `--network testnet` before mainnet
- **Secure storage** - Store mnemonics in encrypted vaults or hardware wallets
- **Verify addresses** - Always verify generated addresses before sending funds

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Setup

1. Install JDK 24 (GraalVM recommended for native builds)
2. Clone the repository
3. Run `./gradlew build` to verify setup
4. Make changes and test with `./gradlew run --args="..."`
5. Submit PR with clear description

## License

Apache License 2.0 - see [LICENSE](LICENSE) for details.

## Resources

- [Cardano Client Library](https://github.com/bloxbean/cardano-client-lib) - Java library for Cardano
- [CIP-1852](https://cips.cardano.org/cips/cip1852/) - HD Wallets for Cardano
- [BIP32](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki) - Hierarchical Deterministic Wallets
- [BIP39](https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki) - Mnemonic Code
- [Blake2b](https://www.blake2.net/) - Cryptographic hash function
