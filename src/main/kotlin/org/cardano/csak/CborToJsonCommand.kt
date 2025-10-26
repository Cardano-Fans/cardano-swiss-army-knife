package org.cardano.csak

import com.bloxbean.cardano.client.metadata.helper.MetadataToJsonNoSchemaConverter
import com.bloxbean.cardano.client.util.HexUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import picocli.CommandLine.Option
import java.util.concurrent.Callable

@Command(
    name = "cbor-to-json",
    mixinStandardHelpOptions = true,
    description = ["Convert CBOR hex bytes to JSON representation"]
)
class CborToJsonCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["CBOR data in hex format"]
    )
    private lateinit var cborHex: String

    @Option(
        names = ["--pretty"],
        description = ["Pretty print the JSON output (default: true)"],
        defaultValue = "true"
    )
    private var prettyPrint: Boolean = true

    override fun call(): Int {
        try {
            // Clean the input
            val cleanCbor = cborHex.replace("\\s".toRegex(), "")

            // Validate hex format
            if (!cleanCbor.matches(Regex("^[0-9a-fA-F]+$"))) {
                println("Error: Invalid hex format. Only hexadecimal characters (0-9, a-f, A-F) are allowed.")
                return 1
            }

            // Decode hex to bytes to get size
            val cborBytes = try {
                HexUtil.decodeHexString(cleanCbor)
            } catch (e: Exception) {
                println("Error: Failed to decode hex string")
                println("Reason: ${e.message}")
                return 1
            }

            // Convert CBOR to JSON using MetadataToJsonNoSchemaConverter
            val jsonString = try {
                val json = MetadataToJsonNoSchemaConverter.cborHexToJson(cleanCbor)
                // Re-format for pretty printing if needed
                if (prettyPrint) {
                    val mapper = ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                    val jsonNode = mapper.readTree(json)
                    mapper.writeValueAsString(jsonNode)
                } else {
                    json
                }
            } catch (e: Exception) {
                println("Error: Failed to convert CBOR to JSON")
                println("Reason: ${e.message}")
                return 1
            }

            // Display results
            println("=".repeat(80))
            println("CBOR to JSON Conversion")
            println("=".repeat(80))
            println()
            println("CBOR Size: ${cborBytes.size} bytes")
            println()
            println(jsonString)
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
