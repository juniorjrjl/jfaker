package net.jfaker.exception;

/**
 * Exception to warn about errors in {@link net.jfaker.annotation.BotBuildStrategy BotBuildStrategy}
 */
public class BuilderStrategyException extends JFakerException{

    public BuilderStrategyException(final String message) {
        super(message);
    }

}
