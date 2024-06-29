package net.jfaker.config;

import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import net.jfaker.bot.AbstractBot;
import net.jfaker.processor.BotProcessor;
import net.jfaker.util.BotAssert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructorStrategyTest {

    @Test
    void DefaultConfigTest() throws IOException {
        final var generatedClass = "BookDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.BookDTO",
                """
                      package net.jfaker.dto;
                      public record %s(int id,
                                       String name,
                                       Integer year,
                                       Long pages,
                                       float price,
                                       Float discount,
                                       char firstLetter,
                                       Character lastLetter
                                       ){}
                      """.formatted(generatedClass));
        final var botSimpleName = "BookDTOBot";
        final var botGeneratedPackage = "net.jfaker.dto";
        final var botQualifiedName = "net.jfaker.bot.BookDTOBot";
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
                                                            botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy)
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass, AbstractBot.class.getSimpleName())
                .hasGeneratedInPackage("net.jfaker.bot")
                .inheritsFrom()
                .hasPrivateNonArgsConstructor()
                .hasPrivatePropertiesWithTypes(
                        Map.of("id","Integer",
                                "name","String",
                                "year","Integer",
                                "pages", "Long",
                                "price","Float",
                                "discount", "Float",
                                "firstLetter", "Character",
                                "lastLetter", "Character")
                )
                .hasPrivatePropertiesWithInitValue(
                        Map.of("id", "faker.number().positive()",
                                "name", "faker.lorem().word()",
                                "year", "faker.number().positive()",
                                "pages", "(long) faker.number().positive()",
                                "price", "(float) faker.number().randomDouble(2, 1, 999)",
                                "discount", "(float) faker.number().randomDouble(2, 1, 999)",
                                "firstLetter", "faker.lorem().character()",
                                "lastLetter", "faker.lorem().character()")
                ).containsWithForProperties(
                        Map.of("id","Integer",
                                "name","String",
                                "year","Integer",
                                "pages", "Long",
                                "price","Float",
                                "discount", "Float",
                                "firstLetter", "Character",
                                "lastLetter", "Character")
                )
                .containsStaticBuilderMethod()
                .containsStaticBuildMethod()
                .buildMethodUsingConstructorStrategy(
                        "net.jfaker.dto.BookDTO",
                        List.of("id", "name", "year", "pages", "price", "discount")
                );
    }

    @Test
    void whenSetFieldInitNullThenBotPropertyHasNull() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public record %s(String stub){}
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
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                constructorStrategy = @ConstructorStrategy(
                                                                    fieldsInitNull = {"stub"}
                                                                )
                                                            )
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass, AbstractBot.class.getSimpleName())
                .hasPrivatePropertiesWithNullInitValue(Map.of("stub", "String"));

    }

    @Test
    void whenClassHasUnregisterTypeThenInitItNull() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public record %s(Object stub){}
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
                                                            botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy)
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass, AbstractBot.class.getSimpleName())
                .hasPrivatePropertiesWithNullInitValue(Map.of("stub", "Object"));
    }

    @Test
    void whenSpecifyConstructorThenUseIt() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public class %s{
                          public %s(String stub){}
                          public %s(String stub, Long stub2, int stub3){}
                          public %s(String stub, boolean stub2){}
                      }
                      """.formatted(generatedClass, generatedClass, generatedClass, generatedClass));
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
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                constructorStrategy = @ConstructorStrategy(
                                                                    argsNames = {"java.lang.String", "boolean"}
                                                                )
                                                            )
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass, AbstractBot.class.getSimpleName())
                .containsStatement(List.of(
                        """
                            public %s build() {
                                return new %s(
                                        stub.get(),
                                        stub2.get()
                                        );
                            }
                        """.formatted(generatedClass, "net.jfaker.dto.SampleDTO")));

    }

    @Test
    void whenParameterSetCustomProviderThenUseIt() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public class %s{
                          public %s(String stub){}
                      }
                      """.formatted(generatedClass, generatedClass));
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
                        import net.jfaker.annotation.FieldConfiguration;
                        import net.jfaker.annotation.ConstructorStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                constructorStrategy = @ConstructorStrategy(
                                                                    fieldsConfiguration = {
                                                                        @FieldConfiguration(
                                                                            name = "stub",
                                                                            fakerProvider = "faker.animal().name()"
                                                                        )
                                                                    }
                                                                )
                                                            )
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass, AbstractBot.class.getSimpleName())
                .hasPrivatePropertiesWithInitValue(Map.of("stub", "faker.animal().name()"));
    }

    @Test
    void whenHasPropertyWithGeneratedBotMappedThenSetPropInit() throws IOException {
        final var utilBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.NestedDTO",
                """
                       package net.jfaker.dto;
                       public record NestedDTO(Long id){}
                       """
        );
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                  """
                  package net.jfaker.dto;
                  public record %s(NestedDTO nested){}
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
                        import net.jfaker.annotation.FieldConfiguration;
                        import net.jfaker.annotation.ConstructorStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            constructorStrategy = @ConstructorStrategy(
                                                fieldsConfiguration = {
                                                    @FieldConfiguration(
                                                        name = "nested",
                                                        botSource = "NestedDTOBot"
                                                    )
                                                }
                                            )
                                        )
                                    ),
                                    @AutoFakerBot(
                                            generatedInstance = "net.jfaker.dto.NestedDTO",
                                            packageToGenerate = "net.jfaker.bot",
                                            botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy)
                                        )
                                }
                        )
                        public class CustomFaker extends Faker {}
                       
                       """.formatted(botGeneratedPackage, generatedClass));
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(utilBot, toGenerateBot, botConfig);

        CompilationSubject.assertThat(compilation).succeeded();
        assertThat(compilation.generatedSourceFiles().size()).isEqualTo(2);
        CompilationSubject.assertThat(compilation).generatedSourceFile(botQualifiedName);
        var classCode = compilation.generatedSourceFile(botQualifiedName).orElseThrow()
                .getCharContent(true).toString();

        BotAssert.assertThat(classCode, botSimpleName, generatedClass, AbstractBot.class.getSimpleName())
                .hasPrivatePropertiesWithInitValue(Map.of("nested", "NestedDTOBot.builder().build()"));
    }

    @Test
    void whenHasPropertyListWithGeneratedBotMappedThenSetPropInit() throws IOException {
        final var utilBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.NestedDTO",
                """
                       package net.jfaker.dto;
                       public record NestedDTO(Long id){}
                       """
        );
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                package net.jfaker.dto;
                import java.util.List;
                public record %s(List<NestedDTO> nested){}
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
                        import net.jfaker.annotation.FieldConfiguration;
                        import net.jfaker.annotation.ConstructorStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            constructorStrategy = @ConstructorStrategy(
                                                fieldsConfiguration = {
                                                    @FieldConfiguration(
                                                        name = "nested",
                                                        botSource = "NestedDTOBot"
                                                    )
                                                }
                                            )
                                        )
                                    ),
                                    @AutoFakerBot(
                                            generatedInstance = "net.jfaker.dto.NestedDTO",
                                            packageToGenerate = "net.jfaker.bot",
                                            botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy)
                                        )
                                }
                        )
                        public class CustomFaker extends Faker {}
                       
                       """.formatted(botGeneratedPackage, generatedClass));
        final var compilation = Compiler.javac()
                .withProcessors(new BotProcessor())
                .compile(utilBot, toGenerateBot, botConfig);

        CompilationSubject.assertThat(compilation).succeeded();
        assertThat(compilation.generatedSourceFiles().size()).isEqualTo(2);
        CompilationSubject.assertThat(compilation).generatedSourceFile(botQualifiedName);
        var classCode = compilation.generatedSourceFile(botQualifiedName).orElseThrow()
                .getCharContent(true).toString();

        BotAssert.assertThat(classCode, botSimpleName, generatedClass, AbstractBot.class.getSimpleName())
                .hasPrivatePropertiesWithInitValue(Map.of("nested", "NestedDTOBot.builder().build(faker.number().randomDigitNotZero())"));
    }

}
