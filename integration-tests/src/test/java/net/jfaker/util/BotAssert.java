package net.jfaker.util;

import org.assertj.core.api.AbstractAssert;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BotAssert extends AbstractAssert<BotAssert, String> {

    private final String className;
    private final String generatedClass;
    private final String abstractClass;

    protected BotAssert(final String classCode,
                        final String className,
                        final String generatedClass,
                        final String abstractClass) {
        super(classCode, BotAssert.class);
        this.className = className;
        this.generatedClass = generatedClass;
        this.abstractClass = abstractClass;
    }

    public static BotAssert assertThat(final String classCode,
                                       final String className,
                                       final String generatedClass,
                                       final String abstractClass){
        return new BotAssert(classCode, className, generatedClass, abstractClass);
    }

    public BotAssert hasGeneratedInPackage(final String packageName){
        isNotNull();
        final var expectedPackage = "package " + packageName + ";";

        if (!actual.startsWith(expectedPackage)){
            failWithMessage("Expected class is in package <%s> but was in <%s>", expectedPackage, actual.substring(0, actual.indexOf(';')));
        }

        return this;
    }

    public BotAssert inheritsFrom(){
        isNotNull();

        if (!actual.contains("extends " + abstractClass)){
            failWithMessage("Expected class inherits from <%s> ", abstractClass);
        }

        return this;
    }

    public BotAssert boGenerateSpecifiedType(){
        isNotNull();

        containsStatement(
                List.of(
                        String.format("public class %s extends %s<%s> {", className, abstractClass, generatedClass),
                        String.format("public %s build() {", generatedClass)
                )
        );

        return this;
    }

    public BotAssert hasName(){
        isNotNull();
        final var classDefinition = "public class ";

        if (!actual.contains(String.format("public class %s extends %s<%s> {", className, abstractClass, generatedClass))){
            final var firstIndex = actual.indexOf(classDefinition) + classDefinition.length();
            final var lastIndex = actual.indexOf(String.format(" extends %s<%s> {", abstractClass, generatedClass));
            final var actualClassName = actual.substring(firstIndex, lastIndex);
            failWithMessage("Expected class name is <%s> but is <%s>", className, actualClassName);
        }

        return this;
    }

    public BotAssert hasPrivateNonArgsConstructor(){
        isNotNull();

        final var expectedConstructor = "private %s()".formatted(className);

        if (!actual.contains(expectedConstructor)){
            failWithMessage("Expected class <%s> has any private constructor without args", className);
        }

        return this;
    }

    public BotAssert hasPrivatePropertiesWithTypes(final Map<String, String> types){
        isNotNull();

        final var propsTemplate = "private Supplier<%s> %s = ";

        types.forEach((n, t) -> {
            final var property = propsTemplate.formatted(t, n);
            if (!actual.contains(property)){
                failWithMessage("Expected bot has property <%s> but is absent", property);
            }
        });
        return this;
    }

    public BotAssert hasPrivatePropertiesWithNullInitValue(final Map<String, String> types){
        isNotNull();

        final var propsTemplate = "private Supplier<%s> %s = () -> null";

        types.forEach((p, t) -> {
            if (!actual.contains(propsTemplate.formatted(t, p))){
                failWithMessage("Expected bot property <%s> init with null value", p);
            }
        });
        return this;
    }

    public BotAssert hasPrivatePropertiesWithInitValue(final Map<String, String> types){
        isNotNull();

        final var propsTemplate = "> %s = () -> %s";

        types.forEach((p, v) -> {
            if (!actual.contains(propsTemplate.formatted(p, v))){
                failWithMessage("Expected bot property <%s> has default value () -> <%s>", p, v);
            }
        });
        return this;
    }

    public BotAssert containsWithForProperties(final Map<String, String> props){
        isNotNull();

        final var withTemplate = """
                    public %s with%s(final Supplier<%s> %s) {
                        this.%s = %s;
                        return this;
                    }
                """;
        props.forEach((p, t) -> {
            final var methodSuffix = p.substring(0, 1).toUpperCase() + p.substring(1);
            final var withToFind = withTemplate.formatted(className, methodSuffix, t, p, p, p);
            if (!actual.contains(withToFind)){
                failWithMessage("Expected bot has with method in bot patter <%s> but is absent", "with" + methodSuffix);
            }
        });
        return this;
    }

    public BotAssert containsStaticBuilderMethod(){
        isNotNull();
        final var builderMethod = """
                    public static %s builder() {
                        return new %s();
                    }
                """.formatted(className, className);
        if (!actual.contains(builderMethod)){
            failWithMessage("Expected bot has builder method <%s> but is absent", builderMethod);
        }
        return this;
    }

    public BotAssert notContainsStatement(final List<String> statements){
        verifyStatement(statements, s ->{
            if (actual.contains(s)){
                failWithMessage("expected bot haven't a statement <%s> but have", s);
            }
        });
        return this;
    }

    public BotAssert containsStatement(final List<String> statements){
        verifyStatement(statements, s ->{
            if (!actual.contains(s)){
                failWithMessage("expected bot has a statement <%s> but haven't", s);
            }
        });
        return this;
    }

    public BotAssert containsStaticBuildMethod(){
        isNotNull();
        final var builderMethod = "public %s build() {".formatted(generatedClass);
        if (!actual.contains(builderMethod)){
            failWithMessage("Expected bot has builder method <%s> but is absent", builderMethod);
        }
        return this;
    }

    public BotAssert buildMethodUsingSetterStrategy(final String varName, final String generatedClassPackage, final List<String> properties){
        isNotNull();
        final var indexStartBuildStatement = getIndexOfStatement(
                "        var %s = new %s();".formatted(varName, generatedClassPackage + '.' + generatedClass),
                        "Expected bot using setter strategy"
        );

        final var indexEndBuildStatement = getIndexOfStatement(
                "        return %s;".formatted(varName),
                "Expected bot using setter strategy");

        properties.forEach(p ->
            verifyStatementBetweenCharIndex(
                    indexStartBuildStatement,
                    indexEndBuildStatement,
                    "        %s.set%s(%s.get());".formatted(
                            varName,
                            p.substring(0, 1).toUpperCase() + p.substring(1),
                            p
                    ),
                    "Expected bot using setter strategy"
            )
        );

        return this;
    }

    public BotAssert buildMethodUsingConstructorStrategy(final String generatedQualifiedName,
                                                         final List<String> properties){
        isNotNull();
        final var indexStartBuildStatement = getIndexOfStatement(
                "        return new %s(".formatted(generatedQualifiedName),
                "Expected bot using constructor strategy"
        );


        final var indexEndBuildStatement = getIndexOfStatement(
                "                );",
                "Expected bot using constructor strategy"
        );

        properties.forEach(p ->
            verifyStatementBetweenCharIndex(
                    indexStartBuildStatement,
                    indexEndBuildStatement,
                    "        %s.get()".formatted(p),
                    "Expected bot using constructor strategy")
        );

        return this;
    }

    public BotAssert buildMethodUsingBuilderStrategyWithStaticMethodClass(final String builderMethod,
                                                                          final String buildMethod,
                                                                          final String builderMethodsPrefix,
                                                                          final List<String> properties){
        isNotNull();
        final var indexStartBuildStatement = getIndexOfStatement(
                "        return %s.%s(".formatted(generatedClass, builderMethod),
                "Expected bot using builder strategy with static method"
        );
        final var indexEndBuildStatement = getIndexOfStatement(
                "                    .%s();".formatted(buildMethod),
                "Expected bot using builder strategy with static method"
        );

        properties.forEach(p ->
            verifyStatementBetweenCharIndex(
                    indexStartBuildStatement,
                    indexEndBuildStatement,
                    "        .%s%s(%s.get())".formatted(
                            builderMethodsPrefix,
                            p.substring(0, 1).toUpperCase() + p.substring(1),
                            p
                    ),
                    "Expected bot using builder strategy with static method")
        );

        return this;
    }

    public BotAssert buildMethodUsingBuilderStrategyWithDirectInstantiate(final String builderClass,
                                                                          final String builderMethodsPrefix,
                                                                          final String buildMethod,
                                                                          final List<String> properties){
        isNotNull();
        final var indexStartBuildStatement = getIndexOfStatement(
                """
                        var builder = new %s();
                        return builder
                """.formatted(builderClass),
                "Expected bot using builder strategy with direct Instantiate"
        );
        final var indexEndBuildStatement = getIndexOfStatement(
                "                    .%s();".formatted(buildMethod),
                "Expected bot using builder strategy with direct Instantiate"
        );

        properties.forEach(p ->
                verifyStatementBetweenCharIndex(
                        indexStartBuildStatement,
                        indexEndBuildStatement,
                        ".%s%s(%s.get())".formatted(
                                builderMethodsPrefix,
                                p.substring(0, 1).toUpperCase() + p.substring(1),
                                p
                        ),
                        "Expected bot using builder strategy with direct Instantiate")
        );

        return this;
    }

    private void verifyStatement(final List<String> statements, final Consumer<String> statementConstraint){
        isNotNull();
        statements.forEach(statementConstraint);
    }

    private int getIndexOfStatement(final String statement, final String messageIfNotFound){
        final var statementIndex = actual.indexOf(statement);
        if (statementIndex == -1){
            failWithMessage(messageIfNotFound);
        }
        return statementIndex;
    }

    private void verifyStatementBetweenCharIndex(final int firstIndex,
                                                 final int lastIndex,
                                                 final String statement,
                                                 final String messageIfFalse){
        final var statementIndex = actual.indexOf(statement);
        if (statementIndex == -1 || statementIndex < firstIndex || statementIndex > lastIndex){
            failWithMessage(messageIfFalse);
        }
    }

}
