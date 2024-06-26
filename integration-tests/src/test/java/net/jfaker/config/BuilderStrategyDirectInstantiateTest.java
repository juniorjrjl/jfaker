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

class BuilderStrategyDirectInstantiateTest {

    @Test
    void DefaultConfigTest() throws IOException {
        final var generatedClass = "FileDTO";
        final var generatedClassBuilder = "FileDTOBuilder";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.FileDTO",
                """
                       package net.jfaker.dto;
                       
                       import java.time.LocalDate;
                       import java.time.LocalDateTime;
                       
                        public class %s {
                       
                        private final byte id;
                        private final Byte address;
                        private final LocalDateTime createdAt;
                        private final LocalDate updatedAt;
                       
                        public %s(final byte id, final Byte address, final LocalDateTime createdAt, final LocalDate updatedAt) {
                             this.id = id;
                             this.address = address;
                             this.createdAt = createdAt;
                             this.updatedAt = updatedAt;
                        }
                       
                            public static final class %s {
                                 private byte id;
                                 private Byte address;
                                 private LocalDateTime createdAt;
                                 private LocalDate updatedAt;
                       
                                 public %s withId(byte id) {
                                     this.id = id;
                                     return this;
                                 }
                       
                                 public %s withAddress(Byte address) {
                                     this.address = address;
                                     return this;
                                 }
                       
                                 public %s withCreatedAt(LocalDateTime createdAt) {
                                     this.createdAt = createdAt;
                                     return this;
                                 }
                       
                                 public %s withUpdatedAt(LocalDate updatedAt) {
                                     this.updatedAt = updatedAt;
                                     return this;
                                 }
                       
                                 public %s build() {
                                     return new %s(id, address, createdAt, updatedAt);
                                 }
                             }
                       
                        }
                       """.formatted(generatedClass,
                        generatedClass,
                        generatedClassBuilder,
                        generatedClassBuilder,
                        generatedClassBuilder,
                        generatedClassBuilder,
                        generatedClassBuilder,
                        generatedClass,
                        generatedClass)
        );

        final var botSimpleName = "FileDTOBot";
        final var botGeneratedPackage = "net.jfaker.dto";
        final var botQualifiedName = "net.jfaker.bot.FileDTOBot";
        final var botConfig= JavaFileObjects.forSourceLines(
                "net.jfaker.config.CustomFaker",
                """
                        package net.jfaker.config;
                       
                        import net.datafaker.Faker;
                        import net.jfaker.annotation.AutoFakerBot;
                        import net.jfaker.annotation.BotBuildStrategy;
                        import net.jfaker.annotation.BuilderStrategy;
                        import net.jfaker.annotation.FakerInfo;
                       
                        import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                        @AutoFakerBot(
                                                generatedInstance = "%s.%s",
                                                packageToGenerate = "net.jfaker.bot",
                                                botBuildStrategy = @BotBuildStrategy(
                                                        builderStrategy = @BuilderStrategy(
                                                                builderQualifiedName = "net.jfaker.dto.FileDTO.%s",
                                                                instantiateMethod = DIRECT_INSTANTIATE
                                                        )
                                                )
                                        )
                                }
                        )
                        public class CustomFaker extends Faker {}
                       """.formatted(botGeneratedPackage, generatedClass, generatedClassBuilder));

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
                        Map.of(
                                "id", "Byte",
                                "address", "Byte",
                                "createdAt", "LocalDateTime",
                                "updatedAt", "LocalDate")
                )
                .hasPrivatePropertiesWithInitValue(
                        Map.of(
                                "id", "Byte.parseByte(faker.number().digit())",
                                "address", "Byte.parseByte(faker.number().digit())",
                                "createdAt",
                                """
                                        Instant.ofEpochMilli(faker.date().birthday().getTime())
                                                         .atZone(ZoneId.systemDefault())
                                                         .toLocalDateTime()
                                            ;
                                        """,
                                "updatedAt", "faker.date().birthdayLocalDate()"
                        )
                ).containsWithForProperties(
                        Map.of(
                                "id", "Byte",
                                "address", "Byte",
                                "createdAt", "LocalDateTime",
                                "updatedAt", "LocalDate")
                )
                .containsStaticBuilderMethod()
                .containsStaticBuildMethod()
                .buildMethodUsingBuilderStrategyWithDirectInstantiate(
                        generatedClassBuilder,
                        "with",
                        "build",
                        true,
                        List.of("id", "address", "createdAt", "updatedAt")
                );
    }

    @Test
    void whenSetFieldInitNullThenBotPropertyHasNull() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public record %s(String stub){
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
                        import net.jfaker.annotation.BuilderStrategy;
                        
                        import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            builderStrategy = @BuilderStrategy(
                                                instantiateMethod = DIRECT_INSTANTIATE,
                                                builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
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
    void whenSetIgnoreWithThenNotCreateProperty() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                      package net.jfaker.dto;
                      public record %s(String stub){
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
                        import net.jfaker.annotation.BuilderStrategy;
                        
                        import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            builderStrategy = @BuilderStrategy(
                                            instantiateMethod = DIRECT_INSTANTIATE,
                                            builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
                                                methodsToIgnore = {"withStub"}
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
                 public record %s(Object stub){
                 
                     public static class SampleDTOBuilder{
                 
                         private Object stub;
                 
                         public SampleDTOBuilder withStub(final Object stub){
                             this.stub = stub;
                             return this;
                         }
                 
                         public SampleDTO build(){
                             return new SampleDTO(stub);
                         }
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
                        import net.jfaker.annotation.BuilderStrategy;
                        
                        import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                builderStrategy = @BuilderStrategy(
                                                                    builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
                                                                    instantiateMethod = DIRECT_INSTANTIATE
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
                .hasPrivatePropertiesWithNullInitValue(Map.of("stub", "Object"));
    }

    @Test
    void whenParameterSetCustomProviderThenUseIt() throws IOException {
        final var generatedClass = "SampleDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.SampleDTO",
                """
                 package net.jfaker.dto;
                 public record %s(String stub){
                 
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
                        import net.jfaker.annotation.BuilderStrategy;
                        
                        import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                builderStrategy = @BuilderStrategy(
                                                                    builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
                                                                    instantiateMethod = DIRECT_INSTANTIATE,
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
                 public record %s(NestedDTO nested){
                     public static class SampleDTOBuilder{
                         private NestedDTO nested;
                         public SampleDTOBuilder withNested(final NestedDTO nested){
                             this.nested = nested;
                             return this;
                         }
                         public SampleDTO build(){
                             return new SampleDTO(nested);
                         }
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
                        import net.jfaker.annotation.BuilderStrategy;
                       
                       import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            builderStrategy = @BuilderStrategy(
                                                builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
                                                instantiateMethod = DIRECT_INSTANTIATE,
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
                 public record %s(List<NestedDTO> nested){
                     public static class SampleDTOBuilder{
                         private List<NestedDTO> nested;
                         public SampleDTOBuilder withNested(final List<NestedDTO> nested){
                             this.nested = nested;
                             return this;
                         }
                         public SampleDTO build(){
                             return new SampleDTO(nested);
                         }
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
                        import net.jfaker.annotation.BuilderStrategy;
                       
                       import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            builderStrategy = @BuilderStrategy(
                                                builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
                                                instantiateMethod = DIRECT_INSTANTIATE,
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
