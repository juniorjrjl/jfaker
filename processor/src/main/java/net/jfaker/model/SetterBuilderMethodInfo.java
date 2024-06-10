package net.jfaker.model;

import javax.lang.model.type.TypeMirror;
import java.util.Objects;

import static net.jfaker.util.StringUtil.firstLetterToUpperCase;

/**
 * Class to represent setter builder method ( method who receive a Supplier of some property and return an own bot instance)
 */
public class SetterBuilderMethodInfo{

    private final String name;
    private final String parameterName;
    private final TypeMirror originalType;

    /**
     * Class constructor
     * @param propertyName property name used to generate method name, ex. property name will generate withName
     * @param originalType type used in generate class property, in bot generation this type become Supplier of this type
     */
    private SetterBuilderMethodInfo(final String propertyName, final TypeMirror originalType) {
        this.name = "with" + firstLetterToUpperCase(propertyName);
        this.parameterName = propertyName;
        this.originalType = originalType;
    }

    public static SetterBuilderMethodInfoBuilder builder(){
        return new SetterBuilderMethodInfoBuilder();
    }

    public String getName() {
        return name;
    }

    public String getParameterName() {
        return parameterName;
    }

    public TypeMirror getOriginalType() {
        return originalType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SetterBuilderMethodInfo that)) return false;
        return Objects.equals(name, that.name) &&
                Objects.equals(parameterName, that.parameterName) &&
                Objects.equals(originalType, that.originalType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameterName, originalType);
    }

    @Override
    public String toString() {
        return "SetterBuilderMethodInfo{" +
                "name='" + name + '\'' +
                ", parameterName='" + parameterName + '\'' +
                ", originalType=" + originalType +
                '}';
    }

    /**
     * Builder class
     */
    public static final class SetterBuilderMethodInfoBuilder {

        private String propertyName;
        private TypeMirror originalType;

        public SetterBuilderMethodInfoBuilder withPropertyName(final String propertyName){
            this.propertyName = propertyName;
            return this;
        }

        public SetterBuilderMethodInfoBuilder withOriginalType(TypeMirror originalType) {
            this.originalType = originalType;
            return this;
        }

        public SetterBuilderMethodInfo build() {
            return new SetterBuilderMethodInfo(propertyName, originalType);
        }
    }
}
