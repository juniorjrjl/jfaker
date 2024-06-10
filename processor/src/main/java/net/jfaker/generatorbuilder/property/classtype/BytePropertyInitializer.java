package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve byte values
 */
public class BytePropertyInitializer extends AbstractPropertyInitializerByTypeOrPrimitive{

    public BytePropertyInitializer() {
        super("Byte", "byte");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> Byte.parseByte(faker.number().digit())");
    }
}
