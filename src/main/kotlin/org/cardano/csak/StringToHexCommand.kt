package org.cardano.csak

import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "string-to-hex",
    mixinStandardHelpOptions = true,
    description = ["Convert a string to hex format (UTF-8 encoding)"]
)
class StringToHexCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Input string to convert to hex"]
    )
    private lateinit var inputString: String

    override fun call(): Int {
        try {
            // Convert string to bytes using UTF-8 encoding
            val bytes = inputString.toByteArray(Charsets.UTF_8)

            // Convert bytes to hex
            val hexString = HexUtil.encodeHexString(bytes)

            // Display results
            println("=".repeat(80))
            println("String to Hex Conversion")
            println("=".repeat(80))
            println()
            println("Input String:")
            println(inputString)
            println()
            println("Hex Output:")
            println(hexString)
            println()
            println("Byte Length: ${bytes.size}")
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
