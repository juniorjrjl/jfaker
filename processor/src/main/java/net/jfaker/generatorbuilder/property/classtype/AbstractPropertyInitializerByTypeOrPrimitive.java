package net.jfaker.generatorbuilder.property.classtype;

import net.jfaker.generatorbuilder.property.IPropertyInitializer;
import net.jfaker.model.BotClassInfo;
import net.jfaker.model.PropertyInfo;

/**
 * abstract class for types who have primitive values
 */
public abstract class AbstractPropertyInitializerByTypeOrPrimitive implements IPropertyInitializer {

    private final String className;
    private final String primitive;

    /**
     * @param className Wrapper simple name class
     * @param primitive primitive representation
     */
    protected AbstractPropertyInitializerByTypeOrPrimitive(final String className, final String primitive) {
        this.className = "java.lang." + className;
        this.primitive = primitive;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEligible(final BotClassInfo botClassInfo, final PropertyInfo propertyInfo){
        return propertyInfo.originalType().toString().equals(className) ||
                propertyInfo.originalType().toString().equals(primitive);
    }

}
