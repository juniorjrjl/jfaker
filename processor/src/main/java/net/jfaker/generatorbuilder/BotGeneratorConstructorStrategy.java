package net.jfaker.generatorbuilder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import net.jfaker.generatorbuilder.property.IPropertyInitializer;
import net.jfaker.model.BuildMethodInfoConstructorStrategy;

import javax.lang.model.util.Types;
import java.util.List;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Implementation for create bots who use constructor strategy in build method
 */
public class BotGeneratorConstructorStrategy extends BotGenerator<BuildMethodInfoConstructorStrategy> {

    public BotGeneratorConstructorStrategy(final Types types, final List<IPropertyInitializer> propertyInitializers) {
        super(types, propertyInitializers);
    }

    /**
     * Method who generate build method in bot using constructor strategy to create instance
     * @param buildMethodInfo information about build's bot method
     * @return build method using constructor strategy
     */
    @Override
    protected MethodSpec generateBuild(final BuildMethodInfoConstructorStrategy buildMethodInfo) {
        final var params = buildMethodInfo.getSettersValues().stream()
                .map(p -> p + ".get(),\n")
                .reduce("", (s, s2) -> s.isEmpty() ? s2  : s + s2);
        final var usedInStatement = buildMethodInfo.getUsedInStatement();
        final var resultClass = buildMethodInfo.getResultClass();
        return MethodSpec.methodBuilder(buildMethodInfo.getName())
                .addModifiers(PUBLIC)
                .returns(ClassName.get(resultClass.getPackageName(), resultClass.getSimpleName()))
                .addStatement(
                        "return new $N(\n$N\n)",
                        usedInStatement.getSimpleName(),
                        params.substring(0, params.length() -2)
                )
                .addJavadoc(
                        "Build a instance of {@link $N $N} using constructor \n",
                        usedInStatement.getQualifiedName(),
                        usedInStatement.getSimpleName()
                )
                .addJavadoc(
                        "@return a instance of {@link $N $N} wirth random data",
                        usedInStatement.getQualifiedName(),
                        usedInStatement.getSimpleName()
                )
                .build();
    }

}
