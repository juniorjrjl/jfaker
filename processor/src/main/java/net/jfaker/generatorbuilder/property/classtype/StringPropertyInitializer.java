package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve String values
 */
public class StringPropertyInitializer extends AbstractPropertyInitializerByType{

    public StringPropertyInitializer() {
        super("java.lang.String");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> faker.lorem().word()");
    }
}
