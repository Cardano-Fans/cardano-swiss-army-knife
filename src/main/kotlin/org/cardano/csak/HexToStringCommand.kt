package org.cardano.csak

import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "hex-to-string",
    mixinStandardHelpOptions = true,
    description = ["Convert a hex string to UTF-8 string format"]
)
class HexToStringCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Input hex string to convert to UTF-8 string"]
    )
    private lateinit var hexString: String

    override fun call(): Int {
        try {
            // Remove any whitespace and validate hex format
            val cleanHex = hexString.replace("\\s".toRegex(), "")

            // Validate hex string
            if (!cleanHex.matches(Regex("^[0-9a-fA-F]*$"))) {
                println("Error: Invalid hex string. Only hexadecimal characters (0-9, a-f, A-F) are allowed.")
                return 1
            }

            if (cleanHex.length % 2 != 0) {
                println("Error: Hex string must have an even number of characters.")
                return 1
            }

            // Convert hex to bytes
            val bytes = HexUtil.decodeHexString(cleanHex)

            // Convert bytes to string using UTF-8 encoding
            val resultString = String(bytes, Charsets.UTF_8)

            // Display results
            println("=".repeat(80))
            println("Hex to String Conversion")
            println("=".repeat(80))
            println()
            println("Input Hex:")
            println(cleanHex)
            println()
            println("UTF-8 String Output:")
            println(resultString)
            println()
            println("Byte Length: ${bytes.size}")
            println()
            println("=".repeat(80))

            return 0
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return 1
        }
    }
}
