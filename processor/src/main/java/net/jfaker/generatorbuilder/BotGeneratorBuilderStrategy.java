package net.jfaker.generatorbuilder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import net.jfaker.generatorbuilder.property.IPropertyInitializer;
import net.jfaker.model.BuildMethodInfoBuilderStrategy;

import javax.lang.model.util.Types;
import java.util.List;

import static javax.lang.model.element.Modifier.PUBLIC;
import static net.jfaker.model.BuilderInstantiateMethod.STATIC_METHOD_CLASS;

/**
 * Implementation for create bots who use builder strategy in build method
 */
public class BotGeneratorBuilderStrategy extends BotGenerator<BuildMethodInfoBuilderStrategy>{


    public BotGeneratorBuilderStrategy(final Types types, final List<IPropertyInitializer> propertyInitializers) {
        super(types, propertyInitializers);
    }

    /**
     * Method who generate build method in bot using builder strategy to create instance
     * @param buildMethodInfo information about build's bot method
     * @return build method using constructor strategy
     */
    @Override
    protected MethodSpec generateBuild(final BuildMethodInfoBuilderStrategy buildMethodInfo) {
        final var settersBuilderStatement = buildMethodInfo.getSettersBuilderStatement();
        final var settersValues = buildMethodInfo.getSettersValues();
        final var resultClass = buildMethodInfo.getResultClass();
        final var builder = MethodSpec.methodBuilder(buildMethodInfo.getName())
                .addModifiers(PUBLIC)
                .returns(ClassName.get(resultClass.getPackageName(), resultClass.getSimpleName()));
        final var statementBuilder = new StringBuilder();
        if (buildMethodInfo.getBuilderInstantiateMethod() == STATIC_METHOD_CLASS){
            statementBuilder.append(String.format("return %s.%s\n", buildMethodInfo.getUsedInStatement().getSimpleName(), buildMethodInfo.getBuilderMethodName()));
            for (int i = 0; i < settersBuilderStatement.size(); i++) {
                statementBuilder.append(String.format("    .%s(%s.get())\n", settersBuilderStatement.get(i).getName(), settersValues.get(i)));
            }
        } else {
            builder.addStatement("var builder = new $N()", buildMethodInfo.getUsedInStatement().getSimpleName());
            statementBuilder.append(String.format(" return builder.%s(%s.get())\n", settersBuilderStatement.get(0).getName(), settersValues.get(0)));
            for (int i = 1; i < settersBuilderStatement.size(); i++) {
                statementBuilder.append(String.format("    .%s(%s.get())\n", settersBuilderStatement.get(i).getName(), settersValues.get(i)));
            }
        }
        statementBuilder.append(String.format("    .%s", buildMethodInfo.getBuildMethodName()));
        builder.addStatement(statementBuilder.toString());
        return builder.addJavadoc("Build a instance of {@link $N $N} using builder \n", resultClass.getQualifiedName(), resultClass.getSimpleName())
                .addJavadoc("@return a instance of {@link $N $N} wirth random data", resultClass.getQualifiedName(), resultClass.getSimpleName())
                .build();
    }

}
