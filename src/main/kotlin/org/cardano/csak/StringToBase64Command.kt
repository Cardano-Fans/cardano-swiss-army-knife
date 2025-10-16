package org.cardano.csak

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.Base64
import java.util.concurrent.Callable

@Command(
    name = "util-string-to-base64",
    mixinStandardHelpOptions = true,
    description = ["Convert a UTF-8 string to Base64 format"]
)
class StringToBase64Command : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Input string to convert to Base64"]
    )
    private lateinit var inputString: String

    override fun call(): Int {
        try {
            // Convert string to bytes using UTF-8 encoding
            val bytes = inputString.toByteArray(Charsets.UTF_8)

            // Encode to Base64
            val base64String = Base64.getEncoder().encodeToString(bytes)

            // Display results
            println("=".repeat(80))
            println("String to Base64 Conversion")
            println("=".repeat(80))
            println()
            println("Input String:")
            println(inputString)
            println()
            println("Base64 Output:")
            println(base64String)
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
