package syncronization

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

class Counter {

    var value = 0
    var lock = Any()
    var mutex = Mutex()

    suspend fun inc() {

        // The first solution
      /*  mutex.withLock {
            delay(1.milliseconds)
            value++
        }*/

        // The second solution
        mutex.lock()
        delay(1.milliseconds)
        try {
            value++
        } finally {
            mutex.unlock()
        }
    }

}

val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
val scope = CoroutineScope(dispatcher)

fun main() {
    val counter = Counter()

    scope.launch {
        buildList {
            repeat(100) {
                scope.launch {
                    repeat(10) {
                        counter.inc()
                    }
                }.let { add(it) }
            }
        }.joinAll()

        println(counter.value)
    }


}