package org.cardano.csak

import org.cardanofoundation.conversions.ClasspathConversionsFactory
import org.cardanofoundation.conversions.domain.EraType
import org.cardanofoundation.conversions.domain.NetworkType
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

@Command(
    name = "cardano-hardforks",
    mixinStandardHelpOptions = true,
    description = ["Display information about Cardano hard forks and era transitions"]
)
class CardanoHardforksCommand : Callable<Int> {

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
            val eraConversions = converters.era()
            val genesisConfig = converters.genesisConfig()

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            // Display header
            println("=".repeat(80))
            println("Cardano Hard Forks & Era Transitions")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println()

            // List of eras to display
            val eras = listOf(
                EraType.Byron,
                EraType.Shelley,
                EraType.Allegra,
                EraType.Mary,
                EraType.Alonzo,
                EraType.Babbage,
                EraType.Conway
            )

            for (era in eras) {
                try {
                    println("${era.name} Era:")
                    println("-".repeat(80))

                    // First real slot
                    val firstSlot = eraConversions.firstRealSlot(era)
                    println("  First Slot: $firstSlot")

                    // First time
                    val firstTime = eraConversions.firstRealEraTime(era)
                    println("  Start Time: ${firstTime.format(formatter)}")

                    // Last slot (if available)
                    val lastSlotOpt = eraConversions.lastRealSlot(era)
                    if (lastSlotOpt.isPresent) {
                        println("  Last Slot: ${lastSlotOpt.get()}")

                        val lastTimeOpt = eraConversions.lastRealEraTime(era)
                        if (lastTimeOpt.isPresent) {
                            println("  End Time: ${lastTimeOpt.get().format(formatter)}")
                        }
                    } else {
                        println("  Last Slot: Current era (ongoing)")
                        println("  End Time: N/A (current era)")
                    }

                    println()
                } catch (e: Exception) {
                    // Era might not exist on this network
                    println("  Not available on ${network.uppercase()}")
                    println()
                }
            }

            // Additional genesis information
            println("Genesis Information:")
            println("-".repeat(80))
            println("  Byron Start Time: ${genesisConfig.startTime.format(formatter)}")
            println("  Shelley Start Time: ${genesisConfig.shelleyStartTime.format(formatter)}")
            println("  First Shelley Slot: ${genesisConfig.firstShelleySlot()}")
            println("  Last Byron Slot: ${genesisConfig.lastByronSlot()}")
            println()

            println("Slot Duration:")
            println("-".repeat(80))
            println("  Byron Slot Length: ${genesisConfig.byronSlotLength.seconds} seconds")
            println("  Shelley Slot Length: ${genesisConfig.shelleySlotLength.seconds} seconds")
            println()

            println("Epoch Length:")
            println("-".repeat(80))
            println("  Shelley Epoch Length: ${genesisConfig.shelleyEpochLength} slots")
            println()

            println("=".repeat(80))

            return 0
        } catch (e: Exception) {
            println("Error: Failed to retrieve hard fork information")
            println("Reason: ${e.message}")
            e.printStackTrace()
            return 1
        }
    }
}
