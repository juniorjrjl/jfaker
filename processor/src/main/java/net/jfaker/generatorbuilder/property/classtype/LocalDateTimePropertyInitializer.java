package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve Date values
 */
public class LocalDateTimePropertyInitializer extends AbstractPropertyInitializerByType{

    public LocalDateTimePropertyInitializer() {
        super("java.time.LocalDateTime");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer(
                """
                () -> $T.ofEpochMilli(faker.date().birthday().getTime())
                             .atZone($T.systemDefault())
                             .toLocalDateTime()
                """,ClassName.get("java.time", "Instant"), ClassName.get("java.time", "ZoneId")

        );
    }
}
