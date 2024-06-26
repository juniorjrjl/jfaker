package net.jfaker.generatorbuilder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import net.jfaker.generatorbuilder.property.IPropertyInitializer;
import net.jfaker.model.BuildMethodInfoSetterStrategy;
import net.jfaker.util.StringUtil;

import javax.lang.model.util.Types;
import java.util.List;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Implementation for create bots who use setter strategy in build method
 */
public class BotGeneratorSetterStrategy extends BotGenerator<BuildMethodInfoSetterStrategy> {

    public BotGeneratorSetterStrategy(final Types types, final List<IPropertyInitializer> propertyInitializers) {
        super(types, propertyInitializers);
    }

    /**
     * Method who generate build method in bot using setter strategy to create instance
     * @param buildMethodInfo information about build's bot method
     * @return build method using setter strategy
     */
    @Override
    protected MethodSpec generateBuild(final BuildMethodInfoSetterStrategy buildMethodInfo) {
        final var botResultInfo = buildMethodInfo.getResultClass();
        final var usedInStatement = buildMethodInfo.getUsedInStatement();
        final var settersStatement = buildMethodInfo.getSettersStatement();
        final var settersValues= buildMethodInfo.getSettersValues();
        final var varName = StringUtil.firstLetterToLowerCase(botResultInfo.getSimpleName());
        final var builder = MethodSpec.methodBuilder(buildMethodInfo.getName())
                .addModifiers(PUBLIC)
                .returns(ClassName.get(botResultInfo.getPackageName(), botResultInfo.getSimpleName()))
                .addStatement("var $N = new $N()", varName, usedInStatement.getQualifiedName());
        for (int i = 0; i < settersStatement.size(); i++) {
            builder.addStatement("$N.$N($N.get())", varName, settersStatement.get(i).name(), settersValues.get(i));
        }
        return builder.addStatement("return $N", varName)
                .addJavadoc("Build a instance of {@link $N $N} using setters \n", botResultInfo.getQualifiedName(), botResultInfo.getSimpleName())
                .addJavadoc("@return a instance of {@link $N $N} wirth random data", botResultInfo.getQualifiedName(), botResultInfo.getSimpleName())
                .build();
    }

}
