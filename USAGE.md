# Usage Guide - Cardano Swiss Army Knife

This guide provides detailed usage examples for all `csak` commands.

## Table of Contents

- [HD Wallet Generation](#hd-wallet-generation)
- [Private to Public Key Conversion](#private-to-public-key-conversion)
- [Blake2b Hashing](#blake2b-hashing)
- [String to Hex Conversion](#string-to-hex-conversion)
- [Common Workflows](#common-workflows)

---

## HD Wallet Generation

Generate hierarchical deterministic (HD) wallets following BIP32/BIP39/CIP-1852 standards.

### Command

```bash
csak hd-wallet-generate [OPTIONS]
```

### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default) or `testnet`
- `-c, --count <COUNT>` - Number of derivation paths to generate (default: 1)
- `-h, --help` - Show help message
- `-V, --version` - Show version

### Examples

#### Generate a single wallet (default)

```bash
csak hd-wallet-generate --network testnet
```

Output includes:
- 24-word mnemonic phrase
- Derivation paths (CIP-1852): `m/1852'/1815'/0'/0/0` (payment), `m/1852'/1815'/0'/2/0` (staking)
- Base address (Bech32)
- Stake address (Bech32)
- Private key (hex and CBOR hex)
- Public key (hex and CBOR hex)

#### Generate multiple derivation paths

```bash
# Generate 5 accounts
csak hd-wallet-generate --network mainnet --count 5

# Short form
csak hd-wallet-generate -n testnet -c 10
```

This generates accounts at indices 0-4 (or 0-9), each with their own:
- Unique derivation path
- Unique addresses
- Unique key pairs

#### Output Example

```
================================================================================
Cardano HD Wallet Generated
================================================================================

Network: TESTNET

Mnemonic (24 words):
--------------------------------------------------------------------------------
word1 word2 word3 ... word24
--------------------------------------------------------------------------------

Account (index=0):
--------------------------------------------------------------------------------
  Derivation Paths (CIP-1852):
    Payment: m/1852'/1815'/0'/0/0
    Staking: m/1852'/1815'/0'/2/0

  Base Address (Bech32):
    addr_test1qp5umtjq9gg9gw63f50gl3g4m7xk26dl94z2zdtpc7trjp...

  Stake Address (Bech32):
    stake_test1ur6ndadd22cadytkzw2jqgct9sfxznds8mnx7u63l6u0j6...

  Private Key (hex):
    884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed3...
  Private Key (CBOR hex):
    5840884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb...

  Public Key (hex):
    1a9f48df8c097ec07215e5527227d4842a01e434b74982f3582b1b27...
  Public Key (CBOR hex):
    58201a9f48df8c097ec07215e5527227d4842a01e434b74982f3582b...
```

### Security Notes

- **NEVER share your mnemonic or private keys**
- Store the mnemonic in a secure location (preferably offline)
- Anyone with access to the mnemonic can control all derived accounts
- The mnemonic is the master key for the entire HD wallet

---

## Private to Public Key Conversion

Extract public key and address from a private key.

### Command

```bash
csak private-to-public-key [OPTIONS] <PRIVATE_KEY>
```

### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default) or `testnet`
- `-f, --format <FORMAT>` - Input format: `cbor` (default) or `hex`
- `-h, --help` - Show help message

### Examples

#### Using CBOR format (default)

```bash
# CBOR hex starts with 5840 (64-byte private key)
csak private-to-public-key 5840884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed346280c44b175e1687b4beb270e89d60616b1d242c20cf96e6695791a7572fb71d5683841 --network testnet
```

#### Using plain hex format

```bash
# Plain 64-byte hex (128 characters)
csak private-to-public-key 884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed346280c44b175e1687b4beb270e89d60616b1d242c20cf96e6695791a7572fb71d5683841 --format hex --network testnet
```

#### Short form

```bash
csak private-to-public-key <key> -f hex -n testnet
```

#### Output Example

```
================================================================================
Public Key Extracted from Private Key
================================================================================

Network: TESTNET
Input Format: CBOR

Private Key (CBOR hex):
5840884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed3...

Public Key (hex):
1a9f48df8c097ec07215e5527227d4842a01e434b74982f3582b1b272b2fe506

Address (Bech32):
addr_test1vz0c54svgvp6uj5uu5jx79jytzethmuk0ddtgt07dzr28zcgl9lt0

================================================================================
```

### Use Cases

- Derive public information from stored private keys
- Verify key pairs
- Generate addresses from existing keys
- Import keys from other wallets

---

## Blake2b Hashing

Calculate Blake2b hashes (160, 224, and 256-bit) from hex input.

### Command

```bash
csak blake2b-hash <HEX_INPUT>
```

### Options

- `-h, --help` - Show help message

### Examples

#### Hash a hex string

```bash
# Example: "Hello World" in hex
csak blake2b-hash 48656c6c6f20576f726c64
```

#### Hash a public key

```bash
csak blake2b-hash 1a9f48df8c097ec07215e5527227d4842a01e434b74982f3582b1b272b2fe506
```

#### Output Example

```
================================================================================
Blake2b Hash Results
================================================================================

Input (hex):
48656c6c6f20576f726c64

Blake2b-160 (20 bytes):
6a8489e6fd6e51fae12ab271ec7fc8134dd5d737

Blake2b-224 (28 bytes):
19790463ef4ad09bdb724e3a6550c640593d4870f6e192ac8147f35d

Blake2b-256 (32 bytes):
1dc01772ee0171f5f614c673e3c7fa1107a8cf727bdf5a6dadb379e93c0d1d00

================================================================================
```

### Use Cases

- Compute address hashes
- Generate payment credential hashes
- Verify data integrity
- Create script hashes
- General cryptographic hashing

---

## String to Hex Conversion

Convert UTF-8 strings to hexadecimal format.

### Command

```bash
csak string-to-hex <STRING>
```

### Options

- `-h, --help` - Show help message

### Examples

#### Convert simple strings

```bash
csak string-to-hex "Hello World"
```

#### Convert for use with blake2b-hash

```bash
csak string-to-hex "Cardano"
```

#### Output Example

```
================================================================================
String to Hex Conversion
================================================================================

Input String:
Hello World

Hex Output:
48656c6c6f20576f726c64

Byte Length: 11

================================================================================
```

### Use Cases

- Prepare strings for hashing
- Convert metadata to hex
- Encode messages for on-chain storage
- Debug UTF-8 encoding issues

---

## Common Workflows

### Workflow 1: Generate and Verify a Wallet

```bash
# Step 1: Generate a wallet
csak hd-wallet-generate -n testnet > my_wallet.txt

# Step 2: Extract the private key (CBOR format) from output

# Step 3: Verify by deriving public key
csak private-to-public-key <private_key_cbor> -n testnet

# The public key and address should match the wallet output
```

### Workflow 2: Hash a String

```bash
# Step 1: Convert string to hex
csak string-to-hex "MyMetadata"

# Step 2: Hash the hex output
csak blake2b-hash <hex_output_from_step1>
```

Example:
```bash
# Get hex
HEX=$(csak string-to-hex "Cardano" | grep "Hex Output:" -A 1 | tail -1)

# Hash it
csak blake2b-hash $HEX
```

### Workflow 3: Multi-Account HD Wallet Setup

```bash
# Generate 10 accounts for an organization
csak hd-wallet-generate -n mainnet -c 10 > organization_wallet.txt

# Each account (0-9) can be assigned to different departments
# Account 0: Treasury
# Account 1: Operations
# Account 2: Marketing
# etc.
```

### Workflow 4: Key Format Conversion

```bash
# You have a plain hex private key and need CBOR format
# The tool can handle both formats

# Plain hex -> derive public key
csak private-to-public-key <64_byte_hex> -f hex -n mainnet

# CBOR hex -> derive public key
csak private-to-public-key <5840_prefixed_hex> -f cbor -n mainnet
```

---

## Tips and Best Practices

### Security

1. **Never commit private keys or mnemonics to version control**
2. **Use testnet for testing** - Always test with `--network testnet` first
3. **Secure storage** - Store mnemonics in encrypted vaults or hardware wallets
4. **One mnemonic, multiple accounts** - Use derivation paths instead of multiple mnemonics

### Performance

1. **Default count** - Use `--count 1` (default) for faster generation
2. **Batch operations** - Generate multiple accounts at once with `-c` when needed
3. **Native binary** - Use the GraalVM native image for fastest startup

### Integration

1. **Scripting** - All commands work in shell scripts and support standard I/O
2. **Piping** - Output can be parsed with standard Unix tools (`grep`, `awk`, etc.)
3. **Exit codes** - Commands return 0 on success, 1 on error

### Example Script

```bash
#!/bin/bash
# Generate a testnet wallet and extract the first address

OUTPUT=$(csak hd-wallet-generate -n testnet -c 1)
ADDRESS=$(echo "$OUTPUT" | grep "Base Address (Bech32):" -A 1 | tail -1 | xargs)

echo "Generated testnet address: $ADDRESS"
```

---

## Getting Help

For command-specific help:

```bash
csak --help
csak hd-wallet-generate --help
csak private-to-public-key --help
csak blake2b-hash --help
csak string-to-hex --help
```

## Version Information

```bash
csak --version
```

---

## Additional Resources

- [CIP-1852: HD Wallets for Cardano](https://cips.cardano.org/cips/cip1852/)
- [BIP32: Hierarchical Deterministic Wallets](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki)
- [BIP39: Mnemonic Code for Generating Deterministic Keys](https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki)
- [Blake2b Hashing Algorithm](https://www.blake2.net/)
