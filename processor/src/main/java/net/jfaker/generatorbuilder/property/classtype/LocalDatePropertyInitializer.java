package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve LocalDate values
 */
public class LocalDatePropertyInitializer extends AbstractPropertyInitializerByType{

    public LocalDatePropertyInitializer() {
        super("java.time.LocalDate");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> faker.date().birthdayLocalDate()");
    }
}
