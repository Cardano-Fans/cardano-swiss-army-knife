package org.cardano.csak

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.concurrent.Callable

@Command(
    name = "hello",
    description = ["Say hello to someone"]
)
class HelloCommand : Callable<Int> {

    @Parameters(
        index = "0",
        description = ["Name to greet"],
        defaultValue = "World"
    )
    private var name: String = "World"

    override fun call(): Int {
        println("Hello, $name!")
        return 0
    }
}
