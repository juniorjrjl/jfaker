package net.jfaker.exception;

/**
 * Exception to warn about errors in {@link net.jfaker.annotation.BotBuildStrategy BotBuildStrategy}
 */
public class BotBuildStrategyException extends JFakerException{

    public BotBuildStrategyException(final String message) {
        super(message);
    }

}
