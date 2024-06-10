package net.jfaker.generatorbuilder.property;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.BotClassInfo;
import net.jfaker.model.PropertyInfo;

/**
 * used if any instance is eligible to solve property initialization
 */
public class NullablePropertyInitializer implements IPropertyInitializer{

    /**
     * {@inheritDoc}
     */
    public boolean isEligible(final BotClassInfo botClassInfo, final PropertyInfo propertyInfo){
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder){
        builder.initializer("() -> null");
    }

}
