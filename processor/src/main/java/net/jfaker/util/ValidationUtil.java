package net.jfaker.util;

import net.jfaker.annotation.AutoFakerBot;
import net.jfaker.annotation.BotBuildStrategy;
import net.jfaker.annotation.BuilderStrategy;
import net.jfaker.annotation.FakerInfo;
import net.jfaker.annotation.FieldConfiguration;
import net.jfaker.exception.AutoFakerBotException;
import net.jfaker.exception.BotBuildStrategyException;
import net.jfaker.exception.BuilderStrategyException;
import net.jfaker.exception.FakerInfoException;
import net.jfaker.exception.FieldConfigurationException;
import net.jfaker.model.ClassInfo;

import java.util.stream.Stream;

import static net.jfaker.util.ClassInfoUtil.noExtendsClass;

public class ValidationUtil {

    private ValidationUtil(){

    }

    /**
     * check annotation @FakerInfo have valid configuration
     * @param fakerInfoAnnotation annotation info
     * @param fakerInfo element information annotated with @FakerInfo
     */
    public static void validateFakerInfo(final FakerInfo fakerInfoAnnotation, final ClassInfo fakerInfo){
        if (fakerInfoAnnotation.botsConfiguration().length == 0){
            throw new FakerInfoException("no bot configuration detected in 'botsConfiguration' property");
        }
        if (noExtendsClass(fakerInfo.getTypeElement(), "net.datafaker.Faker")){
            throw new FakerInfoException("an annotation @FakerInfo is used only 'net.datafaker.Faker' implementations");
        }
    }

    /**
     * check annotation @AutoFakerBot have a valid configuration
     * @param autoFakerBot annotation info
     */
    public static void validateAutoFakerBot(final AutoFakerBot autoFakerBot){
        if (autoFakerBot.abstractBotQualifiedName().isBlank()){
            throw new AutoFakerBotException("enter with a custom abstract bot class or keep default value");
        }
        if (autoFakerBot.generatedInstance().isBlank()){
            throw new AutoFakerBotException("you must say a qualified name of instance bot will generate");
        }
        if (autoFakerBot.packageToGenerate().isBlank()){
            throw new AutoFakerBotException("you must say a package where bot will be generated");
        }
        validateBotBuildStrategy(autoFakerBot.botBuildStrategy());
    }

    /**
     * check annotation @BotBuildStrategy have a valid configuration
     * @param botBuildStrategy annotation info
     */
    private static void validateBotBuildStrategy(final BotBuildStrategy botBuildStrategy){
        final var builderStrategy = botBuildStrategy.builderStrategy();
        final var setterStrategy = botBuildStrategy.setterStrategy();
        final var constructorStrategy = botBuildStrategy.constructorStrategy();
        final var amountEnable = Stream.of(
                        builderStrategy.ignoreConfig(),
                        setterStrategy.ignoreConfig(),
                        constructorStrategy.ignoreConfig())
                .filter(v -> !v)
                .count();
        if (amountEnable != 1L) {
            throw new BotBuildStrategyException("you must choose only 1 configuration in botBuildStrategy and use it");
        }
    }

    /**
     * check annotation @BuilderStrategy have a valid configuration
     * @param builderStrategy annotation info
     */
    public static void validateBuilderStrategy(final BuilderStrategy builderStrategy){
        if (builderStrategy.builderQualifiedName().isBlank()){
            throw new BuilderStrategyException("you must say a builder class qualified name to use builderStrategy");
        }
    }

    /**
     * check annotation @FieldConfiguration have a valid configuration
     * @param fieldConfiguration annotation info
     */
    public static void validateFieldConfiguration(FieldConfiguration fieldConfiguration){
        if ((!fieldConfiguration.fakerProvider().isBlank()) && (!fieldConfiguration.botSource().isBlank())){
            throw new FieldConfigurationException("you must choose 1 source for your customization");
        }
    }

}
