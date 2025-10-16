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
    name = "conversion-slot-to-time",
    mixinStandardHelpOptions = true,
    description = ["Convert Cardano absolute slot number to UTC time"]
)
class ConversionSlotToTimeCommand : Callable<Int> {

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
            val epochConversions = converters.epoch()

            // Convert slot to time
            val utcTime = slotConversions.slotToTime(slotNumber)

            // Get epoch information
            val epochNumber = slotConversions.slotToEpoch(slotNumber)

            // Get epoch start and end times
            val epochStartTime = epochConversions.epochToUTCTime(epochNumber.toInt(), EpochOffset.START)
            val epochEndTime = epochConversions.epochToUTCTime(epochNumber.toInt(), EpochOffset.END)
            val epochStartSlot = epochConversions.epochToAbsoluteSlot(epochNumber.toInt(), EpochOffset.START)
            val epochEndSlot = epochConversions.epochToAbsoluteSlot(epochNumber.toInt(), EpochOffset.END)

            // Format output
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            // Display results
            println("=".repeat(80))
            println("Slot to Time Conversion")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println("Absolute Slot: $slotNumber")
            println()
            println("Slot Time:")
            println("-".repeat(80))
            println(utcTime.format(formatter))
            println("ISO: $utcTime")
            println()
            println("Epoch Information:")
            println("-".repeat(80))
            println("Epoch Number: $epochNumber")
            println()
            println("Epoch Start:")
            println("  Time: ${epochStartTime.format(formatter)}")
            println("  Slot: $epochStartSlot")
            println()
            println("Epoch End:")
            println("  Time: ${epochEndTime.format(formatter)}")
            println("  Slot: $epochEndSlot")
            println()
            println("Position in Epoch:")
            println("  Slot ${slotNumber - epochStartSlot + 1} of ${epochEndSlot - epochStartSlot + 1}")
            println()
            println("=".repeat(80))

            return 0
        } catch (e: Exception) {
            println("Error: Failed to convert slot to time")
            println("Reason: ${e.message}")
            e.printStackTrace()
            return 1
        }
    }
}
