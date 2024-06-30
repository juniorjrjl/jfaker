package net.jfaker.config.error;

import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import net.jfaker.processor.BotProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FakerInfoConfigurationTest {

    @Test
    void whenHasNonMappedBotThenWaringIt(){
        final var botConfig= JavaFileObjects.forSourceLines(
                "net.jfaker.config.CustomFaker",
                """
                       package net.jfaker.config;
                       
                       import net.datafaker.Faker;
                       import net.jfaker.annotation.AutoFakerBot;
                        import net.jfaker.annotation.BotBuildStrategy;
                        import net.jfaker.annotation.FakerInfo;
                        import net.jfaker.annotation.ConstructorStrategy;
                       
                       @FakerInfo(
                           botsConfiguration = {}
                       )
                       public class CustomFaker extends Faker {}
                       
                       """);
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(botConfig);
        CompilationSubject.assertThat(compilation).failed();

        assertThat(compilation.errors()).isNotEmpty();
        final List<String> errors = compilation.errors().stream()
                .map(e -> e.getMessage(null))
                .toList();
        assertThat(errors).contains("no bot configuration detected in 'botsConfiguration' property");
    }

    @Test
    void whenUseAnnotationInNonFakerImplThenWaringIt(){
        final var botConfig= JavaFileObjects.forSourceLines(
                "net.jfaker.config.CustomFaker",
                """
                       package net.jfaker.config;
                       
                       import net.datafaker.Faker;
                       import net.jfaker.annotation.AutoFakerBot;
                       import net.jfaker.annotation.FakerInfo;
                       
                       @FakerInfo(botsConfiguration = @AutoFakerBot(packageToGenerate = "", generatedInstance = ""))
                       public class CustomFaker {}
                       
                       """);
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(botConfig);
        CompilationSubject.assertThat(compilation).failed();

        assertThat(compilation.errors()).isNotEmpty();
        final List<String> errors = compilation.errors().stream()
                .map(e -> e.getMessage(null))
                .toList();
        assertThat(errors).contains("an annotation @FakerInfo is used only 'net.datafaker.Faker' implementations");
    }

}
