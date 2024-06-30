package net.jfaker.exception;

/**
 * Exception to warn about errors in {@link net.jfaker.annotation.AutoFakerBot AutoFakerBot}
 */
public class AutoFakerBotException extends JFakerException{

    public AutoFakerBotException(final String message) {
        super(message);
    }

}
