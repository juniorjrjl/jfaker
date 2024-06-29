package net.jfaker.config;

import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import net.jfaker.bot.AbstractBot;
import net.jfaker.processor.BotProcessor;
import net.jfaker.util.BotAssert;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class AutoFakerCustomizationTest {

    @Test
    void whenSetCustomAbstractBotThenUseIt() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public record %s(String stub){}
                      """.formatted(generatedClass));
        final var customAbstractBot = JavaFileObjects.forSourceLines(
                "net.jfaker.customization.CustomAbstractBot",
                """
                       package net.jfaker.customization;
                       
                       import java.util.Set;
                       import java.util.stream.Stream;
                       import java.util.stream.Collectors;
                       import net.jfaker.bot.AbstractBot;
                       
                       public abstract class CustomAbstractBot<B> extends AbstractBot<B>{
                       
                           public Set<B> buildSet(final long size){
                               return Stream.generate(this::build)
                                        .limit(size)
                                        .collect(Collectors.toSet());
                           }
                       
                       }
                       """
        );
        final var botSimpleName = "SampleDTOBot";
        final var botGeneratedPackage = "net.jfaker.dto";
        final var botQualifiedName = "net.jfaker.bot.SampleDTOBot";
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
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy),
                                        abstractBotQualifiedName = "net.jfaker.customization.CustomAbstractBot"
                                                    )
                                }
                        )
                        public class CustomFaker extends Faker {}
                       
                       """.formatted(botGeneratedPackage, generatedClass));
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(toGenerateBot, customAbstractBot, botConfig);
        CompilationSubject.assertThat(compilation).succeeded();
        assertThat(compilation.generatedSourceFiles().size()).isOne();
        CompilationSubject.assertThat(compilation).generatedSourceFile(botQualifiedName);
        var classCode = compilation.generatedSourceFile(botQualifiedName).orElseThrow()
                .getCharContent(true).toString();

        BotAssert.assertThat(classCode, botSimpleName, generatedClass,  "CustomAbstractBot")
                .inheritsFrom();
    }

    @Test
    void whenSetCustomBotNameThenUseIt() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public record %s(String stub){}
                      """.formatted(generatedClass));
        final var botSimpleName = "SampleCustomBot";
        final var botGeneratedPackage = "net.jfaker.dto";
        final var botQualifiedName = "net.jfaker.bot.SampleCustomBot";
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
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy),
                                        botClassName = "SampleCustomBot"
                                                    )
                                }
                        )
                        public class CustomFaker extends Faker {}
                       
                       """.formatted(botGeneratedPackage, generatedClass));
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(toGenerateBot, botConfig);
        CompilationSubject.assertThat(compilation).succeeded();
        assertThat(compilation.generatedSourceFiles().size()).isOne();
        CompilationSubject.assertThat(compilation).generatedSourceFile(botQualifiedName);
        var classCode = compilation.generatedSourceFile(botQualifiedName).orElseThrow()
                .getCharContent(true).toString();

        BotAssert.assertThat(classCode, botSimpleName, generatedClass,  AbstractBot.class.getSimpleName())
                .hasName();
    }

    @Test
    void whenConfigurePolymorphicBotThenUseIt() throws IOException {
        final var generatedClass = "SampleDTO";
        final var superType = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.AbstractSampleDTO",
                """
                       package net.jfaker.dto;
                       public interface AbstractSampleDTO{}
                       """
        );
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                package net.jfaker.dto;
                public record %s(String stub) implements AbstractSampleDTO{}
                """.formatted(generatedClass));
        final var botSimpleName = "SampleDTOBot";
        final var botGeneratedPackage = "net.jfaker.dto";
        final var botQualifiedName = "net.jfaker.bot.SampleDTOBot";
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
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy),
                                        generatedInstanceSuperClass = "net.jfaker.dto.AbstractSampleDTO"
                                    )
                                }
                        )
                        public class CustomFaker extends Faker {}
                       
                       """.formatted(botGeneratedPackage, generatedClass));
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(superType, toGenerateBot, botConfig);
        CompilationSubject.assertThat(compilation).succeeded();
        assertThat(compilation.generatedSourceFiles().size()).isOne();
        CompilationSubject.assertThat(compilation).generatedSourceFile(botQualifiedName);
        var classCode = compilation.generatedSourceFile(botQualifiedName).orElseThrow()
                .getCharContent(true).toString();

        BotAssert.assertThat(classCode, botSimpleName, "AbstractSampleDTO",  AbstractBot.class.getSimpleName())
                .boGenerateSpecifiedType();
    }

}
