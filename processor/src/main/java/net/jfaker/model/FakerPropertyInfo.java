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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FakerPropertyInfo that)) return false;
        return Objects.equals(type, that.type) &&
                Objects.equals(name, that.name) &&
                Objects.equals(initialization, that.initialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, initialization);
    }

    @Override
    public String toString() {
        return "FakerPropertyInfo{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", initialization='" + initialization + '\'' +
                '}';
    }
}
