package net.jfaker.model;

import javax.lang.model.type.TypeMirror;

import static java.util.Objects.nonNull;

/**
 * Class to represent properties inside of Bot
 * @param name property name
 * @param originalType property type extracted from Class who will be generated
 * @param customSource if property will be used custom provider this property is not empty and not blank
 * @param nullable if this property starts with null value
 */
public record PropertyInfo(String name,
                           TypeMirror originalType,
                           String customSource,
                           boolean nullable) {

    public static PropertyInfoBuilder builder(){
        return new PropertyInfoBuilder();
    }

    public boolean hasCustomSource() {
        return nonNull(customSource) && !customSource.isBlank();
    }

    /**
     * Builder class
     */
    public static final class PropertyInfoBuilder {

        private String name;
        private TypeMirror originalType;
        private String customSource;
        private boolean nullable;

        public PropertyInfoBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PropertyInfoBuilder withOriginalType(TypeMirror originalType) {
            this.originalType = originalType;
            return this;
        }

        public PropertyInfoBuilder withCustomSource(String customSource) {
            this.customSource = customSource;
            return this;
        }

        public PropertyInfoBuilder withNullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        public PropertyInfo build() {
            return new PropertyInfo(name, originalType, customSource, nullable);
        }
    }
}
