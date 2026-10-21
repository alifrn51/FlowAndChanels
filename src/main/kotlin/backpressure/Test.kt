package backpressure

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {
    val flow = flow {
        repeat(100){
            println("Emitted: $it")
            emit(it)
            println("After emit: $it")
            delay(100.milliseconds)
        }
    }.buffer(capacity = 5, onBufferOverflow = BufferOverflow.DROP_LATEST)
    scope.launch {
        flow.collect {
            println("Collected: $it")
            delay(1000.milliseconds)
            println(it)
        }
    }
}