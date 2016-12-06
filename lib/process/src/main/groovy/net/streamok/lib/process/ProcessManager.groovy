package net.streamok.lib.process

import java.util.concurrent.Future

/**
 * Executes and manages system processes.
 */
interface ProcessManager {

    boolean canExecute(Command command)

    /**
     * Executes command and returns output.
     *
     * @param command command to execute, where each command segment is separated by space.
     * @return standard output and standard error combined into a single list. Each line of collected output is
     * represented as a line of returned list.
     */
    List<String> execute(Command command)

    Future<List<String>> executeAsync(Command command)

}