package hotFlows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.milliseconds

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

val stateFlow = MutableStateFlow(0)
val sharedFlow = MutableSharedFlow<Int>(replay = 3, onBufferOverflow = BufferOverflow.DROP_OLDEST)

fun main() {
    stateFlow.onEach {
        delay(1_000.milliseconds)
        println("State flow: $it")
    }.launchIn(scope)

    sharedFlow.onEach {
        delay(1_000.milliseconds)
        println("Shared flow: $it")
    }.launchIn(scope)

    emitFromCoroutine()
}

fun emitFromStandardMethod(){
    Thread.sleep(2_000)
    thread {
        repeat(100){
            stateFlow.tryEmit(it)
        }
    }
    thread {
        repeat(100){
            sharedFlow.tryEmit(it)
        }
    }
}


fun emitFromCoroutine() {
    scope.launch {
        delay(2_000.milliseconds)
        repeat(100) {
            stateFlow.emit(it)
        }
    }

    scope.launch {
        delay(2_000.milliseconds)
        repeat(100) {
            sharedFlow.emit(it)
        }
    }
}