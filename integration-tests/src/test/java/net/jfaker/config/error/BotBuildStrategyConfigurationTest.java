package net.jfaker.config.error;

import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import net.jfaker.processor.BotProcessor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BotBuildStrategyConfigurationTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "@BotBuildStrategy",
            """
            @BotBuildStrategy(
                constructorStrategy = @ConstructorStrategy(ignoreConfig = false),
                builderStrategy = @BuilderStrategy(ignoreConfig = false)
            )
            """,
            """
            @BotBuildStrategy(
                constructorStrategy = @ConstructorStrategy(ignoreConfig = false),
                setterStrategy = @SetterStrategy(ignoreConfig = false)
            )
            """,
            """
            @BotBuildStrategy(
                setterStrategy = @SetterStrategy(ignoreConfig = false),
                builderStrategy = @BuilderStrategy(ignoreConfig = false)
            )
            """,
            """
            @BotBuildStrategy(
                constructorStrategy = @ConstructorStrategy(ignoreConfig = false),
                builderStrategy = @BuilderStrategy(ignoreConfig = false),
                setterStrategy = @SetterStrategy(ignoreConfig = false)
            )
            """
    })
    void whenMoreThanOneStrategyOrAnyStrategyIsChosenThenWaringIt(final String botBuildStrategyConfig){
        final var botConfig= JavaFileObjects.forSourceLines(
                "net.jfaker.config.CustomFaker",
                """
                       package net.jfaker.config;
                       
                       import net.datafaker.Faker;
                       import net.jfaker.annotation.AutoFakerBot;
                       import net.jfaker.annotation.BotBuildStrategy;
                       import net.jfaker.annotation.FakerInfo;
                       import net.jfaker.annotation.BuilderStrategy;
                       import net.jfaker.annotation.ConstructorStrategy;
                       import net.jfaker.annotation.SetterStrategy;
                       
                       @FakerInfo(
                           botsConfiguration = @AutoFakerBot(
                               packageToGenerate = "net.jfaker.bot",
                               generatedInstance = "net.jfaker.model.SampleDTO",
                               botBuildStrategy = %s
                           )
                       )
                       public class CustomFaker extends Faker {}
                       
                       """.formatted(botBuildStrategyConfig));
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(botConfig);
        CompilationSubject.assertThat(compilation).failed();

        assertThat(compilation.errors()).isNotEmpty();
        final List<String> errors = compilation.errors().stream()
                .map(e -> e.getMessage(null))
                .toList();
        assertThat(errors).contains("you must choose only 1 configuration in botBuildStrategy and use it");
    }

}
