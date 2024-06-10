package net.jfaker.generatorbuilder.property.bot;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.generatorbuilder.property.IPropertyInitializer;
import net.jfaker.model.BotClassInfo;
import net.jfaker.model.PropertyInfo;

/**
 * used to solve properties who need a list of some class who have bots
 */
public class ListBotPropertyInitializer implements IPropertyInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEligible(final BotClassInfo botClassInfo, final PropertyInfo propertyInfo) {
        return propertyInfo.hasCustomSource() && propertyInfo.originalType().toString().contains("List<");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer("() -> $N.builder().build(faker.number().randomDigitNotZero())", propertyInfo.customSource());
    }


}
