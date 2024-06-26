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


class SetterStrategyTest {


    @Test
    void DefaultConfigTest() throws IOException {
        final var generatedClass = "UserDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.UserDTO",
                """
                        package net.jfaker.dto;
                       
                        import java.math.BigDecimal;
                        import java.util.Date;
                       
                        public class %s {
                       
                            private long id;
                            private String name;
                            private Date birthdate;
                            private boolean foreigner;
                            private BigDecimal salary;
                       
                            public void setId(final long id) {
                                this.id = id;
                            }
                       
                            public void setName(final String name) {
                                this.name = name;
                            }
                       
                            public void setBirthdate(final Date birthdate) {
                                this.birthdate = birthdate;
                            }
                       
                            public void setForeigner(final boolean foreigner) {
                                this.foreigner = foreigner;
                            }
                       
                            public void setSalary(final BigDecimal salary) {
                                this.salary = salary;
                            }
                        }
                       """.formatted(generatedClass)
        );

        final var botSimpleName = "UserDTOBot";
        final var botGeneratedPackage = "net.jfaker.dto";
        final var botQualifiedName = "net.jfaker.bot.UserDTOBot";
        final var botConfig= JavaFileObjects.forSourceLines(
                "net.jfaker.config.CustomFaker",
                """
                        package net.jfaker.config;
                       
                        import net.datafaker.Faker;
                        import net.jfaker.annotation.AutoFakerBot;
                        import net.jfaker.annotation.BotBuildStrategy;
                        import net.jfaker.annotation.FakerInfo;
                        import net.jfaker.annotation.SetterStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                        @AutoFakerBot(
                                                generatedInstance = "%s.%s",
                                                packageToGenerate = "net.jfaker.bot",
                                                botBuildStrategy = @BotBuildStrategy(setterStrategy = @SetterStrategy)
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


        BotAssert.assertThat(classCode, botSimpleName, generatedClass)
                .hasGeneratedInPackage("net.jfaker.bot")
                .inheritsFrom(AbstractBot.class.getSimpleName())
                .hasPrivateNonArgsConstructor()
                .hasPrivatePropertiesWithTypes(
                        Map.of("id", "Long",
                                "name", "String",
                                "birthdate", "Date",
                                "foreigner", "Boolean",
                                "salary" ,"BigDecimal" )
                )
                .hasPrivatePropertiesWithInitValue(
                        Map.of("id", "(long) faker.number().positive()",
                                "name", "faker.lorem().word()",
                                "birthdate", "faker.date().birthday()",
                                "foreigner", "faker.bool().bool()",
                                "salary",
                           """
                               {
                                              final var integerPart = faker.number().digits(3);
                                              final var decimalPart = faker.number().digits(2);
                                              return new BigDecimal(integerPart + "." + decimalPart);
                                   }
                                   ;
                               """)
                ).containsWithForProperties(
                        Map.of("id", "Long",
                                "name", "String",
                                "birthdate", "Date",
                                "foreigner", "Boolean",
                                "salary" ,"BigDecimal" ))
                .containsStaticBuilderMethod()
                .containsStaticBuildMethod()
                .buildMethodUsingSetterStrategy(
                        "userDTO",
                        "net.jfaker.dto",
                        List.of("id", "name", "birthdate", "foreigner", "salary")
                );
    }

    @Test
    void whenSetFieldInitNullThenBotPropertyHasNull() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public class %s{
                      
                      private String stub;
                      
                      public void setStub(final String stub){
                          this.stub = stub;
                      }
                      
                      }
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
                        import net.jfaker.annotation.SetterStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                setterStrategy = @SetterStrategy(
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass)
                .hasPrivatePropertiesWithNullInitValue(Map.of("stub", "String"));

    }

    @Test
    void whenSetIgnoreSetterThenNotCreateProperty() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public class %s{
                      
                      private String stub;
                      
                      public void setStub(final String stub){
                          this.stub = stub;
                      }
                      
                      }
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
                        import net.jfaker.annotation.SetterStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                setterStrategy = @SetterStrategy(
                                                                    settersToIgnore = {"setStub"}
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass)
                .notContainsStatement(List.of(
                        "private Supplier<String> stub = () -> faker.",
                        ".setStub(stub.get())"
                ));

    }

    @Test
    void whenClassHasUnregisterTypeThenInitItNull() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                  package net.jfaker.dto;
                  public class %s{
                 
                  private Object stub;
                 
                  public void setStub(final Object stub){
                      this.stub = stub;
                  }
                 
                  }
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
                        import net.jfaker.annotation.SetterStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(setterStrategy = @SetterStrategy)
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass)
                .hasPrivatePropertiesWithNullInitValue(Map.of("stub", "Object"));
    }

    @Test
    void whenParameterSetCustomProviderThenUseIt() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                  package net.jfaker.dto;
                  public class %s{
                 
                      private String stub;
                 
                      public void setStub(final String stub){
                          this.stub = stub;
                      }
                 
                  }
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
                        import net.jfaker.annotation.SetterStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                setterStrategy = @SetterStrategy(
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass)
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
                public class %s{
                
                    private NestedDTO nested;
                
                    public void setNested(final NestedDTO nested){
                        this.nested = nested;
                    }
                
                }
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
                        import net.jfaker.annotation.SetterStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            setterStrategy = @SetterStrategy(
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass)
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

                public class %s{
                
                    private List<NestedDTO> nested;
                
                    public void setNested(final List<NestedDTO> nested){
                       this.nested = nested;
                    }
                
                }
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
                        import net.jfaker.annotation.SetterStrategy;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            setterStrategy = @SetterStrategy(
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

        BotAssert.assertThat(classCode, botSimpleName, generatedClass)
                .hasPrivatePropertiesWithInitValue(Map.of("nested", "NestedDTOBot.builder().build(faker.number().randomDigitNotZero())"));
    }

}
