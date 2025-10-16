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

    @Option(
        names = ["-o", "--offset"],
        description = ["Epoch offset: start (default), end"],
        defaultValue = "start"
    )
    private var offset: String = "start"

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

            // Parse offset
            val epochOffset = when (offset.lowercase()) {
                "start" -> EpochOffset.START
                "end" -> EpochOffset.END
                else -> {
                    println("Error: Invalid offset. Use 'start' or 'end'")
                    return 1
                }
            }

            // Create converters
            val converters = ClasspathConversionsFactory.createConverters(networkType)
            val epochConversions = converters.epoch()

            // Convert epoch to time
            val utcTime = epochConversions.epochToUTCTime(epochNumber.toInt(), epochOffset)

            // Also get slot information
            val slot = epochConversions.epochToAbsoluteSlot(epochNumber.toInt(), epochOffset)

            // Format output
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedTime = utcTime.format(formatter)

            // Display results
            println("=".repeat(80))
            println("Epoch to Time Conversion")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println("Epoch: $epochNumber (${offset.lowercase()})")
            println()
            println("UTC Time:")
            println("-".repeat(80))
            println(formattedTime)
            println()
            println("Absolute Slot: $slot")
            println()
            println("ISO Format: ${utcTime}")
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
