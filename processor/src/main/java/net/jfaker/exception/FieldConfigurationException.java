package net.jfaker.exception;

/**
 * Exception to warn about errors in {@link net.jfaker.annotation.FieldConfiguration FieldConfiguration}
 */
public class FieldConfigurationException extends JFakerException{

    public FieldConfigurationException(final String message) {
        super(message);
    }

}
