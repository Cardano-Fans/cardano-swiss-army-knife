package org.cardano.csak

import com.bloxbean.cardano.client.transaction.spec.Transaction
import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "tx-decode",
    mixinStandardHelpOptions = true,
    description = ["Decode transaction CBOR bytes to JSON format"]
)
class TxDecodeCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Transaction CBOR bytes in hex format"]
    )
    private lateinit var txCbor: String

    override fun call(): Int {
        try {
            // Clean the input
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

            // Deserialize transaction
            val transaction = try {
                Transaction.deserialize(txBytes)
            } catch (e: Exception) {
                println("Error: Failed to deserialize transaction")
                println("Reason: ${e.message}")
                e.printStackTrace()
                return 1
            }

            // Convert to JSON and print
            println("=".repeat(80))
            println("Transaction Decoded from CBOR")
            println("=".repeat(80))
            println()
            println("Transaction CBOR Size: ${txBytes.size} bytes")
            println()
            println("Transaction JSON:")
            println(transaction.toJson())
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
