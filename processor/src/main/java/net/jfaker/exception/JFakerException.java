package net.jfaker.exception;

/**
 * Superclass of all exceptions used to warn error configuration
 */
public class JFakerException extends RuntimeException{

    public JFakerException(final String message){
        super(message);
    }

}
