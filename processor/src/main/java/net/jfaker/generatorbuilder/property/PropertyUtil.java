package net.jfaker.generatorbuilder.property;

import net.jfaker.generatorbuilder.property.bot.ListBotPropertyInitializer;
import net.jfaker.generatorbuilder.property.bot.SingleBotPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.BigDecimalPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.BooleanPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.BytePropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.CalendarPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.CharPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.DatePropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.DoublePropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.FloatPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.IntPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.LocalDatePropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.LocalDateTimePropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.LongPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.OffsetDateTimePropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.ShortPropertyInitializer;
import net.jfaker.generatorbuilder.property.classtype.StringPropertyInitializer;

import java.util.List;

public class PropertyUtil {

    private PropertyUtil(){

    }

    /**
     * list with properties initializer
     */
    public static final List<IPropertyInitializer> PROPERTIES_INITIALIZERS = List.of(
                new ListBotPropertyInitializer(),
                new SingleBotPropertyInitializer(),
                new BooleanPropertyInitializer(),
                new BigDecimalPropertyInitializer(),
                new BytePropertyInitializer(),
                new CalendarPropertyInitializer(),
                new CharPropertyInitializer(),
                new DatePropertyInitializer(),
                new DoublePropertyInitializer(),
                new FloatPropertyInitializer(),
                new IntPropertyInitializer(),
                new LocalDatePropertyInitializer(),
                new LocalDateTimePropertyInitializer(),
                new LongPropertyInitializer(),
                new OffsetDateTimePropertyInitializer(),
                new ShortPropertyInitializer(),
                new StringPropertyInitializer()
    );

}
