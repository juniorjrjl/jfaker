package net.jfaker.model;

import javax.lang.model.type.TypeMirror;
import java.util.Objects;

/**
 * Class to represent faker instance inside of bot
 */
public class FakerPropertyInfo {

    private final TypeMirror type;
    private final String name;
    private final String initialization;

    /**
     * Constructor
     * @param type TypeMirror of Faker
     * @param simpleName simpleName of Faker Class used
     */
    public FakerPropertyInfo(final TypeMirror type, final String simpleName) {
        this.type = type;
        this.name = "faker";
        this.initialization = simpleName + "()";
    }

    public TypeMirror getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getInitialization() {
        return initialization;
    }

}
