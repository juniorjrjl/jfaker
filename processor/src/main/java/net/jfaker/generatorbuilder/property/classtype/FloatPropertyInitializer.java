package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve float values
 */
public class FloatPropertyInitializer extends AbstractPropertyInitializerByTypeOrPrimitive{

    public FloatPropertyInitializer() {
        super("Float", "float");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> (float) faker.number().randomDouble(2, 1, 999)");
    }
}
