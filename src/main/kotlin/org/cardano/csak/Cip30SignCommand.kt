package org.cardano.csak

import com.bloxbean.cardano.client.account.Account
import com.bloxbean.cardano.client.address.Address
import com.bloxbean.cardano.client.cip.cip30.CIP30DataSigner
import com.bloxbean.cardano.client.cip.cip30.DataSignError
import com.bloxbean.cardano.client.cip.cip30.DataSignature
import com.bloxbean.cardano.client.common.model.Networks
import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "cip30-sign",
    mixinStandardHelpOptions = true,
    description = ["Sign data using CIP-30 standard (wallet message signing)"]
)
class Cip30SignCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Message to sign (UTF-8 string or hex string, see --message-format)"]
    )
    private lateinit var message: String

    @Option(
        names = ["--message-format"],
        description = ["Message input format: text (UTF-8 string, default), hex (hexadecimal string)"],
        defaultValue = "text"
    )
    private var messageFormat: String = "text"

    @Parameters(
        index = "1",
        description = ["Mnemonic phrase (15 or 24 words) or private key in hex format (128 characters)"]
    )
    private lateinit var mnemonicOrKey: String

    @Option(
        names = ["-n", "--network"],
        description = ["Network type: mainnet (default), preprod, preview"],
        defaultValue = "mainnet"
    )
    private var network: String = "mainnet"

    @Option(
        names = ["--hashed"],
        description = ["Hash the payload before signing (for hardware wallets like Ledger/Trezor)"],
        defaultValue = "false"
    )
    private var hashPayload: Boolean = false

    @Option(
        names = ["-a", "--address"],
        description = ["Address to use for signing (optional, derived from private key if not provided)"]
    )
    private var addressStr: String? = null

    override fun call(): Int {
        try {
            // Parse network type
            val networkObj = when (network.lowercase()) {
                "mainnet" -> Networks.mainnet()
                "preprod" -> Networks.preprod()
                "preview" -> Networks.preview()
                else -> {
                    println("Error: Invalid network. Use 'mainnet', 'preprod', or 'preview'")
                    return 1
                }
            }

            // Determine if input is a mnemonic or private key
            val account: Account
            val address: Address

            if (mnemonicOrKey.contains(" ")) {
                // Input is a mnemonic phrase
                val words = mnemonicOrKey.trim().split("\\s+".toRegex())
                if (words.size != 15 && words.size != 24) {
                    println("Error: Mnemonic must contain 15 or 24 words")
                    println("Provided: ${words.size} words")
                    return 1
                }

                // Create account from mnemonic
                account = try {
                    Account(networkObj, mnemonicOrKey.trim())
                } catch (e: Exception) {
                    println("Error: Invalid mnemonic phrase")
                    println("Reason: ${e.message}")
                    return 1
                }

                // Get address
                address = if (addressStr != null) {
                    Address(addressStr)
                } else {
                    Address(account.baseAddress())
                }
            } else {
                // Input is a hex private key
                val cleanKey = mnemonicOrKey.replace("\\s".toRegex(), "")

                // Decode private key
                val privateKeyBytes = try {
                    HexUtil.decodeHexString(cleanKey)
                } catch (e: Exception) {
                    println("Error: Invalid private key hex format")
                    return 1
                }

                // Validate private key length (64 bytes for extended key from hd-wallet-generate)
                if (privateKeyBytes.size != 64) {
                    println("Error: Private key must be 64 bytes (128 hex characters)")
                    println("Use the 'Private Key (hex)' output from hd-wallet-generate")
                    println("Provided: ${privateKeyBytes.size} bytes")
                    return 1
                }

                // Extract the actual signing key (first 32 bytes) from the 64-byte extended key
                val signingKey = privateKeyBytes.copyOfRange(0, 32)

                // Derive public key from signing key
                val publicKeyBytes = try {
                    com.bloxbean.cardano.client.crypto.KeyGenUtil.getPublicKeyFromPrivateKey(signingKey)
                } catch (e: Exception) {
                    println("Error: Failed to derive public key from private key")
                    println("Reason: ${e.message}")
                    return 1
                }

                // Create a temporary account to get the address if not provided
                if (addressStr == null) {
                    // We need the full mnemonic to properly derive the address
                    println("Error: When using private key directly, you must provide the address with -a")
                    println("Alternative: Use the mnemonic phrase instead of private key")
                    return 1
                }

                address = Address(addressStr)

                // For signing with raw keys, we'll use the signData method directly
                // Convert message to bytes based on format
                val payload = when (messageFormat.lowercase()) {
                    "hex" -> {
                        try {
                            HexUtil.decodeHexString(message.replace("\\s".toRegex(), ""))
                        } catch (e: Exception) {
                            println("Error: Invalid hex format in message")
                            println("Reason: ${e.message}")
                            return 1
                        }
                    }
                    "text" -> message.toByteArray(Charsets.UTF_8)
                    else -> {
                        println("Error: Invalid message format. Use 'text' or 'hex'")
                        return 1
                    }
                }

                // Sign directly with keys
                val dataSignature: DataSignature = try {
                    CIP30DataSigner.INSTANCE.signData(
                        address.bytes,
                        payload,
                        signingKey,
                        publicKeyBytes,
                        hashPayload
                    )
                } catch (e: DataSignError) {
                    println("Error: Failed to sign data")
                    println("Reason: ${e.message}")
                    e.printStackTrace()
                    return 1
                }

                // Display results and return early
                println("=".repeat(80))
                println("CIP-30 Data Signature")
                println("=".repeat(80))
                println()
                println("Message Input Format: ${messageFormat.uppercase()}")
                println()
                if (messageFormat.lowercase() == "text") {
                    println("Message (text):")
                    println(message)
                    println()
                }
                println("Message (hex):")
                println(HexUtil.encodeHexString(payload))
                println()
                println("Address:")
                println(address.address)
                println()
                println("Signature Mode:")
                if (hashPayload) {
                    println("Hashed (Hardware Wallet compatible)")
                } else {
                    println("Full payload (Software Wallet)")
                }
                println()
                println("CIP-30 Signature (hex):")
                println(dataSignature.signature())
                println()
                println("CIP-30 Key (hex):")
                println(dataSignature.key())
                println()
                println("Network: ${network.uppercase()}")
                println()
                println("=".repeat(80))
                println()
                println("Usage:")
                println("To verify this signature, use:")
                println("  csak cip30-verify ${dataSignature.signature()} -k ${dataSignature.key()}")
                println()
                println("=".repeat(80))

                return 0
            }

            // Convert message to bytes based on format
            val payload = when (messageFormat.lowercase()) {
                "hex" -> {
                    try {
                        HexUtil.decodeHexString(message.replace("\\s".toRegex(), ""))
                    } catch (e: Exception) {
                        println("Error: Invalid hex format in message")
                        println("Reason: ${e.message}")
                        return 1
                    }
                }
                "text" -> message.toByteArray(Charsets.UTF_8)
                else -> {
                    println("Error: Invalid message format. Use 'text' or 'hex'")
                    return 1
                }
            }

            // Sign the data
            val dataSignature: DataSignature = try {
                CIP30DataSigner.INSTANCE.signData(
                    address.bytes,
                    payload,
                    account,
                    hashPayload
                )
            } catch (e: DataSignError) {
                println("Error: Failed to sign data")
                println("Reason: ${e.message}")
                e.printStackTrace()
                return 1
            }

            // Display results
            println("=".repeat(80))
            println("CIP-30 Data Signature")
            println("=".repeat(80))
            println()
            println("Message Input Format: ${messageFormat.uppercase()}")
            println()
            if (messageFormat.lowercase() == "text") {
                println("Message (text):")
                println(message)
                println()
            }
            println("Message (hex):")
            println(HexUtil.encodeHexString(payload))
            println()
            println("Address:")
            println(address.address)
            println()
            println("Signature Mode:")
            if (hashPayload) {
                println("Hashed (Hardware Wallet compatible)")
            } else {
                println("Full payload (Software Wallet)")
            }
            println()
            println("CIP-30 Signature (hex):")
            println(dataSignature.signature())
            println()
            println("CIP-30 Key (hex):")
            println(dataSignature.key())
            println()
            println("Network: ${network.uppercase()}")
            println()
            println("=".repeat(80))
            println()
            println("Usage:")
            println("To verify this signature, use:")
            println("  csak cip30-verify ${dataSignature.signature()} -k ${dataSignature.key()}")
            println()
            println("=".repeat(80))

            return 0
        } catch (e: Exception) {
            println("Error: ${e.message}")
            e.printStackTrace()
            return 1
        }
    }
}
