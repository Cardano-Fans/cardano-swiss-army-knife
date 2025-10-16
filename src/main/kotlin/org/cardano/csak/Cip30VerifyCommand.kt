package org.cardano.csak

import com.bloxbean.cardano.client.util.HexUtil
import org.cardanofoundation.cip30.CIP30Verifier
import org.cardanofoundation.cip30.AddressFormat
import org.cardanofoundation.cip30.MessageFormat
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "cip30-verify",
    mixinStandardHelpOptions = true,
    description = ["Verify and parse CIP-30 data signatures (wallet message signing)"]
)
class Cip30VerifyCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Signature data (hex string)"]
    )
    private lateinit var signature: String

    @Option(
        names = ["-k", "--key"],
        description = ["Public key (hex string) - optional if embedded in signature"]
    )
    private var publicKey: String? = null

    @Option(
        names = ["-f", "--format"],
        description = ["Message output format: text (default), hex, base64"],
        defaultValue = "text"
    )
    private var messageFormat: String = "text"

    override fun call(): Int {
        try {
            // Clean input (remove whitespace)
            val cleanSig = signature.replace("\\s".toRegex(), "")
            val cleanKey = publicKey?.replace("\\s".toRegex(), "")

            // Create verifier
            val verifier = if (cleanKey != null) {
                CIP30Verifier(cleanSig, cleanKey)
            } else {
                CIP30Verifier(cleanSig)
            }

            // Get verification result
            val result = verifier.verify()

            // Display header
            println("=".repeat(80))
            println("CIP-30 Signature Verification")
            println("=".repeat(80))
            println()

            // Validation status
            println("Signature Valid: ${if (result.isValid) "✓ YES" else "✗ NO"}")

            // Hardware wallet detection
            val hwWallet = if (result.isHashed) {
                "✓ YES (Hardware Wallet - message was hashed before signing)"
            } else {
                "NO (Software Wallet - direct message signing)"
            }
            println("Hardware Wallet Signature: $hwWallet")
            println()

            // Address
            println("Address (Bech32):")
            println("-".repeat(80))
            try {
                val addressOpt = result.getAddress(AddressFormat.TEXT)
                if (addressOpt.isPresent) {
                    println(addressOpt.get())
                } else {
                    println("No address found in signature")
                }
            } catch (e: Exception) {
                println("Error extracting address: ${e.message}")
            }
            println()

            // Message
            println("Signed Message:")
            println("-".repeat(80))
            try {
                // For hardware wallet signatures, show hex by default to avoid garbled output
                val format = if (result.isHashed && messageFormat == "text") {
                    MessageFormat.HEX
                } else {
                    when (messageFormat.lowercase()) {
                        "hex" -> MessageFormat.HEX
                        "base64" -> MessageFormat.BASE64
                        else -> MessageFormat.TEXT
                    }
                }
                val message = result.getMessage(format)
                if (result.isHashed && messageFormat == "text") {
                    println("$message (Blake2b-224 hash - use --format text to see raw)")
                } else {
                    println(message)
                }
            } catch (e: Exception) {
                println("Error extracting message: ${e.message}")
            }
            println()

            // Public Key
            println("Public Key (Ed25519):")
            println("-".repeat(80))
            try {
                val pubKey = result.ed25519PublicKey
                println(HexUtil.encodeHexString(pubKey))
            } catch (e: Exception) {
                println("Error extracting public key: ${e.message}")
            }
            println()

            // Signature
            println("Signature (Ed25519):")
            println("-".repeat(80))
            try {
                val sig = result.ed25519Signature
                println(HexUtil.encodeHexString(sig))
            } catch (e: Exception) {
                println("Error extracting signature: ${e.message}")
            }
            println()

            // COSE Payload
            println("COSE Payload (hex):")
            println("-".repeat(80))
            try {
                val cosePayload = result.cosePayload
                println(HexUtil.encodeHexString(cosePayload))
            } catch (e: Exception) {
                println("Error extracting COSE payload: ${e.message}")
            }
            println()

            println("=".repeat(80))
            println()

            if (result.isHashed) {
                println("NOTE: This signature was created by a hardware wallet (Ledger, Trezor, etc.)")
                println("      The message was hashed before signing for security.")
                println()
                println("      To verify the payload matches:")
                println("      Use result.verifyPayload(originalPayload) in your code")
                println()
                println("=".repeat(80))
            }

            return if (result.isValid) 0 else 1

        } catch (e: Exception) {
            println("Error: Failed to verify CIP-30 signature")
            println("Reason: ${e.message}")
            println()
            println("Make sure:")
            println("  1. Signature is a valid hex string")
            println("  2. Public key is a valid hex string (if provided)")
            println("  3. Signature was created according to CIP-30 standard")
            println()
            e.printStackTrace()
            return 1
        }
    }
}
