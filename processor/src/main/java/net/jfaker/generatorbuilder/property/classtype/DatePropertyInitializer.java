package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve Date values
 */
public class DatePropertyInitializer extends AbstractPropertyInitializerByType{

    public DatePropertyInitializer() {
        super("java.util.Date");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> faker.date().birthday()");
    }
}
