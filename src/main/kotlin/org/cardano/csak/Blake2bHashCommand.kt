package org.cardano.csak

import com.bloxbean.cardano.client.crypto.Blake2bUtil
import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "blake2b-hash",
    mixinStandardHelpOptions = true,
    description = ["Calculate Blake2b hashes (160, 224, 256) from input data"]
)
class Blake2bHashCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Input data (hex string or UTF-8 text, see --input-format)"]
    )
    private lateinit var input: String

    @picocli.CommandLine.Option(
        names = ["--input-format"],
        description = ["Input format: hex (hexadecimal string, default), text (UTF-8 string)"],
        defaultValue = "hex"
    )
    private var inputFormat: String = "hex"

    override fun call(): Int {
        try {
            // Convert input to bytes based on format
            val inputBytes = when (inputFormat.lowercase()) {
                "hex" -> {
                    try {
                        HexUtil.decodeHexString(input.replace("\\s".toRegex(), ""))
                    } catch (e: Exception) {
                        println("Error: Invalid hex format in input")
                        println("Reason: ${e.message}")
                        return 1
                    }
                }
                "text" -> input.toByteArray(Charsets.UTF_8)
                else -> {
                    println("Error: Invalid input format. Use 'hex' or 'text'")
                    return 1
                }
            }

            // Calculate Blake2b hashes
            val blake2b160 = Blake2bUtil.blake2bHash160(inputBytes)
            val blake2b224 = Blake2bUtil.blake2bHash224(inputBytes)
            val blake2b256 = Blake2bUtil.blake2bHash256(inputBytes)

            // Convert to hex strings
            val blake2b160Hex = HexUtil.encodeHexString(blake2b160)
            val blake2b224Hex = HexUtil.encodeHexString(blake2b224)
            val blake2b256Hex = HexUtil.encodeHexString(blake2b256)

            // Display results
            println("=".repeat(80))
            println("Blake2b Hash Results")
            println("=".repeat(80))
            println()
            println("Input Format: ${inputFormat.uppercase()}")
            println()
            if (inputFormat.lowercase() == "text") {
                println("Input (text):")
                println(input)
                println()
            }
            println("Input (hex):")
            println(HexUtil.encodeHexString(inputBytes))
            println()
            println("Input Size: ${inputBytes.size} bytes")
            println()
            println("Blake2b-160 (20 bytes):")
            println(blake2b160Hex)
            println()
            println("Blake2b-224 (28 bytes):")
            println(blake2b224Hex)
            println()
            println("Blake2b-256 (32 bytes):")
            println(blake2b256Hex)
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
