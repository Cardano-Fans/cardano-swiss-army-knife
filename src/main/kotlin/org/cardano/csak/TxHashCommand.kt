package org.cardano.csak

import com.bloxbean.cardano.client.transaction.util.TransactionUtil
import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "tx-hash",
    mixinStandardHelpOptions = true,
    description = ["Calculate transaction hash from transaction CBOR bytes"]
)
class TxHashCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Transaction CBOR bytes in hex format"]
    )
    private lateinit var txCbor: String

    override fun call(): Int {
        try {
            // Clean input - remove whitespace
            val cleanCbor = txCbor.replace("\\s".toRegex(), "")

            // Validate hex format
            if (!cleanCbor.matches(Regex("^[0-9a-fA-F]+$"))) {
                println("Error: Invalid hex format. Only hexadecimal characters (0-9, a-f, A-F) are allowed.")
                return 1
            }

            // Decode hex to bytes
            val txBytes = try {
                HexUtil.decodeHexString(cleanCbor)
            } catch (e: Exception) {
                println("Error: Failed to decode hex string")
                println("Reason: ${e.message}")
                return 1
            }

            // Calculate transaction hash
            val txHash = try {
                TransactionUtil.getTxHash(txBytes)
            } catch (e: Exception) {
                println("Error: Failed to calculate transaction hash")
                println("Reason: ${e.message}")
                e.printStackTrace()
                return 1
            }

            // Display results
            println("=".repeat(80))
            println("Transaction Hash Calculation")
            println("=".repeat(80))
            println()
            println("Transaction CBOR (hex):")
            println(cleanCbor)
            println()
            println("Transaction CBOR Size: ${txBytes.size} bytes")
            println()
            println("Transaction Hash:")
            println(txHash)
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
