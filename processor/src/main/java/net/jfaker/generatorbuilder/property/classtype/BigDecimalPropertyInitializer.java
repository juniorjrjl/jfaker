package net.jfaker.generatorbuilder.property.classtype;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import net.jfaker.model.PropertyInfo;

/**
 * class to solve BigDecimal values
 */
public class BigDecimalPropertyInitializer extends AbstractPropertyInitializerByType{

    public BigDecimalPropertyInitializer() {
        super("java.math.BigDecimal");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitializerBlock(final PropertyInfo propertyInfo, final FieldSpec.Builder builder) {
        builder.initializer(
                """
                () -> {
                           final var integerPart = faker.number().digits(3);
                           final var decimalPart = faker.number().digits(2);
                           return new $T(integerPart + "." + decimalPart);
                }
                """,
                ClassName.get("java.math", "BigDecimal")
        );
    }
}
