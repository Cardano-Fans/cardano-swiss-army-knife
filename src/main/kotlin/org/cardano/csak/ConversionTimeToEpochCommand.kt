package org.cardano.csak

import org.cardanofoundation.conversions.ClasspathConversionsFactory
import org.cardanofoundation.conversions.domain.NetworkType
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.concurrent.Callable

@Command(
    name = "conversion-time-to-epoch",
    mixinStandardHelpOptions = true,
    description = ["Convert UTC time to Cardano epoch number"]
)
class ConversionTimeToEpochCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["UTC time in ISO format (e.g., 2023-11-22T12:47:09 or '2023-11-22 12:47:09')"]
    )
    private lateinit var timeString: String

    @Option(
        names = ["-n", "--network"],
        description = ["Network type: mainnet (default), preprod, preview"],
        defaultValue = "mainnet"
    )
    private var network: String = "mainnet"

    override fun call(): Int {
        try {
            // Parse network type
            val networkType = when (network.lowercase()) {
                "mainnet" -> NetworkType.MAINNET
                "preprod" -> NetworkType.PREPROD
                "preview" -> NetworkType.PREVIEW
                else -> {
                    println("Error: Invalid network. Use 'mainnet', 'preprod', or 'preview'")
                    return 1
                }
            }

            // Parse time - try multiple formats
            val utcTime = try {
                LocalDateTime.parse(timeString)
            } catch (e: DateTimeParseException) {
                try {
                    // Try space-separated format
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    LocalDateTime.parse(timeString, formatter)
                } catch (e2: DateTimeParseException) {
                    println("Error: Invalid time format")
                    println("Use ISO format like: 2023-11-22T12:47:09")
                    println("Or space-separated: '2023-11-22 12:47:09'")
                    return 1
                }
            }

            // Create converters
            val converters = ClasspathConversionsFactory.createConverters(networkType)
            val timeConversions = converters.time()

            // Convert time to epoch
            val epochNumber = timeConversions.utcTimeToEpochNo(utcTime)

            // Also get slot information
            val slot = timeConversions.toSlot(utcTime)

            // Display results
            println("=".repeat(80))
            println("Time to Epoch Conversion")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println("UTC Time: $utcTime")
            println()
            println("Epoch Number:")
            println("-".repeat(80))
            println(epochNumber)
            println()
            println("Absolute Slot: $slot")
            println()
            println("=".repeat(80))

            return 0
        } catch (e: IllegalArgumentException) {
            println("Error: ${e.message}")
            println()
            println("The provided time may be before the blockchain started.")
            return 1
        } catch (e: Exception) {
            println("Error: Failed to convert time to epoch")
            println("Reason: ${e.message}")
            e.printStackTrace()
            return 1
        }
    }
}
