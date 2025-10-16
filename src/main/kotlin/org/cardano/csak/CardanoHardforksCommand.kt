package org.cardano.csak

import org.cardanofoundation.conversions.ClasspathConversionsFactory
import org.cardanofoundation.conversions.domain.NetworkType
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.time.format.DateTimeFormatter
import java.util.concurrent.Callable

@Command(
    name = "cardano-hardforks",
    mixinStandardHelpOptions = true,
    description = ["Display information about Cardano hard forks (including intra-era forks)"]
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
            val genesisConfig = converters.genesisConfig()
            val eraHistory = genesisConfig.eraHistory

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            // Display header
            println("=".repeat(80))
            println("Cardano Hard Forks")
            println("=".repeat(80))
            println()
            println("Network: ${network.uppercase()}")
            println()
            println("Hard forks represent protocol upgrades on the Cardano blockchain.")
            println("This includes both era transitions and intra-era hard forks.")
            println()

            println("Hard Fork Timeline:")
            println("-".repeat(80))
            println()

            println("Era transitions represent major hard forks:")
            println("  Byron → Shelley: Shelley HF")
            println("  Shelley → Allegra: Allegra HF")
            println("  Allegra → Mary: Mary HF")
            println("  Mary → Alonzo: Alonzo HF")
            println("  Alonzo → Babbage: Vasil HF")
            println("  Babbage → Conway: Chang HF")
            println()

            // Display known mainnet intra-era hard forks
            if (networkType == NetworkType.MAINNET) {
                println("Known Intra-Era Hard Forks (Mainnet):")
                println("-".repeat(80))
                println()

                // Alonzo intra-era hard fork
                println("Alonzo Intra-Era HF (Epoch 290 → 290):")
                println("  Date: September 12, 2021")
                println("  Description: Alonzo launch - Smart contracts enabled")
                println("  Slot: 39916800")
                println()

                // Vasil hard fork (Babbage era start)
                println("Vasil HF (Epoch 364 → 365):")
                println("  Date: September 22, 2022")
                println("  Description: Babbage era - Plutus V2, reference inputs, inline datums")
                println("  Slot: 72316800")
                println()

                // Valentine (SECP256k1) intra-era hard fork in Babbage
                println("Valentine Intra-Era HF (Epoch 394):")
                println("  Date: February 14, 2023")
                println("  Description: SECP256k1 support, Plutus V2 enhancements")
                println("  Occurred within Babbage era")
                println()

                // Chang hard fork #1 (Conway era start)
                println("Chang HF #1 (Epoch 506 → 507):")
                println("  Date: September 1, 2024")
                println("  Description: Conway era - Voltaire governance phase begins")
                println("  Slot: 133660800")
                println()
            }

            println("=".repeat(80))
            println()
            println("Note: Intra-era hard forks are protocol upgrades that occur within")
            println("      the same era without changing the ledger era type.")
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
