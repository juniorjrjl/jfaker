package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve boolean values
 */
public class BooleanPropertyInitializer extends AbstractPropertyInitializerByTypeOrPrimitive{

    public BooleanPropertyInitializer() {
        super("Boolean", "boolean");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> faker.bool().bool()");
    }
}
