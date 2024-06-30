package net.jfaker.config.error;

import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import net.jfaker.processor.BotProcessor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FieldConfigurationTest {

    @ParameterizedTest
    @ValueSource(strings = {
            """
            builderStrategy = @BuilderStrategy(
                builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
                fieldsConfiguration = @FieldConfiguration(
                    name = "stub",
                    fakerProvider = "faker.lorem().word()",
                    botSource = "SampleDTOBot"
                )
            )
            """,
            """
            setterStrategy = @SetterStrategy(
                fieldsConfiguration = @FieldConfiguration(
                    name = "stub",
                    fakerProvider = "faker.lorem().word()",
                    botSource = "SampleDTOBot"
                )
            )
            """,
            """
            constructorStrategy = @ConstructorStrategy(
                fieldsConfiguration = @FieldConfiguration(
                    name = "stub",
                    fakerProvider = "faker.lorem().word()",
                    botSource = "SampleDTOBot"
                )
            )
            """
    })
    void whenUseFieldConfigurationWithBotSourceAndFakerProviderThenWaringIt(final String configuration){
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public record SampleDTO(String stub){
                      public static SampleDTOBuilder builder(){
                          return new SampleDTOBuilder();
                      }
                          public static class SampleDTOBuilder{
                              private String stub;
                              public SampleDTOBuilder withStub(final String stub){
                                  this.stub = stub;
                                  return this;
                              }
                              public SampleDTO build(){
                                  return new SampleDTO(stub);
                              }
                          }
                      }
                      """);
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
                       import net.jfaker.annotation.FieldConfiguration;
                       
                       @FakerInfo(
                           botsConfiguration = @AutoFakerBot(
                               packageToGenerate = "net.jfaker.bot",
                               generatedInstance = "net.jfaker.dto.SampleDTO",
                               botBuildStrategy =  @BotBuildStrategy(
                                   %s
                               )
                           )
                       )
                       public class CustomFaker extends Faker {}
                       
                       """.formatted(configuration));
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(toGenerateBot, botConfig);
        CompilationSubject.assertThat(compilation).failed();

        assertThat(compilation.errors()).isNotEmpty();
        final List<String> errors = compilation.errors().stream()
                .map(e -> e.getMessage(null))
                .toList();
        assertThat(errors).contains("you must choose 1 source for your customization");
    }

}
