# Usage Guide - Cardano Swiss Army Knife

This guide provides detailed usage examples for all `csak` commands.

## Table of Contents

- [Wallet & Key Management](#wallet--key-management)
  - [HD Wallet Generation](#hd-wallet-generation)
  - [HD Wallet Restore](#hd-wallet-restore)
  - [Private to Public Key Conversion](#private-to-public-key-conversion)
- [Cryptographic Operations](#cryptographic-operations)
  - [Blake2b Hashing](#blake2b-hashing)
  - [CIP-30 Data Signing](#cip-30-data-signing)
  - [CIP-30 Signature Verification](#cip-30-signature-verification)
- [Transaction Operations](#transaction-operations)
  - [Transaction Hash Calculation](#transaction-hash-calculation)
  - [Transaction Decoding](#transaction-decoding)
- [CBOR & PlutusData Operations](#cbor--plutusdata-operations)
  - [CBOR to JSON Conversion](#cbor-to-json-conversion)
  - [Datum to JSON Conversion](#datum-to-json-conversion)
- [Time & Epoch Conversions](#time--epoch-conversions)
  - [Epoch to Time Conversion](#epoch-to-time-conversion)
  - [Time to Epoch Conversion](#time-to-epoch-conversion)
  - [Slot to Time Conversion](#slot-to-time-conversion)
  - [Time to Slot Conversion](#time-to-slot-conversion)
  - [Slot to Epoch Conversion](#slot-to-epoch-conversion)
  - [Epoch to Slot Conversion](#epoch-to-slot-conversion)
- [Blockchain Information](#blockchain-information)
  - [Cardano Eras](#cardano-eras)
  - [Cardano Hard Forks](#cardano-hard-forks)
- [Utility Commands](#utility-commands)
  - [String to Hex Conversion](#string-to-hex-conversion)
  - [Hex to String Conversion](#hex-to-string-conversion)
  - [String to Base64 Conversion](#string-to-base64-conversion)
  - [Base64 to String Conversion](#base64-to-string-conversion)
- [Common Workflows](#common-workflows)

---

## Wallet & Key Management

### HD Wallet Generation

Generate hierarchical deterministic (HD) wallets following BIP32/BIP39/CIP-1852 standards.

#### Command

```bash
csak hd-wallet-generate [OPTIONS]
```

##### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, or `preview`
- `-c, --count <COUNT>` - Number of derivation paths to generate (default: 1)
- `-h, --help` - Show help message
- `-V, --version` - Show version

##### Examples

##### Generate a single wallet (default)

```bash
csak hd-wallet-generate --network preprod
```

Output includes:
- 24-word mnemonic phrase
- Derivation paths (CIP-1852): `m/1852'/1815'/0'/0/0` (payment), `m/1852'/1815'/0'/2/0` (staking)
- Base address (Bech32)
- Stake address (Bech32)
- Private key (hex and CBOR hex)
- Public key (hex and CBOR hex)

##### Generate multiple derivation paths

```bash
# Generate 5 accounts
csak hd-wallet-generate --network mainnet --count 5

# Short form
csak hd-wallet-generate -n preprod -c 10
```

This generates accounts at indices 0-4 (or 0-9), each with their own:
- Unique derivation path
- Unique addresses
- Unique key pairs

##### Output Example

```
================================================================================
Cardano HD Wallet Generated
================================================================================

Network: PREPROD

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

#### Security Notes

- **NEVER share your mnemonic or private keys**
- Store the mnemonic in a secure location (preferably offline)
- Anyone with access to the mnemonic can control all derived accounts
- The mnemonic is the master key for the entire HD wallet

---

### HD Wallet Restore

Restore and validate HD wallets from existing mnemonic phrases (15 or 24 words).

#### Command

```bash
csak hd-wallet-restore [OPTIONS] <MNEMONIC_WORDS>
```

##### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, or `preview`
- `-i, --index <INDEX>` - Account index to restore (default: 0)
- `-h, --help` - Show help message

##### Examples

##### Restore a 24-word wallet

```bash
csak hd-wallet-restore --network preprod word1 word2 word3 ... word24
```

##### Restore a 15-word wallet

```bash
csak hd-wallet-restore --network mainnet word1 word2 word3 ... word15
```

##### Restore a specific account index

```bash
# Restore account at index 5
csak hd-wallet-restore -n preprod -i 5 word1 word2 ... word24
```

##### Output Example

```
================================================================================
HD Wallet Restored
================================================================================

Network: PREPROD
Mnemonic Word Count: 24
Account Index: 0

Mnemonic Validation:
--------------------------------------------------------------------------------
✓ Mnemonic is VALID (checksum verified)

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
================================================================================
```

##### Use Cases

- Restore wallets from backup mnemonics
- Validate mnemonic checksums
- Import wallets from other tools or wallets
- Recover access to funds with saved mnemonics
- Verify mnemonic phrase integrity

#### Error Handling

If the mnemonic is invalid:

```
Error: Invalid mnemonic phrase
Reason: Checksum validation failed
```

Common issues:
- Wrong word count (must be 15 or 24)
- Misspelled words (not in BIP39 wordlist)
- Wrong word order
- Invalid checksum

---

### Private to Public Key Conversion

Extract public key and address from a private key.

#### Command

```bash
csak private-to-public-key [OPTIONS] <PRIVATE_KEY>
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, or `preview`
- `-f, --format <FORMAT>` - Input format: `cbor` (default) or `hex`
- `-h, --help` - Show help message

#### Examples

##### Using CBOR format (default)

```bash
# CBOR hex starts with 5840 (64-byte private key)
csak private-to-public-key 5840884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed346280c44b175e1687b4beb270e89d60616b1d242c20cf96e6695791a7572fb71d5683841 --network preprod
```

##### Using plain hex format

```bash
# Plain 64-byte hex (128 characters)
csak private-to-public-key 884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed346280c44b175e1687b4beb270e89d60616b1d242c20cf96e6695791a7572fb71d5683841 --format hex --network preprod
```

##### Short form

```bash
csak private-to-public-key <key> -f hex -n preprod
```

##### Output Example

```
================================================================================
Public Key Extracted from Private Key
================================================================================

Network: PREPROD
Input Format: CBOR

Private Key (CBOR hex):
5840884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed3...

Public Key (hex):
1a9f48df8c097ec07215e5527227d4842a01e434b74982f3582b1b272b2fe506

Address (Bech32):
addr_test1vz0c54svgvp6uj5uu5jx79jytzethmuk0ddtgt07dzr28zcgl9lt0

================================================================================
```

#### Use Cases

- Derive public information from stored private keys
- Verify key pairs
- Generate addresses from existing keys
- Import keys from other wallets

---

## Cryptographic Operations

### Blake2b Hashing

Calculate Blake2b hashes (160, 224, and 256-bit) from hex input.

#### Command

```bash
csak blake2b-hash <HEX_INPUT>
```

#### Options

- `-h, --help` - Show help message

#### Examples

##### Hash a hex string

```bash
# Example: "Hello World" in hex
csak blake2b-hash 48656c6c6f20576f726c64
```

##### Hash a public key

```bash
csak blake2b-hash 1a9f48df8c097ec07215e5527227d4842a01e434b74982f3582b1b272b2fe506
```

##### Output Example

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

#### Use Cases

- Compute address hashes
- Generate payment credential hashes
- Verify data integrity
- Create script hashes
- General cryptographic hashing

---

### CIP-30 Data Signing

Sign data using the CIP-30 standard for wallet message signing. Supports both software wallet (full payload) and hardware wallet (hashed payload) modes.

#### Command

```bash
csak cip30-sign [OPTIONS] <MESSAGE> <PRIVATE_KEY>
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `--hashed` - Hash the payload before signing (for hardware wallets like Ledger/Trezor)
- `-a, --address <ADDRESS>` - Address to use for signing (optional, derived from private key if not provided)
- `-h, --help` - Show help message

#### Parameters

- `<MESSAGE>` - Message to sign (UTF-8 string)
- `<PRIVATE_KEY>` - Private key in hex format (32 or 64 bytes)

#### Examples

##### Sign with software wallet mode (full payload)

```bash
# Sign a message with a private key
csak cip30-sign "Hello Cardano" 884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed346280c44b175e1687b4beb270e89d60616b1d242c20cf96e6695791a7572fb71d5683841 --network preprod
```

##### Sign with hardware wallet mode (hashed payload)

```bash
# Sign with hashed payload for hardware wallet compatibility
csak cip30-sign "Hello Cardano" 884728da33f13706de4d85fd86d3a3e8bcaa1d71547be1a7b0fb9ed346280c44b175e1687b4beb270e89d60616b1d242c20cf96e6695791a7572fb71d5683841 --network preprod --hashed
```

##### Sign with specific address

```bash
# Use a specific address for signing
csak cip30-sign "Hello Cardano" <private_key> -a addr_test1qp5umtjq9gg9gw63f50gl3g4m7xk26dl94z2zdtpc7trjp... --network preprod
```

##### Output Example

```
================================================================================
CIP-30 Data Signature
================================================================================

Message:
Hello Cardano

Message (hex):
48656c6c6f2043617264616e6f

Address:
addr_test1qp5umtjq9gg9gw63f50gl3g4m7xk26dl94z2zdtpc7trjp...

Signature Mode:
Full payload (Software Wallet)

CIP-30 Signature (hex):
845846a201276761646472657373583900327d065c4c135860b9ac6a758c9ef032100...

CIP-30 Key (hex):
a4010103272006215820097c8507b71063f99e38147f09eacf76f25576a2ddfac2f...

Network: PREPROD

================================================================================

Usage:
To verify this signature, use:
  csak cip30-verify 845846a201... a4010103...

================================================================================
```

#### Use Cases

- Sign messages for wallet authentication
- Create proofs of wallet ownership
- Sign data for DApp interactions
- Generate signatures for off-chain verification
- Test CIP-30 wallet implementations

#### Signing Modes

**Software Wallet Mode (default):**
- Full message payload is included in the signature
- Compatible with browser wallets (Nami, Eternl, etc.)
- Message is directly visible in the signature

**Hardware Wallet Mode (`--hashed`):**
- Message is hashed (Blake2b-224) before signing
- Required for Ledger and Trezor hardware wallets
- More compact signature format
- Hardware wallet devices show hash instead of full message

#### Security Notes

- Never share your private keys
- Verify the address matches your wallet before signing
- Hardware wallet mode is recommended for production use
- Always verify signatures after creation

---

### CIP-30 Signature Verification

Verify and parse CIP-30 data signatures from Cardano wallets (software and hardware wallets).

#### Command

```bash
csak cip30-verify [OPTIONS] <CIP30_DATA_SIGNATURE> [PUBLIC_KEY]
```

#### Options

- `-h, --help` - Show help message

#### Parameters

- `<CIP30_DATA_SIGNATURE>` - The CIP-30 signature hex string (required)
- `[PUBLIC_KEY]` - Optional public key hex string for verification

#### Examples

##### Verify a software wallet signature

```bash
csak cip30-verify 845846a2012...
```

##### Verify with explicit public key

```bash
csak cip30-verify 845846a2012... a401022001215820abcd...
```

##### Output Example

```
================================================================================
CIP-30 Signature Verification
================================================================================

Signature Valid: ✓ YES

Address (from signature):
addr_test1qz8f8w7nzqaypv00tpc96mvn5...

Message (original):
Hello Cardano!

Message (hex):
48656c6c6f20436172646e6f21

Signature Type:
NO (Software Wallet - direct message signing)

Public Key (Ed25519):
a401022001215820abcd1234...

Signature (Ed25519):
58405820def456789abcdef0...

COSE Payload:
a266686173686564f4...

================================================================================
```

##### Hardware Wallet Signature Example

When verifying a signature from a hardware wallet (Ledger, Trezor):

```
Signature Type:
✓ YES (Hardware Wallet - message was hashed before signing)
```

#### Use Cases

- Verify wallet message signatures
- Authenticate wallet ownership
- Validate signed data from DApps
- Detect hardware wallet signatures
- Parse CIP-30 COSE structures
- Implement wallet connect workflows

#### CIP-30 Background

CIP-30 defines the standard for wallet message signing on Cardano. Key features:

- **COSE Format**: Signatures use CBOR Object Signing and Encryption
- **Hardware Wallet Detection**: The `isHashed` flag indicates if message was pre-hashed
- **Ed25519 Signatures**: Standard Cardano signature algorithm
- **Address Binding**: Signatures include the signing address

---

## Transaction Operations

### Transaction Hash Calculation

Calculate the transaction hash from transaction CBOR bytes. This is useful for verifying transaction IDs or calculating hashes for transactions obtained from various sources.

#### Command

```bash
csak tx-hash <TX_CBOR>
```

#### Options

- `-h, --help` - Show help message

#### Parameters

- `<TX_CBOR>` - Transaction CBOR bytes in hexadecimal format (whitespace is automatically removed)

#### Examples

##### Calculate transaction hash from CBOR

```bash
# Example with a real transaction CBOR
csak tx-hash 84a8008482582003c5d1951fa6e1aa9f6d41dcee053d9e031487f1156eabb1ebb99166cd80394a03...
```

##### With whitespace (automatically cleaned)

```bash
# Whitespace in the CBOR string is automatically removed
csak tx-hash "84a800 8482 5820 03c5..."
```

#### Output Example

```
================================================================================
Transaction Hash Calculation
================================================================================

Transaction CBOR (hex):
84a8008482582003c5d1951fa6e1aa9f6d41dcee053d9e031487f1156eabb1...

Transaction CBOR Size: 2847 bytes

Transaction Hash:
ae2210f94144e2f650adeecfde1c7df0131925cf865a858d9ee2137296f3e334

================================================================================
```

#### Use Cases

- Verify transaction IDs after transaction construction
- Calculate tx hash for transactions from blockchain explorers
- Debug transaction building issues
- Verify transaction identity before submission
- Calculate transaction hashes for unsigned transactions
- Validate transaction CBOR from external sources

#### Technical Notes

The transaction hash is calculated by:
1. Extracting the transaction body from the CBOR structure
2. Computing Blake2b-256 hash of the transaction body
3. Returning the hash as a hexadecimal string

This follows the Cardano ledger specification for transaction identification.

#### Error Handling

If the CBOR is invalid:

```
Error: Failed to calculate transaction hash
Reason: Invalid transaction CBOR structure
```

Common issues:
- Malformed CBOR hex string
- Non-hex characters in input
- Invalid transaction structure
- Incomplete transaction data

---

### Transaction Decoding

Deserialize transaction CBOR bytes and display the transaction structure as formatted JSON. This is essential for inspecting transaction details, debugging, and understanding transaction structure.

#### Command

```bash
csak tx-decode <TX_CBOR>
```

#### Options

- `-h, --help` - Show help message

#### Parameters

- `<TX_CBOR>` - Transaction CBOR bytes in hexadecimal format (whitespace is automatically removed)

#### Examples

##### Decode a transaction from CBOR

```bash
# Example with a simple transaction
csak tx-decode 84a40081825820d82e82776b53c1d6dfd1fcd3edd0df5ab72b35650044f155ac9f45e045f0adec00018182581d6148d28ab9e8b9cdbe79b63ce5c6f5eb60a42c9869d11e28d9bb0a22a01a000f4240021a0002a389031a04d8b5d0a100818258203f89ee3d0e7db26dcdfe5c6809dd0ce35e1b5bf7d8d1b3d6e8889ab4a0e32e8258401e4ba0b4ee65b332efecb32b8d5c7c5cb59c6c5fe29a2f33f735654d1b28d6efc1a53feb6b27a64b7f0f8a26154c2e9f41db45f06c12cf8a6a8a2d1df3e950eef5f6
```

##### With whitespace (automatically cleaned)

```bash
# Whitespace in the CBOR string is automatically removed
csak tx-decode "84a400 8182 5820 d82e..."
```

#### Output Example

```
================================================================================
Transaction Decoded from CBOR
================================================================================

Transaction CBOR Size: 197 bytes

Transaction JSON:
{
  "era" : null,
  "body" : {
    "inputs" : [ {
      "transactionId" : "d82e82776b53c1d6dfd1fcd3edd0df5ab72b35650044f155ac9f45e045f0adec",
      "index" : 0
    } ],
    "outputs" : [ {
      "address" : "addr1v9yd9z4eazuum0nekc7wt3h4ads2gtycd8g3u2xehv9z9gq5heenf",
      "value" : {
        "coin" : 1000000,
        "multiAssets" : [ ],
        "zero" : false,
        "positive" : true
      },
      "datumHash" : null,
      "inlineDatum" : null,
      "scriptRef" : null
    } ],
    "fee" : 172937,
    "ttl" : 81311184,
    "certs" : [ ],
    "withdrawals" : [ ],
    "update" : null,
    "auxiliaryDataHash" : null,
    "validityStartInterval" : 0,
    "mint" : [ ],
    "scriptDataHash" : null,
    "collateral" : [ ],
    "requiredSigners" : [ ],
    "networkId" : null,
    "collateralReturn" : null,
    "totalCollateral" : null,
    "referenceInputs" : [ ],
    "votingProcedures" : null,
    "proposalProcedures" : null,
    "currentTreasuryValue" : null,
    "donation" : null
  },
  "witnessSet" : {
    "vkeyWitnesses" : [ {
      "vkey" : "P4nuPQ59sm3N/lxoCd0M414bW/fY0bPW6IiatKDjLoI=",
      "signature" : "HkugtO5lszLv7LMrjVx8XLWcbF/imi8z9zVlTRso1u/BpT/rayemS38PiiYVTC6fQdtF8GwSz4pqii0d8+lQ7g=="
    } ],
    "nativeScripts" : null,
    "bootstrapWitnesses" : null,
    "plutusV1Scripts" : null,
    "plutusDataList" : null,
    "redeemers" : null,
    "plutusV2Scripts" : null,
    "plutusV3Scripts" : null
  },
  "auxiliaryData" : null,
  "valid" : true
}

================================================================================
```

#### JSON Structure

The decoded JSON contains the complete transaction structure:

**Transaction Level:**
- `era`: The ledger era (Byron, Shelley, Allegra, Mary, Alonzo, Babbage, Conway)
- `body`: Transaction body with all transaction details
- `witnessSet`: Signatures and other witnesses
- `auxiliaryData`: Metadata and scripts
- `valid`: Transaction validity flag (true for valid transactions)

**Transaction Body:**
- `inputs`: Transaction inputs (UTXOs being spent)
- `outputs`: Transaction outputs (new UTXOs created)
- `fee`: Transaction fee in lovelace
- `ttl`: Time-to-live (absolute slot number)
- `certs`: Stake pool certificates and delegation certificates
- `withdrawals`: Staking reward withdrawals
- `mint`: Native tokens minted/burned
- `scriptDataHash`: Hash of Plutus script data
- `collateral`: Collateral inputs for Plutus scripts
- `requiredSigners`: Required signatories for scripts
- `referenceInputs`: Reference inputs (Babbage+)
- `votingProcedures`: Governance votes (Conway+)
- `proposalProcedures`: Governance proposals (Conway+)

**Witness Set:**
- `vkeyWitnesses`: Verification key witnesses (signatures)
- `nativeScripts`: Native scripts
- `bootstrapWitnesses`: Byron-era bootstrap witnesses
- `plutusV1Scripts`: Plutus V1 scripts
- `plutusV2Scripts`: Plutus V2 scripts (Babbage+)
- `plutusV3Scripts`: Plutus V3 scripts (Conway+)
- `plutusDataList`: Plutus data (datums and redeemers)
- `redeemers`: Plutus script redeemers

#### Use Cases

- **Transaction Inspection**: Examine transaction details before or after submission
- **Debugging**: Debug transaction construction issues
- **Analysis**: Analyze transaction structure for auditing or research
- **Verification**: Verify transaction contents match expectations
- **Learning**: Understand Cardano transaction structure
- **Integration**: Parse transactions from blockchain explorers
- **Development**: Test transaction serialization/deserialization
- **Plutus Scripts**: Inspect script execution details
- **Metadata Analysis**: Extract and analyze transaction metadata
- **Governance**: Inspect voting and proposal procedures (Conway era)

#### Working with Different Eras

The decoder automatically handles transactions from all Cardano eras:

- **Byron**: Legacy bootstrap addresses and witnesses
- **Shelley**: Stake pool operations and delegation
- **Allegra**: Token locking (timelock scripts)
- **Mary**: Multi-asset support (native tokens)
- **Alonzo**: Plutus V1 smart contracts
- **Babbage**: Plutus V2, reference inputs, inline datums
- **Conway**: Governance (voting, proposals, constitution)

#### Decoding Complex Transactions

##### Smart Contract Transactions (Plutus)

```bash
# Decode a Plutus script transaction
csak tx-decode <plutus_tx_cbor>
```

The output will include:
- `plutusDataList`: Datums and redeemers
- `scriptDataHash`: Hash of script data
- `collateral`: Collateral inputs
- `requiredSigners`: Script signatories

##### Multi-Asset Transactions

```bash
# Decode a native token transaction
csak tx-decode <token_tx_cbor>
```

The output will show:
- `multiAssets`: Token policies and amounts
- `mint`: Minting/burning operations

##### Metadata Transactions

```bash
# Decode a transaction with metadata
csak tx-decode <metadata_tx_cbor>
```

The `auxiliaryData` field will contain metadata details.

#### Piping with tx-hash

You can combine tx-decode with other commands:

```bash
# Get both transaction hash and decoded JSON
csak tx-hash <tx_cbor>
csak tx-decode <tx_cbor>
```

#### Error Handling

If the CBOR is invalid:

```
Error: Failed to deserialize transaction
Reason: CBOR deserialization failed
```

Common issues:
- Malformed CBOR hex string
- Non-hex characters in input
- Invalid transaction structure
- Incomplete transaction data
- Unsupported era format

#### Technical Notes

The transaction decoder:
1. Parses the CBOR structure using cardano-client-lib
2. Deserializes into a Transaction object
3. Converts to JSON using Jackson serialization
4. Pretty-prints the output for readability

Binary data (signatures, keys, hashes) is encoded as Base64 in the JSON output for compactness.

---

## CBOR & PlutusData Operations

### CBOR to JSON Conversion

Convert general CBOR (Concise Binary Object Representation) hex bytes to JSON format. This is useful for inspecting any CBOR-encoded data structures used in Cardano.

#### Command

```bash
csak cbor-to-json [OPTIONS] <CBOR_HEX>
```

#### Options

- `--pretty` - Pretty print the JSON output (default: true)
- `-h, --help` - Show help message

#### Parameters

- `<CBOR_HEX>` - CBOR data in hexadecimal format (whitespace is automatically removed)

#### Examples

##### Convert CBOR map to JSON

```bash
# Simple CBOR map: {a: 1, b: [2, 3]}
csak cbor-to-json "a26161016162820203"
```

##### With whitespace (automatically cleaned)

```bash
csak cbor-to-json "a2 6161 01 6162 820203"
```

#### Output Example

```
================================================================================
CBOR to JSON Conversion
================================================================================

CBOR Size: 9 bytes

{
  "a" : 1,
  "b" : [ 2, 3 ]
}

================================================================================
```

#### Use Cases

- **Metadata Inspection**: Decode transaction metadata from CBOR
- **Debugging**: Inspect CBOR structures for debugging
- **Data Analysis**: Convert blockchain CBOR data to readable JSON
- **Integration**: Parse CBOR from external sources
- **Development**: Test CBOR serialization/deserialization

#### Technical Notes

This command uses the `MetadataToJsonNoSchemaConverter` from cardano-client-lib to convert CBOR structures to JSON. It handles:
- Maps (key-value pairs)
- Arrays (lists)
- Numbers (integers)
- Byte strings (displayed as hex with 0x prefix)
- Unicode strings
- Booleans and null values

---

### Datum to JSON Conversion

Convert Cardano PlutusData (smart contract datum) from CBOR format to JSON. PlutusData is the data structure used in Plutus smart contracts on Cardano.

#### Command

```bash
csak datum-to-json [OPTIONS] <DATUM_HEX>
```

#### Options

- `-h, --help` - Show help message

#### Parameters

- `<DATUM_HEX>` - Datum CBOR data in hexadecimal format (PlutusData serialized)

#### Examples

##### Example 1: Simple Constructor

```bash
# Constr 0 [1, 2]
csak datum-to-json "d8799f0102ff"
```

##### Example 2: Bytes

```bash
# Bytes 0xdeadbeef
csak datum-to-json "44deadbeef"
```

##### Example 3: List of Integers

```bash
# List [1, 2, 3]
csak datum-to-json "9f010203ff"
```

##### Example 4: Big Integer

```bash
# Int 1000000000000
csak datum-to-json "1b000000e8d4a51000"
```

##### Example 5: Complex Nested Datum

```bash
# Constr 1 [bytes, int, constructor, list, map]
csak datum-to-json "d87a9f581cc2ff616e11299d9094ce0a7eb5b7284b705147a822f4ffbd471f971a1b0000017e9874d2a0d8668218829f187b44313233349f040506ffa2014131024132ffff"
```

#### Output Example

```
================================================================================
Datum (PlutusData) to JSON Conversion
================================================================================

Datum CBOR Size: 6 bytes

Datum Hash:
3b6ae2922d032a66a7f4bfa8a238c15493e97cbe32e1bbdf19605720b1e5099a

PlutusData JSON:
{
  "constructor" : 0,
  "fields" : [ {
    "int" : 1
  }, {
    "int" : 2
  } ]
}

================================================================================
```

#### PlutusData Types

The JSON output follows Cardano's ScriptDataJsonSchema and can represent:

**Constructor (Tagged Union):**
```json
{
  "constructor": 0,
  "fields": [...]
}
```

**Integer (Arbitrary Precision):**
```json
{
  "int": 42
}
```

**Bytes (Hex Encoded):**
```json
{
  "bytes": "deadbeef"
}
```

**List (Array):**
```json
{
  "list": [...]
}
```

**Map (Key-Value Pairs):**
```json
{
  "map": [
    {"k": {...}, "v": {...}},
    ...
  ]
}
```

#### Use Cases

- **Smart Contract Development**: Inspect datum structures for Plutus contracts
- **Debugging**: Debug smart contract data on-chain
- **UTXO Analysis**: Analyze datum attached to UTXOs
- **Integration**: Parse datum from blockchain explorers
- **Testing**: Verify datum serialization in tests
- **Hash Calculation**: Calculate datum hash for script validation

#### Datum Hash

The datum hash is automatically calculated and displayed. The Blake2b-256 hash of the datum is used for:
- Locking UTXOs with datum hash (instead of inline datum)
- Script validation and reference
- Datum identification on-chain

#### Technical Notes

This command:
1. Deserializes PlutusData from CBOR using cardano-client-lib
2. Converts to JSON using `PlutusDataJsonConverter`
3. Optionally calculates the datum hash (Blake2b-256 of CBOR bytes)

**Important**: This command expects PlutusData CBOR format. For general CBOR, use `cbor-to-json` instead.

#### Common Datum Patterns

**NFT Metadata (CIP-68):**
```bash
# Often contains version, metadata map, and extra data
csak datum-to-json <cip68_datum_hex>
```

**Vesting Contract:**
```bash
# Typically: Constr with beneficiary, amount, unlock time
csak datum-to-json <vesting_datum_hex>
```

**Marketplace Listing:**
```bash
# Usually: Constr with seller, price, deadline, metadata
csak datum-to-json <marketplace_datum_hex>
```

---

## Time & Epoch Conversions

All conversion commands support three Cardano networks: mainnet, preprod, and preview.

### Epoch to Time Conversion

Convert a Cardano epoch number to UTC time, showing both epoch start and end times.

#### Command

```bash
csak conversion-epoch-to-time [OPTIONS] <EPOCH_NUMBER>
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `-h, --help` - Show help message

#### Parameters

- `<EPOCH_NUMBER>` - The epoch number to convert

#### Examples

##### Convert mainnet epoch

```bash
csak conversion-epoch-to-time 450
```

##### Convert preprod epoch

```bash
csak conversion-epoch-to-time --network preprod 150
```

##### Output Example

```
================================================================================
Epoch to Time Conversion
================================================================================

Network: MAINNET
Epoch: 450

Start Time:
--------------------------------------------------------------------------------
2024-01-15 21:44:51
Slot: 112132800
ISO: 2024-01-15T21:44:51Z

End Time:
--------------------------------------------------------------------------------
2024-01-20 21:44:50
Slot: 112564799
ISO: 2024-01-20T21:44:50Z

Duration: 432000 slots

================================================================================
```

#### Use Cases

- Calculate epoch boundaries for stake snapshots
- Determine epoch start/end for rewards calculation
- Plan protocol parameter updates
- Schedule blockchain events by epoch

---

### Time to Epoch Conversion

Convert UTC time to a Cardano epoch number.

#### Command

```bash
csak conversion-time-to-epoch [OPTIONS] <UTC_TIME>
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `-h, --help` - Show help message

#### Parameters

- `<UTC_TIME>` - UTC time in ISO-8601 format (e.g., `2024-01-15T21:44:51Z`)

#### Examples

##### Convert time to epoch

```bash
csak conversion-time-to-epoch "2024-01-15T21:44:51Z"
```

##### With network specification

```bash
csak conversion-time-to-epoch -n preprod "2024-03-01T00:00:00Z"
```

##### Output Example

```
================================================================================
Time to Epoch Conversion
================================================================================

Network: MAINNET
Input Time: 2024-01-15T21:44:51Z

Epoch Number: 450

Epoch Start: 2024-01-15 21:44:51
Epoch End: 2024-01-20 21:44:50

================================================================================
```

#### Use Cases

- Determine which epoch a transaction occurred in
- Calculate epoch for historical events
- Find epoch boundaries for time ranges

---

### Slot to Time Conversion

Convert an absolute slot number to UTC time with full epoch context.

#### Command

```bash
csak conversion-slot-to-time [OPTIONS] <SLOT_NUMBER>
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `-h, --help` - Show help message

#### Parameters

- `<SLOT_NUMBER>` - The absolute slot number to convert

#### Examples

##### Convert mainnet slot

```bash
csak conversion-slot-to-time 112132800
```

##### Convert preview slot

```bash
csak conversion-slot-to-time -n preview 50000000
```

##### Output Example

```
================================================================================
Slot to Time Conversion
================================================================================

Network: MAINNET
Absolute Slot: 112132800

Slot Time:
--------------------------------------------------------------------------------
2024-01-15 21:44:51
ISO: 2024-01-15T21:44:51Z

Epoch Information:
--------------------------------------------------------------------------------
Epoch Number: 450

Epoch Start:
  Time: 2024-01-15 21:44:51
  Slot: 112132800

Epoch End:
  Time: 2024-01-20 21:44:50
  Slot: 112564799

Position in Epoch:
  Slot 1 of 432000

================================================================================
```

#### Use Cases

- Convert transaction slots to human-readable times
- Determine block timestamps
- Calculate time until slot deadline
- Understand slot position within epoch

---

### Time to Slot Conversion

Convert UTC time to an absolute slot number.

#### Command

```bash
csak conversion-time-to-slot [OPTIONS] <UTC_TIME>
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `-h, --help` - Show help message

#### Parameters

- `<UTC_TIME>` - UTC time in ISO-8601 format

#### Examples

```bash
csak conversion-time-to-slot "2024-01-15T21:44:51Z"
```

##### Output Example

```
================================================================================
Time to Slot Conversion
================================================================================

Network: MAINNET
Input Time: 2024-01-15T21:44:51Z

Absolute Slot: 112132800
Epoch: 450

================================================================================
```

#### Use Cases

- Calculate slot numbers for transaction scheduling
- Determine slot from block timestamp
- Convert time constraints to slot constraints

---

### Slot to Epoch Conversion

Convert an absolute slot number to an epoch number.

#### Command

```bash
csak conversion-slot-to-epoch [OPTIONS] <SLOT_NUMBER>
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `-h, --help` - Show help message

#### Parameters

- `<SLOT_NUMBER>` - The absolute slot number

#### Examples

```bash
csak conversion-slot-to-epoch 112132800
```

##### Output Example

```
================================================================================
Slot to Epoch Conversion
================================================================================

Network: MAINNET
Slot: 112132800

Epoch Number: 450

================================================================================
```

#### Use Cases

- Quick slot to epoch lookup
- Batch processing of slot data
- Epoch filtering for analytics

---

### Epoch to Slot Conversion

Convert an epoch number to the first absolute slot of that epoch.

#### Command

```bash
csak conversion-epoch-to-slot [OPTIONS] <EPOCH_NUMBER>
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `-h, --help` - Show help message

#### Parameters

- `<EPOCH_NUMBER>` - The epoch number

#### Examples

```bash
csak conversion-epoch-to-slot 450
```

##### Output Example

```
================================================================================
Epoch to Slot Conversion
================================================================================

Network: MAINNET
Epoch: 450

Start Slot: 112132800
End Slot: 112564799

Duration: 432000 slots

================================================================================
```

#### Use Cases

- Find epoch boundaries in slot numbers
- Calculate slot ranges for epochs
- Epoch-based data querying

---

## Blockchain Information

### Cardano Eras

Display comprehensive information about Cardano blockchain eras and their transitions.

#### Command

```bash
csak cardano-eras [OPTIONS]
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `-h, --help` - Show help message

#### Examples

##### View mainnet eras

```bash
csak cardano-eras
```

##### View preprod eras

```bash
csak cardano-eras --network preprod
```

##### Output Example

```
================================================================================
Cardano Eras Information
================================================================================

Network: MAINNET

Byron Era:
--------------------------------------------------------------------------------
  First Slot: 0
  Start Time: 2017-09-23 21:44:51
  Last Slot: 4492799
  End Time: 2020-07-29 21:44:51

Shelley Era:
--------------------------------------------------------------------------------
  First Slot: 4492800
  Start Time: 2020-07-29 21:44:51
  Last Slot: 16588799
  End Time: 2020-12-16 21:44:51

...

Conway Era:
--------------------------------------------------------------------------------
  First Slot: 133660800
  Start Time: 2024-09-01 21:44:51
  Last Slot: Current era (ongoing)
  End Time: N/A (current era)

Genesis Information:
--------------------------------------------------------------------------------
  Byron Start Time: 2017-09-23 21:44:51
  Shelley Start Time: 2020-07-29 21:44:51
  First Shelley Slot: 4492800
  Last Byron Slot: 4492799

Slot Duration:
--------------------------------------------------------------------------------
  Byron Slot Length: 20 seconds
  Shelley Slot Length: 1 seconds

Epoch Length:
--------------------------------------------------------------------------------
  Shelley Epoch Length: 432000 slots

================================================================================
```

#### Use Cases

- Understand Cardano's evolution timeline
- Identify which era a slot/epoch belongs to
- Study era transition points
- Educational purposes for Cardano architecture

---

### Cardano Hard Forks

Display information about Cardano hard fork events, including both inter-era and intra-era hard forks.

#### Command

```bash
csak cardano-hardforks [OPTIONS]
```

#### Options

- `-n, --network <NETWORK>` - Network type: `mainnet` (default), `preprod`, `preview`
- `-h, --help` - Show help message

#### Examples

##### View mainnet hard forks

```bash
csak cardano-hardforks
```

##### Output Example

```
================================================================================
Cardano Hard Forks
================================================================================

Network: MAINNET

Hard forks represent protocol upgrades on the Cardano blockchain.
This includes both era transitions and intra-era hard forks.

Hard Fork Timeline:
--------------------------------------------------------------------------------

Era transitions represent major hard forks:
  Byron → Shelley: Shelley HF
  Shelley → Allegra: Allegra HF
  Allegra → Mary: Mary HF
  Mary → Alonzo: Alonzo HF
  Alonzo → Babbage: Vasil HF
  Babbage → Conway: Chang HF

Known Intra-Era Hard Forks (Mainnet):
--------------------------------------------------------------------------------

Alonzo Intra-Era HF (Epoch 290 → 290):
  Date: September 12, 2021
  Description: Alonzo launch - Smart contracts enabled
  Slot: 39916800

Vasil HF (Epoch 364 → 365):
  Date: September 22, 2022
  Description: Babbage era - Plutus V2, reference inputs, inline datums
  Slot: 72316800

Valentine Intra-Era HF (Epoch 394):
  Date: February 14, 2023
  Description: SECP256k1 support, Plutus V2 enhancements
  Occurred within Babbage era

Chang HF #1 (Epoch 506 → 507):
  Date: September 1, 2024
  Description: Conway era - Voltaire governance phase begins
  Slot: 133660800

================================================================================

Note: Intra-era hard forks are protocol upgrades that occur within
      the same era without changing the ledger era type.

================================================================================
```

#### Use Cases

- Understand Cardano protocol upgrade history
- Identify when features were enabled (smart contracts, Plutus V2, etc.)
- Distinguish between era transitions and intra-era upgrades
- Plan for future hard forks
- Educational purposes for Cardano governance

#### Hard Fork Types

- **Inter-Era Hard Forks**: Transition between ledger eras (e.g., Shelley → Allegra)
- **Intra-Era Hard Forks**: Protocol upgrades within the same era (e.g., Valentine, Chang #1)

---

## Utility Commands

### String to Hex Conversion

Convert UTF-8 strings to hexadecimal format.

#### Command

```bash
csak util-string-to-hex <STRING>
```

#### Options

- `-h, --help` - Show help message

#### Examples

##### Convert simple strings

```bash
csak util-string-to-hex "Hello World"
```

##### Convert for use with blake2b-hash

```bash
csak util-string-to-hex "Cardano"
```

##### Output Example

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

#### Use Cases

- Prepare strings for hashing
- Convert metadata to hex
- Encode messages for on-chain storage
- Debug UTF-8 encoding issues

---

### Hex to String Conversion

Convert hexadecimal strings back to UTF-8 text.

#### Command

```bash
csak util-hex-to-string <HEX_STRING>
```

#### Options

- `-h, --help` - Show help message

#### Parameters

- `<HEX_STRING>` - The hexadecimal string to convert (whitespace is automatically removed)

#### Examples

##### Convert hex to string

```bash
csak util-hex-to-string 48656c6c6f20576f726c64
```

##### With whitespace (automatically cleaned)

```bash
csak util-hex-to-string "48 65 6c 6c 6f 20 57 6f 72 6c 64"
```

##### Output Example

```
================================================================================
Hex to String Conversion
================================================================================

Input Hex:
48656c6c6f20576f726c64

UTF-8 String:
Hello World

Byte Length: 11

================================================================================
```

#### Use Cases

- Decode hex-encoded metadata
- Read on-chain stored messages
- Debug hex data
- Reverse string-to-hex operations
- Parse transaction metadata

#### Error Handling

If the hex string is invalid:

```
Error: Invalid hex string. Must contain only hexadecimal characters (0-9, a-f, A-F)
```

Common issues:
- Non-hex characters in input
- Odd number of hex characters (must be even)
- Invalid UTF-8 byte sequences

---

### String to Base64 Conversion

Convert UTF-8 strings to Base64 format.

#### Command

```bash
csak util-string-to-base64 <STRING>
```

#### Options

- `-h, --help` - Show help message

#### Parameters

- `<STRING>` - The UTF-8 string to convert to Base64

#### Examples

##### Convert simple strings

```bash
csak util-string-to-base64 "Hello World"
```

##### Convert for encoding data

```bash
csak util-string-to-base64 "Cardano blockchain"
```

##### Output Example

```
================================================================================
String to Base64 Conversion
================================================================================

Input String:
Hello World

Base64 Output:
SGVsbG8gV29ybGQ=

Byte Length: 11

================================================================================
```

#### Use Cases

- Encode data for transmission
- Prepare data for JSON/XML embedding
- Encode binary data as text
- API authentication tokens
- Data serialization

---

### Base64 to String Conversion

Convert Base64 strings back to UTF-8 text.

#### Command

```bash
csak util-base64-to-string <BASE64_STRING>
```

#### Options

- `-h, --help` - Show help message

#### Parameters

- `<BASE64_STRING>` - The Base64 string to convert (whitespace is automatically removed)

#### Examples

##### Convert Base64 to string

```bash
csak util-base64-to-string SGVsbG8gV29ybGQ=
```

##### With whitespace (automatically cleaned)

```bash
csak util-base64-to-string "SGVs bG8g V29y bGQ="
```

##### Output Example

```
================================================================================
Base64 to String Conversion
================================================================================

Input Base64:
SGVsbG8gV29ybGQ=

UTF-8 String Output:
Hello World

Byte Length: 11

================================================================================
```

#### Use Cases

- Decode Base64-encoded data
- Parse API responses
- Decode authentication tokens
- Extract embedded data from JSON/XML
- Reverse Base64 encoding operations

#### Error Handling

If the Base64 string is invalid:

```
Error: Invalid Base64 string format
```

Common issues:
- Non-Base64 characters in input
- Invalid padding
- Corrupted Base64 data

---

## Common Workflows

### Workflow 1: Generate and Verify a Wallet

```bash
# Step 1: Generate a wallet
csak hd-wallet-generate -n preprod > my_wallet.txt

# Step 2: Extract the private key (CBOR format) from output

# Step 3: Verify by deriving public key
csak private-to-public-key <private_key_cbor> -n preprod

# The public key and address should match the wallet output
```

### Workflow 2: Hash a String

```bash
# Step 1: Convert string to hex
csak util-string-to-hex "MyMetadata"

# Step 2: Hash the hex output
csak blake2b-hash <hex_output_from_step1>
```

Example:
```bash
# Get hex
HEX=$(csak util-string-to-hex "Cardano" | grep "Hex Output:" -A 1 | tail -1)

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
2. **Use test networks for testing** - Always test with `--network preprod` first
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
# Generate a preprod wallet and extract the first address

OUTPUT=$(csak hd-wallet-generate -n preprod -c 1)
ADDRESS=$(echo "$OUTPUT" | grep "Base Address (Bech32):" -A 1 | tail -1 | xargs)

echo "Generated preprod address: $ADDRESS"
```

---

## Getting Help

For command-specific help:

```bash
# General help
csak --help

# Wallet & Key Management
csak hd-wallet-generate --help
csak hd-wallet-restore --help
csak private-to-public-key --help

# Cryptographic Operations
csak blake2b-hash --help
csak cip30-sign --help
csak cip30-verify --help

# Transaction Operations
csak tx-hash --help
csak tx-decode --help

# CBOR & PlutusData Operations
csak cbor-to-json --help
csak datum-to-json --help

# Time & Epoch Conversions
csak conversion-epoch-to-time --help
csak conversion-time-to-epoch --help
csak conversion-slot-to-time --help
csak conversion-time-to-slot --help
csak conversion-slot-to-epoch --help
csak conversion-epoch-to-slot --help

# Blockchain Information
csak cardano-eras --help
csak cardano-hardforks --help

# Utilities
csak util-string-to-hex --help
csak util-hex-to-string --help
csak util-string-to-base64 --help
csak util-base64-to-string --help
```

## Version Information

```bash
csak --version
```

---

## Additional Resources

### Cardano Standards

- [CIP-1852: HD Wallets for Cardano](https://cips.cardano.org/cips/cip1852/)
- [CIP-30: Cardano dApp-Wallet Web Bridge](https://cips.cardano.org/cips/cip30/)
- [Cardano Improvement Proposals (CIPs)](https://cips.cardano.org/)

### Cryptographic Standards

- [BIP32: Hierarchical Deterministic Wallets](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki)
- [BIP39: Mnemonic Code for Generating Deterministic Keys](https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki)
- [Blake2b Hashing Algorithm](https://www.blake2.net/)
- [Ed25519 Signatures](https://ed25519.cr.yp.to/)
- [COSE: CBOR Object Signing and Encryption](https://datatracker.ietf.org/doc/html/rfc8152)

### Libraries Used

- [Cardano Client Library (Java)](https://github.com/bloxbean/cardano-client-lib)
- [CIP-30 Data Signature Parser](https://github.com/cardano-foundation/cip30-data-signature-parser)
- [Cardano Conversions Library](https://github.com/cardano-foundation/cf-cardano-conversions-java)

### Cardano Documentation

- [Cardano Developer Portal](https://developers.cardano.org/)
- [Cardano Docs](https://docs.cardano.org/)
- [Cardano Ledger Specifications](https://github.com/IntersectMBO/cardano-ledger)

---

**Project**: [cardano-swiss-army-knife](https://github.com/Cardano-Fans/cardano-swiss-army-knife)

**License**: Apache 2.0
