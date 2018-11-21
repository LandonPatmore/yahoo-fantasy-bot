package services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Service implements Runnable {
    private static final Logger log = LogManager.getLogger(Service.class);

    private boolean shouldUse = true;

    final String url;
    private String currentMessage;

    public Service(String url) {
        this.url = url;
    }

    /**
     * Creates a message.  Recursively creates messages when they are too large.
     *
     * @param message the message to be sent
     */
    abstract void createMessage(String message);

    /**
     * Sends a message to the group.
     *
     * @param message the message to send
     */
    abstract void sendMessage(String message);

    public void shouldNotUse() {
        this.shouldUse = false;
    }

    public boolean shouldUse() {
        return shouldUse;
    }

    String correctMessage(String message) {
        try {
            Thread.sleep(1000);
            if (message.endsWith("\\")) {
                message = message.substring(0, message.length() - 1);
            } else if (message.startsWith("n")) {
                message = message.substring(1);
            }

            return message;
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), new Throwable());

            return null;
        }
    }

    public void setCurrentMessage(String currentMessage) {
        this.currentMessage = currentMessage;
    }

    @Override
    public void run() {
        if(currentMessage != null) {
            createMessage(currentMessage);
            currentMessage = null;
        }
    }
}
