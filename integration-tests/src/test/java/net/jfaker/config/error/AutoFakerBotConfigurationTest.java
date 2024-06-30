package net.jfaker.config.error;

import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import net.jfaker.processor.BotProcessor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoFakerBotConfigurationTest {

    @Test
    void whenSetAbstractBotClassEmptyThenWaringIt(){
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
                           botsConfiguration = @AutoFakerBot(
                               abstractBotQualifiedName = "",
                               packageToGenerate = "net.jfaker.bot",
                               generatedInstance = "net.jfaker.model.SampleDTO"
                           )
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
        assertThat(errors).contains("enter with a custom abstract bot class or keep default value");
    }

    @Test
    void whenHasNonMappedPackageThenWaringIt() {
        final var botConfig = JavaFileObjects.forSourceLines(
                "net.jfaker.config.CustomFaker",
                """
                        package net.jfaker.config;
                        
                        import net.datafaker.Faker;
                        import net.jfaker.annotation.AutoFakerBot;
                         import net.jfaker.annotation.BotBuildStrategy;
                         import net.jfaker.annotation.FakerInfo;
                         import net.jfaker.annotation.ConstructorStrategy;
                        
                        @FakerInfo(
                            botsConfiguration = @AutoFakerBot(
                                packageToGenerate = "",
                                generatedInstance = "java.lang.Object"
                            )
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
        assertThat(errors).contains("you must say a package where bot will be generated");
    }

    @Test
    void whenHasNonTargetClassMappedThenWaringIt(){
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
                           botsConfiguration = @AutoFakerBot(
                               packageToGenerate = "net.jfaker.bot",
                               generatedInstance = ""
                           )
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
        assertThat(errors).contains("you must say a qualified name of instance bot will generate");
    }

}
