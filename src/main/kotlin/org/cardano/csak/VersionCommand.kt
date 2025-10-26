package org.cardano.csak

import picocli.CommandLine.Command
import java.util.concurrent.Callable

@Command(
    name = "version",
    mixinStandardHelpOptions = true,
    description = ["Display version information"]
)
class VersionCommand : Callable<Int> {

    override fun call(): Int {
        println("=".repeat(80))
        println("Cardano Swiss Army Knife (csak)")
        println("=".repeat(80))
        println()
        println("Version: ${Version.VERSION}")
        println()
        println("Build Information:")
        println("  Kotlin: 2.2.0")
        println("  JDK: 24")
        println("  Cardano Client Library: 0.7.0")
        println()
        println("Project: https://github.com/Cardano-Fans/cardano-swiss-army-knife")
        println("License: Apache 2.0")
        println()
        println("=".repeat(80))
        return 0
    }
}
