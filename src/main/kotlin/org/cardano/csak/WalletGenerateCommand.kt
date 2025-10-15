package org.cardano.csak

import com.bloxbean.cardano.client.account.Account
import com.bloxbean.cardano.client.common.model.Networks
import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.util.concurrent.Callable

@Command(
    name = "wallet-generate",
    mixinStandardHelpOptions = true,
    description = ["Generate a 24-word mnemonic with Cardano wallet details"]
)
class WalletGenerateCommand : Callable<Int> {

    @Option(
        names = ["-n", "--network"],
        description = ["Network type: mainnet (default), testnet"],
        defaultValue = "mainnet"
    )
    private var network: String = "mainnet"

    override fun call(): Int {
        // Create account based on network
        val account = when (network.lowercase()) {
            "testnet" -> Account(Networks.testnet())
            "mainnet" -> Account(Networks.mainnet())
            else -> {
                println("Error: Invalid network. Use 'mainnet' or 'testnet'")
                return 1
            }
        }

        // Get addresses, keys, and mnemonic
        val baseAddress = account.baseAddress()
        val stakeAddress = account.stakeAddress()
        val mnemonic = account.mnemonic()

        // Get private and public keys as hex
        val privateKeyHex = HexUtil.encodeHexString(account.privateKeyBytes())
        val publicKeyHex = HexUtil.encodeHexString(account.publicKeyBytes())

        // Display results
        println("=".repeat(80))
        println("Cardano Wallet Generated")
        println("=".repeat(80))
        println()
        println("Network: ${network.uppercase()}")
        println()
        println("Mnemonic (24 words):")
        println("-".repeat(80))
        println(mnemonic)
        println("-".repeat(80))
        println()
        println("Base Address (index=0):")
        println(baseAddress)
        println()
        println("Stake Address:")
        println(stakeAddress)
        println()
        println("Private Key (hex):")
        println(privateKeyHex)
        println()
        println("Public Key (hex):")
        println(publicKeyHex)
        println()
        println("=".repeat(80))
        println()
        println("IMPORTANT: Store this mnemonic and private key securely!")
        println("Anyone with access to these can control your funds.")
        println("=".repeat(80))

        return 0
    }
}
