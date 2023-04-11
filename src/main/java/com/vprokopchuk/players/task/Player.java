package com.vprokopchuk.players.task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class Player implements Callable<Boolean>

{

    private static final Logger logger  = LoggerFactory.getLogger(Player.class);
    ThreadLocal<Integer> counterHolder = ThreadLocal.withInitial(() -> 0);
    protected final BlockingQueue<String> messageQueue;
    private static final String INIT_MESSAGE = "first message";

    /**

     Constructs a new Player instance with the specified message queue.
     Initializes the message queue field with the specified queue.
     @param messageQueue The message queue.
     */
    public Player(BlockingQueue<String> messageQueue)
    {
        this.messageQueue = messageQueue;
    }


    /**

    * Executes the task asynchronously by looping until the instance has received 10 messages.
    * For each iteration of the loop, it calls the receive method to get the next message
    * from the message queue and then calls the reply method to reply to the message.
     @return A boolean value indicating whether the task completed successfully.
     */

    @Override
    public Boolean call() {
        while (counterHolder.get() < 10) {
            {
                String receivedMessage = receive();
                reply(receivedMessage);
            }
        }
        return true;
    }

    /**
     * Adds an initial message to the message queue to start the conversation.
     */

    public void sendInitMessage()
    {
        try
        {
            messageQueue.put(INIT_MESSAGE);
            logger.info("Player {} sent message {}",  this, INIT_MESSAGE);

        }
        catch (InterruptedException interrupted)
        {
            logger.error("Player {} failed to send message {}", this,  INIT_MESSAGE );
        }
    }

    /**
     * Waits for the next message to arrive on the message queue and returns it.
     * Catches any InterruptedException that may occur when waiting for the message and logs an error message if necessary.
     * @return The next message on the queue.
     *
     */

    protected String receive()
    {
        String receivedMessage = null;
        try
        {
            receivedMessage = messageQueue.take();
        }
        catch (InterruptedException interrupted)
        {
            logger.error("Player {} failed to receive message {}, counter {}", this,  INIT_MESSAGE, counterHolder.get() );
        }
        return receivedMessage;
    }


    /**
     * Takes a received message as input, creates a reply message by appending the current value
     * of the counter holder, and adds the reply message to the message queue. It also increments
     * the value of the counter holder and waits for 1 second before returning.
     * Catches any InterruptedException that may occur when adding the reply message to the queue and logs an error message if necessary.
     * @param receivedMessage The received message.
     */

    protected void reply(String receivedMessage)
    {
        String reply = receivedMessage + " " + counterHolder.get();
        try
        {
            messageQueue.put(reply);
            logger.info("Player {} reply message {}",  this, reply);
            Integer integer = counterHolder.get();
            counterHolder.set(++integer);
            Thread.sleep(1000);

        }
        catch (InterruptedException interrupted)
        {
            logger.error("Player {} failed to reply message {}, counter {}",  this, reply, counterHolder.get());
        }
    }
}