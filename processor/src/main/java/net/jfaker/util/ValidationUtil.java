package net.jfaker.util;

import net.jfaker.annotation.AutoFakerBot;
import net.jfaker.annotation.BotBuildStrategy;
import net.jfaker.annotation.BuilderStrategy;
import net.jfaker.annotation.FakerInfo;
import net.jfaker.annotation.FieldConfiguration;
import net.jfaker.model.ClassInfo;

import javax.annotation.processing.Messager;

import java.util.stream.Stream;

import static javax.tools.Diagnostic.Kind.ERROR;
import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;
import static net.jfaker.util.ClassInfoUtil.noExtendsClass;

public class ValidationUtil {

    private ValidationUtil(){

    }

    /**
     * check annotation @FakerInfo have valid configuration
     * @param messager used to propagate errors
     * @param fakerInfoAnnotation annotation info
     * @param fakerInfo element information annotated with @FakerInfo
     */
    public static void validateFakerInfo(final Messager messager,
                                         final FakerInfo fakerInfoAnnotation,
                                         final ClassInfo fakerInfo){
        if (fakerInfoAnnotation.botsConfiguration().length == 0){
            messager.printMessage(ERROR, "no bot configuration detected in 'botsConfiguration' property");
        }
        if (noExtendsClass(fakerInfo.getTypeElement(), "net.datafaker.Faker")){
            messager.printMessage(ERROR, "an annotation @FakerInfo is used only 'net.datafaker.Faker' implementations");
        }
    }

    /**
     * check annotation @AutoFakerBot have a valid configuration
     * @param messager used to propagate errors
     * @param autoFakerBot annotation info
     */
    public static void validateAutoFakerBot(final Messager messager, final AutoFakerBot autoFakerBot){
        if (autoFakerBot.abstractBotQualifiedName().isBlank()){
            messager.printMessage(ERROR, "enter with a custom abstract bot class or keep default value");
        }
        if (autoFakerBot.generatedInstance().isBlank()){
            messager.printMessage(ERROR, "you must say a qualified name of instance bot will generate");
        }
        if (autoFakerBot.packageToGenerate().isBlank()){
            messager.printMessage(ERROR, "you must say a package where bot will be generated");
        }
        validateBotBuildStrategy(messager, autoFakerBot.botBuildStrategy());
    }

    /**
     * check annotation @BotBuildStrategy have a valid configuration
     * @param messager used to propagate errors
     * @param botBuildStrategy annotation info
     */
    private static void validateBotBuildStrategy(final Messager messager, final BotBuildStrategy botBuildStrategy){
        final var builderStrategy = botBuildStrategy.builderStrategy();
        final var setterStrategy = botBuildStrategy.setterStrategy();
        final var constructorStrategy = botBuildStrategy.constructorStrategy();
        final var amountEnable = Stream.of(
                        builderStrategy.ignoreConfig(),
                        setterStrategy.ignoreConfig(),
                        constructorStrategy.ignoreConfig())
                .filter(v -> !v)
                .count();
        if (amountEnable != 1L)
        {
            messager.printMessage(ERROR, "you must choose only 1 configuration in botBuildStrategy and use it");
        }
    }

    /**
     * check annotation @BuilderStrategy have a valid configuration
     * @param messager used to propagate errors
     * @param builderStrategy annotation info
     */
    public static void validateBuilderStrategy(final Messager messager, final BuilderStrategy builderStrategy){
        if (builderStrategy.builderQualifiedName().isBlank()){
            messager.printMessage(ERROR, "you must say a builder class qualified name to use builderStrategy");
        }
    }

    public static void validateFieldConfiguration(final Messager messager, FieldConfiguration fieldConfiguration){
        if ((!fieldConfiguration.fakerProvider().isBlank()) && (!fieldConfiguration.botSource().isBlank())){
            messager.printMessage(ERROR, "you must choose 1 source for your customization");
        }
    }

}
