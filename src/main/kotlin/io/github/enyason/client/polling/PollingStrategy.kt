package io.github.enyason.client.polling

import io.github.enyason.client.task.Task

/**
 * An interface defining a strategy for polling the status and results of a [Task].
 * This interface allows for implementing different polling mechanisms for various task types.
 *
 * @param <RESULT> The type of data associated with the task result.
 * @see [Task]
 */
interface PollingStrategy<RESULT> {
    /**
     * Polls for the status and result of a task.
     * This method is expected to be implemented by concrete strategies to retrieve task updates
     * using the provided task ID and any optional arguments. The polling logic involves
     * a delay between attempts until the task reaches a terminal state or an error occurs.
     *
     * @param taskId the ID of the task to poll
     * @param extraArgs optional arguments that might be specific to the polling strategy implementation
     * @return an updated `Task` object reflecting the final state of the task after polling
     */
    suspend fun pollTask(
        taskId: String,
        extraArgs: Map<String, Any>?,
    ): Task<RESULT>
}
