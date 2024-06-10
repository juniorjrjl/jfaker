package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve long values
 */
public class LongPropertyInitializer extends AbstractPropertyInitializerByTypeOrPrimitive{

    public LongPropertyInitializer() {
        super("Long", "long");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> (long) faker.number().positive()");
    }
}
