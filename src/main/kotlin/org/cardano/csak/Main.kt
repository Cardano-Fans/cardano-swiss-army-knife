package org.cardano.csak

import picocli.CommandLine
import picocli.CommandLine.Command
import kotlin.system.exitProcess

@Command(
    name = "csak",
    mixinStandardHelpOptions = true,
    version = ["csak 0.1.0"],
    description = [
        "Cardano Swiss Army Knife - A collection of useful Cardano tools.",
        "",
        "Available commands:",
        "  hd-wallet-generate         - Generate HD wallet with derivation paths",
        "  hd-wallet-restore          - Restore HD wallet from 15 or 24-word mnemonic",
        "  private-to-public-key      - Extract public key and address from private key",
        "  blake2b-hash               - Calculate Blake2b hashes from hex string",
        "  cip30-sign                 - Sign data using CIP-30 standard (wallet message signing)",
        "  cip30-verify               - Verify and parse CIP-30 data signatures",
        "  tx-hash                    - Calculate transaction hash from transaction CBOR bytes",
        "  tx-decode                  - Decode transaction CBOR bytes to JSON format",
        "  cbor-to-json               - Convert CBOR hex bytes to JSON representation",
        "  datum-to-json              - Convert Cardano datum (PlutusData CBOR) to JSON format",
        "  util-string-to-hex         - Convert a string to hex format",
        "  util-hex-to-string         - Convert a hex string to UTF-8 format",
        "  util-string-to-base64      - Convert a string to Base64 format",
        "  util-base64-to-string      - Convert a Base64 string to UTF-8 format",
        "  conversion-epoch-to-time   - Convert epoch number to UTC time",
        "  conversion-time-to-epoch   - Convert UTC time to epoch number",
        "  conversion-slot-to-time    - Convert slot number to UTC time",
        "  conversion-time-to-slot    - Convert UTC time to slot number",
        "  conversion-slot-to-epoch   - Convert slot number to epoch number",
        "  conversion-epoch-to-slot   - Convert epoch number to slot number",
        "  cardano-eras               - Display Cardano eras and transitions",
        "  cardano-hardforks          - Display Cardano hard forks (including intra-era)",
        "",
        "Use '<command> --help' for more information about a command."
    ],
    subcommands = [
        HdWalletGenerateCommand::class,
        HdWalletRestoreCommand::class,
        PrivateToPublicKeyCommand::class,
        Blake2bHashCommand::class,
        Cip30SignCommand::class,
        Cip30VerifyCommand::class,
        TxHashCommand::class,
        TxDecodeCommand::class,
        CborToJsonCommand::class,
        DatumToJsonCommand::class,
        StringToHexCommand::class,
        HexToStringCommand::class,
        StringToBase64Command::class,
        Base64ToStringCommand::class,
        ConversionEpochToTimeCommand::class,
        ConversionTimeToEpochCommand::class,
        ConversionSlotToTimeCommand::class,
        ConversionTimeToSlotCommand::class,
        ConversionSlotToEpochCommand::class,
        ConversionEpochToSlotCommand::class,
        CardanoErasCommand::class,
        CardanoHardforksCommand::class
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
