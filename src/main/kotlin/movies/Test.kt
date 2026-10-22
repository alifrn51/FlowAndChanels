package movies

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime


private val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
private val scope = CoroutineScope(dispatcher)
fun main() {

    val job = scope.launch {
        val time = measureTime {
            loadMovie()
        }
        println(time)
    }

    scope.launch {
        delay(4000.milliseconds)
        job.cancel()
    }


}

private suspend fun loadMoviesIds(): List<Int> {
    delay(3_000.milliseconds)
    return (0..100).toList()
}

private suspend fun loadMovieById(id: Int): String {
    delay(5000.milliseconds)
    return "Movie $id"
}

private suspend fun loadMovie(): List<String> {
    return coroutineScope {
        loadMoviesIds().map { it ->
            async {
                loadMovieById(it).also {
                    println(it)
                }
            }
        }.awaitAll()
    }
}
