package net.jfaker.factory;

import net.jfaker.annotation.BotBuildStrategy;
import net.jfaker.model.BuildMethodInfo;
import net.jfaker.model.BuildMethodInfoBuilderStrategy;
import net.jfaker.model.BuildMethodInfoConstructorStrategy;
import net.jfaker.model.BuildMethodInfoSetterStrategy;
import net.jfaker.model.BuilderInstantiateMethod;
import net.jfaker.model.ClassInfo;
import net.jfaker.model.SetterBuilderStatement;
import net.jfaker.model.SetterStatement;

import javax.lang.model.util.Elements;
import java.util.List;

import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;

/**
 * class to generate {@link BuildMethodInfo net.jfaker.model.BuildMethodInfo} instance
 */
public class BuildFactory {

    /**
     * method to receive information and generate correct implementation
     * @param generatedClass class generated by bot
     * @param generatedSuperclass superclass of generated class, used for polymorphic bot
     * @param propertiesName properties will be used by bot
     * @param elementUtils class used to create type element from qualified name
     * @param botBuildStrategy strategy used by bot to generate instances
     * @return class with information to generate build method
     */
    public static BuildMethodInfo initializeBuildMethod(final ClassInfo generatedClass,
                                                        final ClassInfo generatedSuperclass,
                                                        final List<String> propertiesName,
                                                        final Elements elementUtils,
                                                        final BotBuildStrategy botBuildStrategy){
        final var setterStrategy = botBuildStrategy.setterStrategy();
        final var constructorStrategy = botBuildStrategy.constructorStrategy();
        final var builderStrategy = botBuildStrategy.builderStrategy();
        if (!setterStrategy.ignoreConfig()){
            return initializeBuildMethodSetterStrategy(generatedClass, generatedSuperclass, propertiesName);
        } else if (!constructorStrategy.ignoreConfig()) {
            return initializeBuildMethodConstructorStrategy(generatedClass, generatedSuperclass, propertiesName);
        } else {
            final var builderGeneratedClass = new ClassInfo(
                    builderStrategy.builderQualifiedName(),
                    elementUtils.getTypeElement(builderStrategy.builderQualifiedName()),
                    builderStrategy.builderClassIsInner()
            );
            return initializeBuildMethodBuilderStrategy(
                    generatedSuperclass,
                    propertiesName,
                    builderStrategy.prefixMethods(),
                    builderStrategy.instantiateMethod() == DIRECT_INSTANTIATE ? builderGeneratedClass : generatedClass,
                    builderStrategy.staticBuilderInvocation(),
                    builderStrategy.buildMethod(),
                    builderStrategy.instantiateMethod()
            );
        }

    }

    /**
     * generate instance with information to generate build method using setters
     * @param generatedClass class generated by bot
     * @param generatedSuperclass superclass of generated class, used for polymorphic bot
     * @param propertiesName  properties will be used by bot
     * @return build method using setter strategy
     */
    private static BuildMethodInfo initializeBuildMethodSetterStrategy(final ClassInfo generatedClass,
                                                                       final ClassInfo generatedSuperclass,
                                                                       final List<String> propertiesName){
        return BuildMethodInfoSetterStrategy.builder()
                .withName("build")
                .withResultClass(generatedSuperclass)
                .withUsedInStatement(generatedClass)
                .withSettersStatement(propertiesName
                        .stream()
                        .map(SetterStatement::new)
                        .toList())
                .withSettersValues(propertiesName)
                .build();
    }

    /**
     * generate instance with information to generate build method using constructor
     * @param generatedClass class generated by bot
     * @param generatedSuperclass superclass of generated class, used for polymorphic bot
     * @param propertiesName  properties will be used by bot
     * @return build method using constructor strategy
     */
    private static BuildMethodInfo initializeBuildMethodConstructorStrategy(final ClassInfo generatedClass,
                                                                            final ClassInfo generatedSuperclass,
                                                                            final List<String> propertiesName){
        return BuildMethodInfoConstructorStrategy.builder()
                .withName("build")
                .withResultClass(generatedSuperclass)
                .withUsedInStatement(generatedClass)
                .withSettersValues(propertiesName)
                .build();
    }

    /**
     *
     * @param generatedSuperclass superclass of generated class, used for polymorphic bot
     * @param propertiesName properties will be used by bot
     * @param builderPrefix builder method prefix
     * @param usedInStatement class used in build statement to create instance
     * @param builderMethodName builder method name used by bot
     * @param buildMethodName build method name used by bot
     * @param builderInstantiateMethod builder strategy used to create class
     * @return build method using builder strategy
     */
    private static BuildMethodInfo initializeBuildMethodBuilderStrategy(final ClassInfo generatedSuperclass,
                                                                        final List<String> propertiesName,
                                                                        final String builderPrefix,
                                                                        final ClassInfo usedInStatement,
                                                                        final String builderMethodName,
                                                                        final String buildMethodName,
                                                                        final BuilderInstantiateMethod builderInstantiateMethod){
        return BuildMethodInfoBuilderStrategy.builder()
                .withName("build")
                .withResultClass(generatedSuperclass)
                .withUsedInStatement(usedInStatement)
                .withBuilderMethodName(builderMethodName)
                .withSettersStatement(propertiesName
                        .stream()
                        .map(p -> new SetterBuilderStatement(builderPrefix, p))
                        .toList())
                .withSettersValues(propertiesName)
                .withBuildMethodName(buildMethodName)
                .withBuilderInstantiateMethod(builderInstantiateMethod)
                .build();
    }

}
