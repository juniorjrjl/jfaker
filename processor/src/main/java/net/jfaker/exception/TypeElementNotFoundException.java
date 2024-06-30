package net.jfaker.exception;

/**
 * Class to warn about some class use in faker config not found
 */
public class TypeElementNotFoundException extends JFakerException{

    public TypeElementNotFoundException(final String message) {
        super(message);
    }

}
