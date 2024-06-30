package net.jfaker.exception;

/**
 * Exception to warn about errors in {@link net.jfaker.annotation.FakerInfo FakerInfo}
 */
public class FakerInfoException extends JFakerException{

    public FakerInfoException(final String message) {
        super(message);
    }
}
