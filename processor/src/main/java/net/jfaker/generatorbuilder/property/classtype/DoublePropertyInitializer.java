package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve double values
 */
public class DoublePropertyInitializer extends AbstractPropertyInitializerByTypeOrPrimitive{

    public DoublePropertyInitializer() {
        super("Double", "double");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> faker.number().randomDouble(2, 1, 999)");
    }
}
