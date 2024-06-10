package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve int values
 */
public class IntPropertyInitializer extends AbstractPropertyInitializerByTypeOrPrimitive{

    public IntPropertyInitializer() {
        super("Integer", "int");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> faker.number().positive()");
    }
}
