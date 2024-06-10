package net.jfaker.generatorbuilder.property.classtype;

import net.jfaker.generatorbuilder.property.IPropertyInitializer;
import net.jfaker.model.BotClassInfo;
import net.jfaker.model.PropertyInfo;

/**
 * abstract class for types who haven't primitive values
 */
public abstract class AbstractPropertyInitializerByType implements IPropertyInitializer {

    private final String className;

    /**
     * @param className Class qualified name solved
     */
    protected AbstractPropertyInitializerByType(final String className) {
        this.className = className;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEligible(final BotClassInfo botClassInfo, final PropertyInfo propertyInfo){
        return propertyInfo.originalType().toString().equals(className);
    }

}
