package org.cardano.csak

import com.bloxbean.cardano.client.plutus.spec.PlutusData
import com.bloxbean.cardano.client.plutus.spec.serializers.PlutusDataJsonConverter
import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import picocli.CommandLine.Option
import java.util.concurrent.Callable

@Command(
    name = "datum-to-json",
    mixinStandardHelpOptions = true,
    description = ["Convert Cardano datum (PlutusData CBOR) to JSON format"]
)
class DatumToJsonCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Datum CBOR data in hex format (PlutusData serialized)"]
    )
    private lateinit var datumHex: String


    override fun call(): Int {
        try {
            // Clean the input
            val cleanHex = datumHex.replace("\\s".toRegex(), "")

            // Validate hex format
            if (!cleanHex.matches(Regex("^[0-9a-fA-F]+$"))) {
                println("Error: Invalid hex format. Only hexadecimal characters (0-9, a-f, A-F) are allowed.")
                return 1
            }

            // Decode hex to bytes
            val datumBytes = try {
                HexUtil.decodeHexString(cleanHex)
            } catch (e: Exception) {
                println("Error: Failed to decode hex string")
                println("Reason: ${e.message}")
                return 1
            }

            // Deserialize PlutusData from CBOR
            val plutusData = try {
                PlutusData.deserialize(datumBytes)
            } catch (e: Exception) {
                println("Error: Failed to deserialize PlutusData from CBOR")
                println("Reason: ${e.message}")
                println()
                println("Note: This command expects PlutusData CBOR format. For general CBOR, use 'cbor-to-json' instead.")
                return 1
            }

            // Convert to JSON
            val json = try {
                PlutusDataJsonConverter.toJson(plutusData)
            } catch (e: Exception) {
                println("Error: Failed to convert PlutusData to JSON")
                println("Reason: ${e.message}")
                return 1
            }

            // Display results
            println("=".repeat(80))
            println("Datum (PlutusData) to JSON Conversion")
            println("=".repeat(80))
            println()
            println("Datum CBOR Size: ${datumBytes.size} bytes")
            println()

            val datumHash = try {
                plutusData.getDatumHash()
            } catch (e: Exception) {
                println("Warning: Failed to calculate datum hash")
                println("Reason: ${e.message}")
                null
            }

            if (datumHash != null) {
                println("Datum Hash:")
                println(datumHash)
                println()
            }

            println("PlutusData JSON:")
            println(json)
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
