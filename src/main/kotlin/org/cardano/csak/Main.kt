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
        "  hello - Say hello",
        "",
        "Use '<command> --help' for more information about a command."
    ],
    subcommands = [
        HelloCommand::class
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
