package net.jfaker.config.error;

import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import net.jfaker.processor.BotProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BuilderStrategyConfigurationTest {

    @Test
    void whenUseBuilderStrategyWithoutBuilderQualifiedNameThenWaringIt(){
        final var generatedClass = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                       package net.jfaker.dto;
                       public record SampleDTO(String stub){}
                       """
        );
        final var botConfig= JavaFileObjects.forSourceLines(
                "net.jfaker.config.CustomFaker",
                """
                       package net.jfaker.config;
                       
                       import net.datafaker.Faker;
                       import net.jfaker.annotation.AutoFakerBot;
                       import net.jfaker.annotation.BotBuildStrategy;
                       import net.jfaker.annotation.FakerInfo;
                       import net.jfaker.annotation.BuilderStrategy;
                       
                       @FakerInfo(
                           botsConfiguration = @AutoFakerBot(
                               packageToGenerate = "net.jfaker.bot",
                               generatedInstance = "net.jfaker.dto.SampleDTO",
                               botBuildStrategy = @BotBuildStrategy(
                                   builderStrategy = @BuilderStrategy
                               )
                           )
                       )
                       public class CustomFaker extends Faker {}
                       
                       """);
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(generatedClass, botConfig);
        CompilationSubject.assertThat(compilation).failed();

        assertThat(compilation.errors()).isNotEmpty();
        final List<String> errors = compilation.errors().stream()
                .map(e -> e.getMessage(null))
                .toList();
        assertThat(errors).contains("you must say a builder class qualified name to use builderStrategy");
    }

}
