package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve Calendar values
 */
public class CalendarPropertyInitializer extends AbstractPropertyInitializerByType{

    public CalendarPropertyInitializer() {
        super("java.util.Calendar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer(
                """
                () -> {
                    var calendar = Calendar.getInstance();
                    calendar.setTime(faker.date().birthday());
                    return calendar;
                }
                """
        );
    }
}
