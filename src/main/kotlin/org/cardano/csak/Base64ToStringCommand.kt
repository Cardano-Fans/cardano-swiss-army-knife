package org.cardano.csak

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.Base64
import java.util.concurrent.Callable

@Command(
    name = "util-base64-to-string",
    mixinStandardHelpOptions = true,
    description = ["Convert a Base64 string to UTF-8 string format"]
)
class Base64ToStringCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Input Base64 string to convert to UTF-8 string"]
    )
    private lateinit var base64String: String

    override fun call(): Int {
        try {
            // Remove any whitespace
            val cleanBase64 = base64String.replace("\\s".toRegex(), "")

            // Validate Base64 format (basic check)
            if (!cleanBase64.matches(Regex("^[A-Za-z0-9+/]*={0,2}$"))) {
                println("Error: Invalid Base64 string. Only Base64 characters (A-Z, a-z, 0-9, +, /, =) are allowed.")
                return 1
            }

            // Decode Base64 to bytes
            val bytes = Base64.getDecoder().decode(cleanBase64)

            // Convert bytes to string using UTF-8 encoding
            val resultString = String(bytes, Charsets.UTF_8)

            // Display results
            println("=".repeat(80))
            println("Base64 to String Conversion")
            println("=".repeat(80))
            println()
            println("Input Base64:")
            println(cleanBase64)
            println()
            println("UTF-8 String Output:")
            println(resultString)
            println()
            println("Byte Length: ${bytes.size}")
            println()
            println("=".repeat(80))

            return 0
        } catch (e: IllegalArgumentException) {
            println("Error: Invalid Base64 string format")
            println("Reason: ${e.message}")
            return 1
        } catch (e: Exception) {
            println("Error: ${e.message}")
            return 1
        }
    }
}
