package org.cardano.csak

import org.cardanofoundation.conversions.ClasspathConversionsFactory
import org.cardanofoundation.conversions.domain.NetworkType
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

@Command(
    name = "conversion-slot-to-epoch",
    mixinStandardHelpOptions = true,
    description = ["Convert Cardano absolute slot number to epoch number"]
)
class ConversionSlotToEpochCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Absolute slot number"]
    )
    private var slotNumber: Long = 0

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

            // Create converters
            val converters = ClasspathConversionsFactory.createConverters(networkType)
            val slotConversions = converters.slot()

            // Convert slot to epoch
            val epochNumber = slotConversions.slotToEpoch(slotNumber)

            // Also get time information
            val utcTime = slotConversions.slotToTime(slotNumber)
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedTime = utcTime.format(formatter)

            // Display results
            println("=".repeat(80))
            println("Slot to Epoch Conversion")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println("Absolute Slot: $slotNumber")
            println()
            println("Epoch Number:")
            println("-".repeat(80))
            println(epochNumber)
            println()
            println("UTC Time: $formattedTime")
            println()
            println("=".repeat(80))

            return 0
        } catch (e: Exception) {
            println("Error: Failed to convert slot to epoch")
            println("Reason: ${e.message}")
            e.printStackTrace()
            return 1
        }
    }
}
