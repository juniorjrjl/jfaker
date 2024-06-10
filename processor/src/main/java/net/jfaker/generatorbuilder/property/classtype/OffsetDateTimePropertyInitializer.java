package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve OffsetDateTime values
 */
public class OffsetDateTimePropertyInitializer extends AbstractPropertyInitializerByType{

    public OffsetDateTimePropertyInitializer() {
        super("java.time.OffsetDateTime");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {

        builder.initializer(
                """
                () -> faker.date().birthday().toInstant().atOffset($T.UTC)
                """,
                ClassName.get("java.time", "ZoneOffset")

        );
    }
}
