package net.streamok.lib.vertx

import io.vertx.core.Handler

import java.util.concurrent.atomic.AtomicInteger

class Handlers {

    static def <T> void completeIteration(Iterable<T> iterable, Handler<IterationContext<T>> handler) {
        def sempahore = new AtomicInteger(iterable.size())
        iterable.forEach {
            handler.handle(new IterationContext(it, sempahore))
        }
    }

    static class IterationContext<T> {

        private final T element

        private final AtomicInteger semaphore

        IterationContext(T element, AtomicInteger semaphore) {
            this.element = element
            this.semaphore = semaphore
        }

        void ifFinished(Closure closure) {
            if(semaphore.decrementAndGet() == 0) {
                closure()
            }
        }

        T element() {
            return element
        }

    }

}
