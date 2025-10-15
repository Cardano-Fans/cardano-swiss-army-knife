package org.cardano.csak

import com.bloxbean.cardano.client.address.AddressProvider
import com.bloxbean.cardano.client.common.model.Networks
import com.bloxbean.cardano.client.crypto.KeyGenCborUtil
import com.bloxbean.cardano.client.crypto.KeyGenUtil
import com.bloxbean.cardano.client.crypto.SecretKey
import com.bloxbean.cardano.client.crypto.VerificationKey
import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "private-to-public-key",
    mixinStandardHelpOptions = true,
    description = ["Extract public key and address from a private key"]
)
class PrivateToPublicKeyCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Private key in hex format (plain or CBOR)"]
    )
    private lateinit var privateKeyHex: String

    @Option(
        names = ["-n", "--network"],
        description = ["Network type: mainnet (default), testnet"],
        defaultValue = "mainnet"
    )
    private var network: String = "mainnet"

    @Option(
        names = ["-f", "--format"],
        description = ["Input format: cbor (default), hex"],
        defaultValue = "cbor"
    )
    private var format: String = "cbor"

    override fun call(): Int {
        try {
            // Determine if mainnet
            val isMainnet = when (network.lowercase()) {
                "testnet" -> false
                "mainnet" -> true
                else -> {
                    println("Error: Invalid network. Use 'mainnet' or 'testnet'")
                    return 1
                }
            }

            // Convert to CBOR hex if needed
            val privateKeyCborHex = when (format.lowercase()) {
                "hex" -> {
                    // Convert plain hex to CBOR hex
                    val bytes = HexUtil.decodeHexString(privateKeyHex)
                    KeyGenCborUtil.bytesToCbor(bytes)
                }
                "cbor" -> privateKeyHex
                else -> {
                    println("Error: Invalid format. Use 'cbor' or 'hex'")
                    return 1
                }
            }

            // Create SecretKey from CBOR hex
            val secretKey = SecretKey(privateKeyCborHex)

            // Get verification key (public key) from private key
            val verificationKey: VerificationKey = KeyGenUtil.getPublicKeyFromPrivateKey(secretKey)

            // Get public key bytes and hex
            val publicKeyBytes = verificationKey.bytes
            val publicKeyHex = HexUtil.encodeHexString(publicKeyBytes)

            // Create HdPublicKey and derive enterprise address
            val hdPublicKey = com.bloxbean.cardano.client.crypto.bip32.key.HdPublicKey()
            hdPublicKey.keyData = verificationKey.bytes

            val address = AddressProvider.getEntAddress(
                hdPublicKey,
                if (isMainnet) Networks.mainnet() else Networks.testnet()
            )
            val addressBech32 = address.toBech32()

            // Display results
            println("=".repeat(80))
            println("Public Key Extracted from Private Key")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println("Input Format: ${format.uppercase()}")
            println()
            println("Private Key (${if (format.lowercase() == "hex") "hex" else "CBOR hex"}):")
            println(if (format.lowercase() == "hex") privateKeyHex else privateKeyCborHex)
            println()
            println("Public Key (hex):")
            println(publicKeyHex)
            println()
            println("Address (Bech32):")
            println(addressBech32)
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
