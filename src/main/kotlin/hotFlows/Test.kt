package hotFlows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {

    val flow = Repository2.timer
    scope.launch {
        flow.collect {
            println("Coroutine 1: $it")
        }
    }
    scope.launch {
        delay(5000.milliseconds)
        flow.collect {
            println("Coroutine 2: $it")
        }
    }
}

