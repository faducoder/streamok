package net.streamok.lib.process

import net.streamok.lib.common.Closeable
import org.slf4j.Logger

import java.util.concurrent.Callable;
import java.util.concurrent.Future

import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newCachedThreadPool
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Base process manager using JDK Executor Framework for handling asynchronous command invocations.
 */
abstract class ExecutorBasedProcessManager implements ProcessManager, Closeable<ExecutorBasedProcessManager> {

    protected final Logger log = getLogger(getClass())

    private final executor = newCachedThreadPool()

    boolean canExecute(Command command) {
        try {
            execute(command)
            true
        } catch (ProcessExecutionException e) {
            log.debug("Cannot execute command " + asList(command) + " because of:", e)
            false
        }
    }

    @Override
    Future<List<String>> executeAsync(Command command) {
        executor.submit({execute(command)} as Callable<List<String>>)
    }

    ExecutorBasedProcessManager close() {
        executor.shutdown()
        this
    }

    static String[] command(String command) {
        command.split(/\s+(?=([^"]*"[^"]*")*[^"]*$)/) // don't split values by whitespaces inside " "
    }

}