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
    name = "conversion-time-to-slot",
    mixinStandardHelpOptions = true,
    description = ["Convert UTC time to Cardano absolute slot number"]
)
class ConversionTimeToSlotCommand : Callable<Int> {

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
            val slotConversions = converters.slot()

            // Convert time to slot
            val slotNumber = timeConversions.toSlot(utcTime)

            // Also get epoch information
            val epochNumber = slotConversions.slotToEpoch(slotNumber)

            // Display results
            println("=".repeat(80))
            println("Time to Slot Conversion")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println("UTC Time: $utcTime")
            println()
            println("Absolute Slot:")
            println("-".repeat(80))
            println(slotNumber)
            println()
            println("Epoch Number: $epochNumber")
            println()
            println("=".repeat(80))

            return 0
        } catch (e: IllegalArgumentException) {
            println("Error: ${e.message}")
            println()
            println("The provided time may be before the blockchain started.")
            return 1
        } catch (e: Exception) {
            println("Error: Failed to convert time to slot")
            println("Reason: ${e.message}")
            e.printStackTrace()
            return 1
        }
    }
}
