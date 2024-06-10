package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve short values
 */
public class ShortPropertyInitializer extends AbstractPropertyInitializerByTypeOrPrimitive{

    public ShortPropertyInitializer() {
        super("Short", "short");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> Short.parseShort(faker.number().digit())");
    }
}
