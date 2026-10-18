package lesson3

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

private var lastIndex = 0
private suspend fun loadVideo(): List<String>{
    delay(3000.milliseconds)
    return (lastIndex..<(lastIndex + 10))
        .map { "Video: $it" }
        .also {
            lastIndex += 10
            println("Loaded: ${it.joinToString()}")
        }
}

private suspend fun scroll(videos: List<String>){
    delay((videos.size * 100L).milliseconds)
    println("Scrolled: ${videos.joinToString()}")
}

private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)

fun main() {

    scope.launch {
        flow {
            repeat(10) {
                emit(loadVideo())
            }
        }.take(2).collect { scroll(it) }
    }

}