package com.vprokopchuk.players;

import com.vprokopchuk.players.task.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

/**
 * This class represents a main method that creates two players and an executor service to handle their message exchange.
 * The class creates an ArrayBlockingQueue to hold the messages and sends an initial message to the initiator player.
 * The executor service submits the initiator player to a thread pool and the second player runs in a separate thread.
 * The class waits for the executor service to shutdown before terminating.
 */


public class MessageTask {

    /**
     * The maximum number of messages that can be held in the message queue.
     */
    private static final int MAX_MESSAGES_IN_QUEUE = 1;
    private static final Logger logger  = LoggerFactory.getLogger(MessageTask.class);

    /**
     * The main method of the class, which creates two player instances, an executor service and a message queue.
     * The initiator player sends an initial message and is submitted to the executor service. The second player runs
     * in a separate thread. The class waits for the executor service to shutdown before terminating.
     *
     * @param args the command-line arguments, which are not used in this implementation
     */

    public static void main(String[] args) {
        // Create a blocking queue to hold the messages
        BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(MAX_MESSAGES_IN_QUEUE);

        // Create the initiator and second player instances
        Player initiator = new Player(messageQueue);
        Player secondPlayer = new Player(messageQueue);
        // Create an executor service with a fixed thread pool of size 2
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // Submit the initiator player to the executor service and run the second player in a separate thread
        Future<Boolean> submit = executor.submit(initiator);
        executor.submit(secondPlayer);
        // Send an initial message from the initiator player
        initiator.sendInitMessage();

        // Wait for the executor service to shutdown before terminating
        try {
            if (submit.get()) {
                executor.shutdown();
                if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            executor.shutdownNow();
        } finally {
            executor.shutdownNow();
        }
    }

}
