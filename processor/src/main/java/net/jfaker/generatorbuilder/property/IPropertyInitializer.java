package net.jfaker.generatorbuilder.property;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.BotClassInfo;
import net.jfaker.model.PropertyInfo;

/**
 * Interface used to set initialization for bot properties.
 */
public interface IPropertyInitializer {

    /**
     * Say if this instance is eligible to set initialization for current property
     * @param botClassInfo bot information
     * @param propertyInfo current property
     * @return if current instance is eligible or not
     */
    boolean isEligible(final BotClassInfo botClassInfo, final PropertyInfo propertyInfo);

    /**
     * set initialization for property
     * @param propertyInfo current property
     * @param builder bot information
     */
    void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder);

}
