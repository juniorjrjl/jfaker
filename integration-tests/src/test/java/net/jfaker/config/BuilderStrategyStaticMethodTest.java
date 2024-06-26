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


class BuilderStrategyStaticMethodTest {

    @Test
    void DefaultConfigTest() throws IOException {
        final var generatedClass = "ProductDTO";
        final var toGenerateBot = JavaFileObjects.forSourceLines(
                "net.jfaker.dto.ProductDTO",
                """
                        package net.jfaker.dto;
                       
                        import java.time.OffsetDateTime;
                        import java.util.Calendar;
                       
                        public class %s {
                       
                            private final Long id;
                            private final String name;
                            private final Double price;
                            private final double discount;
                            private final Calendar createdAt;
                            private final OffsetDateTime updatedAt;
                            private final Boolean old;
                            private final short brandCode;
                            private final Short modelCode;
                       
                            public %s(final Long id,
                                              final String name,
                                              final Double price,
                                              final double discount,
                                              final Calendar createdAt,
                                              final OffsetDateTime updatedAt,
                                              final Boolean old,
                                              final short brandCode,
                                              final Short modelCode) {
                                this.id = id;
                                this.name = name;
                                this.price = price;
                                this.discount = discount;
                                this.createdAt = createdAt;
                                this.updatedAt = updatedAt;
                                this.old = old;
                                this.brandCode = brandCode;
                                this.modelCode = modelCode;
                            }
                           public static ProductDTOBuilder builder(){
                               return new ProductDTOBuilder();
                           }
                            public static final class ProductDTOBuilder {
                                private Long id;
                                private String name;
                                private Double price;
                                private double discount;
                                private Calendar createdAt;
                                private OffsetDateTime updatedAt;
                                private Boolean old;
                                private short brandCode;
                                private Short modelCode;
                       
                                public ProductDTOBuilder withId(Long id) {
                                    this.id = id;
                                    return this;
                                }
                       
                                public ProductDTOBuilder withName(String name) {
                                    this.name = name;
                                    return this;
                                }
                       
                                public ProductDTOBuilder withPrice(Double price) {
                                    this.price = price;
                                    return this;
                                }
                       
                                public ProductDTOBuilder withDiscount(double discount) {
                                    this.discount = discount;
                                    return this;
                                }
                       
                                public ProductDTOBuilder withCreatedAt(Calendar createdAt) {
                                    this.createdAt = createdAt;
                                    return this;
                                }
                       
                                public ProductDTOBuilder withUpdatedAt(OffsetDateTime updatedAt) {
                                    this.updatedAt = updatedAt;
                                    return this;
                                }
                       
                                public ProductDTOBuilder withOld(Boolean old) {
                                    this.old = old;
                                    return this;
                                }
                       
                                public ProductDTOBuilder withBrandCode(short brandCode) {
                                    this.brandCode = brandCode;
                                    return this;
                                }
                       
                                public ProductDTOBuilder withModelCode(Short modelCode) {
                                    this.modelCode = modelCode;
                                    return this;
                                }
                       
                                public %s build() {
                                    return new %s(id, name, price, discount, createdAt, updatedAt, old, brandCode, modelCode);
                                }
                            }
                        }
                       """.formatted(generatedClass, generatedClass, generatedClass, generatedClass)
        );

        final var botSimpleName = "ProductDTOBot";
        final var botGeneratedPackage = "net.jfaker.dto";
        final var botQualifiedName = "net.jfaker.bot.ProductDTOBot";
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
                                botsConfiguration = {
                                        @AutoFakerBot(
                                                generatedInstance = "%s.%s",
                                                packageToGenerate = "net.jfaker.bot",
                                                botBuildStrategy = @BotBuildStrategy(
                                                        builderStrategy = @BuilderStrategy(
                                                                builderQualifiedName = "net.jfaker.dto.ProductDTO.ProductDTOBuilder"
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
                .hasGeneratedInPackage("net.jfaker.bot")
                .inheritsFrom(AbstractBot.class.getSimpleName())
                .hasPrivateNonArgsConstructor()
                .hasPrivatePropertiesWithTypes(
                        Map.of("id", "Long",
                                "name", "String",
                                "price", "Double",
                                "discount", "Double",
                                "createdAt", "Calendar",
                                "updatedAt", "OffsetDateTime",
                                "old", "Boolean",
                                "brandCode", "Short",
                                "modelCode", "Short")
                )
                .hasPrivatePropertiesWithInitValue(
                        Map.of("id", "(long) faker.number().positive()",
                                "name", "faker.lorem().word()",
                                "price", "faker.number().randomDouble(2, 1, 999)",
                                "discount", "faker.number().randomDouble(2, 1, 999)",
                                "createdAt",
                                        """
                {
                        var calendar = Calendar.getInstance();
                        calendar.setTime(faker.date().birthday());
                        return calendar;
                    }
                """,
                                "updatedAt", "faker.date().birthday().toInstant().atOffset(ZoneOffset.UTC)",
                                "old", "faker.bool().bool()",
                                "brandCode", "Short.parseShort(faker.number().digit()",
                                "modelCode", "Short.parseShort(faker.number().digit()")
                ).containsWithForProperties(
                        Map.of("id", "Long",
                                "name", "String",
                                "price", "Double",
                                "discount", "Double",
                                "createdAt", "Calendar",
                                "updatedAt", "OffsetDateTime",
                                "old", "Boolean",
                                "brandCode", "Short",
                                "modelCode", "Short")
                )
                .containsStaticBuilderMethod()
                .containsStaticBuildMethod()
                .buildMethodUsingBuilderStrategyWithStaticMethodClass(
                        "builder",
                        "build",
                        "with",
                        List.of("id", "name", "price", "discount", "createdAt", "updatedAt", "old", "brandCode", "modelCode")
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
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            builderStrategy = @BuilderStrategy(
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
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            builderStrategy = @BuilderStrategy(
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
                 
                     public static SampleDTOBuilder builder(){
                         return new SampleDTOBuilder();
                     }
                 
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
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                builderStrategy = @BuilderStrategy(
                                                                    builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder"
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
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                                            generatedInstance = "%s.%s",
                                                            packageToGenerate = "net.jfaker.bot",
                                                            botBuildStrategy = @BotBuildStrategy(
                                                                builderStrategy = @BuilderStrategy(
                                                                    builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
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
                     public static SampleDTOBuilder builder(){
                         return new SampleDTOBuilder();
                     }
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
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            builderStrategy = @BuilderStrategy(
                                                builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
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
                     public static SampleDTOBuilder builder(){
                         return new SampleDTOBuilder();
                     }
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
                       
                        @FakerInfo(
                                botsConfiguration = {
                                    @AutoFakerBot(
                                        generatedInstance = "%s.%s",
                                        packageToGenerate = "net.jfaker.bot",
                                        botBuildStrategy = @BotBuildStrategy(
                                            builderStrategy = @BuilderStrategy(
                                                builderQualifiedName = "net.jfaker.dto.SampleDTO.SampleDTOBuilder",
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
