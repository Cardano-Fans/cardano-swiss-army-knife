package org.cardano.csak

import picocli.CommandLine
import picocli.CommandLine.Command
import kotlin.system.exitProcess

@Command(
    name = "csak",
    mixinStandardHelpOptions = true,
    version = ["csak 1.0.0"],
    description = [
        "Cardano Swiss Army Knife - A collection of useful Cardano tools.",
        "",
        "Available commands:",
        "  hd-wallet-generate     - Generate HD wallet with derivation paths",
        "  hd-wallet-restore      - Restore HD wallet from 15 or 24-word mnemonic",
        "  private-to-public-key  - Extract public key and address from private key",
        "  blake2b-hash           - Calculate Blake2b hashes from hex string",
        "  string-to-hex          - Convert a string to hex format",
        "  hex-to-string          - Convert a hex string to UTF-8 format",
        "  cip30-verify           - Verify and parse CIP-30 data signatures",
        "",
        "Use '<command> --help' for more information about a command."
    ],
    subcommands = [
        HdWalletGenerateCommand::class,
        HdWalletRestoreCommand::class,
        PrivateToPublicKeyCommand::class,
        Blake2bHashCommand::class,
        StringToHexCommand::class,
        HexToStringCommand::class,
        Cip30VerifyCommand::class
    ]
)
class CsakCLI : Runnable {

    @CommandLine.Spec
    private lateinit var spec: CommandLine.Model.CommandSpec

    override fun run() {
        // When no subcommand is provided, print help
        throw CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand")
    }
}

fun main(args: Array<String>) {
    val commandLine = CommandLine(CsakCLI())

    // If no arguments provided, show help
    if (args.isEmpty()) {
        commandLine.usage(System.out)
        exitProcess(0)
    }

    exitProcess(commandLine.execute(*args))
}
