package org.cardano.csak

import com.bloxbean.cardano.client.account.Account
import com.bloxbean.cardano.client.common.model.Networks
import com.bloxbean.cardano.client.crypto.KeyGenCborUtil
import com.bloxbean.cardano.client.util.HexUtil
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "hd-wallet-restore",
    mixinStandardHelpOptions = true,
    description = ["Restore and validate an HD wallet from a 15 or 24-word mnemonic"]
)
class HdWalletRestoreCommand : Callable<Int> {

    @Parameters(
        index = "0..*",
        arity = "15..24",
        description = ["Mnemonic phrase (15 or 24 words separated by spaces)"]
    )
    private lateinit var mnemonicWords: List<String>

    @Option(
        names = ["-n", "--network"],
        description = ["Network type: mainnet (default), preprod, preview"],
        defaultValue = "mainnet"
    )
    private var network: String = "mainnet"

    @Option(
        names = ["-c", "--count"],
        description = ["Number of derivation paths to generate (default: 1)"],
        defaultValue = "1"
    )
    private var count: Int = 1

    override fun call(): Int {
        // Validate count
        if (count < 1) {
            println("Error: Count must be at least 1")
            return 1
        }

        // Join mnemonic words
        val mnemonic = mnemonicWords.joinToString(" ")

        // Validate mnemonic word count
        val wordCount = mnemonicWords.size
        if (wordCount != 15 && wordCount != 24) {
            println("Error: Mnemonic must contain exactly 15 or 24 words (received $wordCount words)")
            return 1
        }

        // Try to create account with the mnemonic
        val account = try {
            when (network.lowercase()) {
                "mainnet" -> Account(Networks.mainnet(), mnemonic)
                "preprod" -> Account(Networks.preprod(), mnemonic)
                "preview" -> Account(Networks.preview(), mnemonic)
                else -> {
                    println("Error: Invalid network. Use 'mainnet', 'preprod', or 'preview'")
                    return 1
                }
            }
        } catch (e: Exception) {
            println("Error: Invalid mnemonic phrase")
            println("Reason: ${e.message}")
            return 1
        }

        // If we got here, the mnemonic is valid
        println("=".repeat(80))
        println("Cardano HD Wallet Restored Successfully")
        println("=".repeat(80))
        println()
        println("Network: ${network.uppercase()}")
        println("Mnemonic Words: $wordCount")
        println()
        println("Mnemonic:")
        println("-".repeat(80))
        println(mnemonic)
        println("-".repeat(80))
        println()

        // Display accounts based on count
        for (index in 0 until count) {
            if (index > 0) {
                println()
            }

            val accountLabel = if (index == 0) "Account" else "Derived Account"
            println("$accountLabel (index=$index):")
            println("-".repeat(80))

            // Create account at index
            val derivedAccount = if (index == 0) {
                account
            } else {
                val networkObj = when (network.lowercase()) {
                    "mainnet" -> Networks.mainnet()
                    "preprod" -> Networks.preprod()
                    "preview" -> Networks.preview()
                    else -> Networks.mainnet()
                }
                Account(networkObj, mnemonic, index)
            }
            displayAccountInfo(derivedAccount, index)
        }

        println()
        println("=".repeat(80))
        println()
        println("IMPORTANT: Keep this mnemonic and private keys secure!")
        println("Anyone with access to these can control your funds.")
        println("=".repeat(80))

        return 0
    }

    private fun displayAccountInfo(account: Account, index: Int) {
        // Cardano derivation paths (CIP-1852)
        // Format: m / purpose' / coin_type' / account' / role / address_index
        val purpose = "1852'"  // CIP-1852 (Shelley)
        val coinType = "1815'" // Ada (Lovelace's birth year)
        val accountIndex = "$index'"

        // CIP-1852 Role types
        val externalPath = "m/$purpose/$coinType/$accountIndex/0/0"  // Payment/External addresses
        val internalPath = "m/$purpose/$coinType/$accountIndex/1/0"  // Change/Internal addresses
        val stakingPath = "m/$purpose/$coinType/$accountIndex/2/0"   // Staking key
        val drepPath = "m/$purpose/$coinType/$accountIndex/3/0"      // DRep key (CIP-0105)
        val ccColdPath = "m/$purpose/$coinType/$accountIndex/4/0"    // Constitutional Committee Cold
        val ccHotPath = "m/$purpose/$coinType/$accountIndex/5/0"     // Constitutional Committee Hot

        // Get addresses
        val baseAddress = account.baseAddress()
        val stakeAddress = account.stakeAddress()

        // Get private and public keys as hex
        val privateKeyHex = HexUtil.encodeHexString(account.privateKeyBytes())
        val publicKeyHex = HexUtil.encodeHexString(account.publicKeyBytes())

        // Get private and public keys as CBOR hex
        val privateKeyCborHex = KeyGenCborUtil.bytesToCbor(account.privateKeyBytes())
        val publicKeyCborHex = KeyGenCborUtil.bytesToCbor(account.publicKeyBytes())

        println("  Wallet Type: SOFTWARE (Icarus derivation)")
        println()
        println("  Derivation Paths (CIP-1852):")
        println("    External Chain (payment):  $externalPath")
        println("    Internal Chain (change):   $internalPath")
        println("    Staking Key:                $stakingPath")
        println("    DRep Key (governance):      $drepPath")
        println("    CC Cold Key (governance):   $ccColdPath")
        println("    CC Hot Key (governance):    $ccHotPath")
        println()
        println("  Base Address (Bech32):")
        println("    $baseAddress")
        println()
        println("  Stake Address (Bech32):")
        println("    $stakeAddress")
        println()
        println("  Private Key (hex):")
        println("    $privateKeyHex")
        println("  Private Key (CBOR hex):")
        println("    $privateKeyCborHex")
        println()
        println("  Public Key (hex):")
        println("    $publicKeyHex")
        println("  Public Key (CBOR hex):")
        println("    $publicKeyCborHex")
    }
}
