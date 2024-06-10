package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve char values
 */
public class CharPropertyInitializer extends AbstractPropertyInitializerByTypeOrPrimitive{

    public CharPropertyInitializer() {
        super("Character", "char");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> faker.lorem().character()");
    }
}
