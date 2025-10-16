package org.cardano.csak

import org.cardanofoundation.conversions.ClasspathConversionsFactory
import org.cardanofoundation.conversions.domain.EpochOffset
import org.cardanofoundation.conversions.domain.NetworkType
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

@Command(
    name = "conversion-epoch-to-time",
    mixinStandardHelpOptions = true,
    description = ["Convert Cardano epoch number to UTC time"]
)
class ConversionEpochToTimeCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Epoch number"]
    )
    private var epochNumber: Long = 0

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
            val epochConversions = converters.epoch()

            // Get both start and end times
            val startTime = epochConversions.epochToUTCTime(epochNumber.toInt(), EpochOffset.START)
            val endTime = epochConversions.epochToUTCTime(epochNumber.toInt(), EpochOffset.END)

            val startSlot = epochConversions.epochToAbsoluteSlot(epochNumber.toInt(), EpochOffset.START)
            val endSlot = epochConversions.epochToAbsoluteSlot(epochNumber.toInt(), EpochOffset.END)

            // Format output
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            // Display results
            println("=".repeat(80))
            println("Epoch to Time Conversion")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println("Epoch: $epochNumber")
            println()
            println("Start Time:")
            println("-".repeat(80))
            println(startTime.format(formatter))
            println("Slot: $startSlot")
            println("ISO: $startTime")
            println()
            println("End Time:")
            println("-".repeat(80))
            println(endTime.format(formatter))
            println("Slot: $endSlot")
            println("ISO: $endTime")
            println()
            println("Duration: ${endSlot - startSlot + 1} slots")
            println()
            println("=".repeat(80))

            return 0
        } catch (e: Exception) {
            println("Error: Failed to convert epoch to time")
            println("Reason: ${e.message}")
            e.printStackTrace()
            return 1
        }
    }
}
