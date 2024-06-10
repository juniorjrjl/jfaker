package net.jfaker.processor;

import com.squareup.javapoet.JavaFile;
import net.jfaker.annotation.BotBuildStrategy;
import net.jfaker.annotation.FakerInfo;
import net.jfaker.annotation.FieldConfiguration;
import net.jfaker.factory.BuildFactory;
import net.jfaker.factory.PropertyFactory;
import net.jfaker.generatorbuilder.BotGenerator;
import net.jfaker.generatorbuilder.BotGeneratorBuilderStrategy;
import net.jfaker.generatorbuilder.BotGeneratorConstructorStrategy;
import net.jfaker.generatorbuilder.BotGeneratorSetterStrategy;
import net.jfaker.model.BotClassInfo;
import net.jfaker.model.BotSuperClassInfo;
import net.jfaker.model.BuildMethodInfoBuilderStrategy;
import net.jfaker.model.BuildMethodInfoConstructorStrategy;
import net.jfaker.model.BuildMethodInfoSetterStrategy;
import net.jfaker.model.ClassInfo;
import net.jfaker.model.FakerPropertyInfo;
import net.jfaker.model.PropertyInfo;
import net.jfaker.model.SetterBuilderMethodInfo;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.jfaker.generatorbuilder.property.PropertyUtil.PROPERTIES_INITIALIZERS;

/**
 * Class who will process annotation {@link net.jfaker.annotation.FakerInfo FakerInfo} and generate bots
 */
@SupportedAnnotationTypes("net.jfaker.annotation.FakerInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class BotProcessor extends AbstractProcessor {

    private BotGenerator<BuildMethodInfoConstructorStrategy> botGeneratorConstructorStrategy;
    private BotGenerator<BuildMethodInfoSetterStrategy> botGeneratorSetterStrategy;
    private BotGenerator<BuildMethodInfoBuilderStrategy> botGeneratorBuilderStrategy;
    
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        final var elementUtils = processingEnv.getElementUtils();
        botGeneratorConstructorStrategy = new BotGeneratorConstructorStrategy(processingEnv.getTypeUtils(), PROPERTIES_INITIALIZERS);
        botGeneratorSetterStrategy = new BotGeneratorSetterStrategy(processingEnv.getTypeUtils(), PROPERTIES_INITIALIZERS);
        botGeneratorBuilderStrategy = new BotGeneratorBuilderStrategy(processingEnv.getTypeUtils(), PROPERTIES_INITIALIZERS);

        var optionalElement = roundEnv.getElementsAnnotatedWith(FakerInfo.class)
                .stream()
                .findFirst();
        if (optionalElement.isEmpty()) return false;

        final var botClassesInfo = buildBotClass(optionalElement.get(), elementUtils);
        final var javaFile = botClassesInfo.stream().map(b -> {
            if (b.getBuildMethodInfo() instanceof BuildMethodInfoSetterStrategy buildMethodInfo){
                return botGeneratorSetterStrategy.create(b, buildMethodInfo).indent("    ").build();
            } else if (b.getBuildMethodInfo() instanceof BuildMethodInfoConstructorStrategy buildMethodInfo) {
                return botGeneratorConstructorStrategy.create(b, buildMethodInfo).indent("    ").build();
            } else {
                return botGeneratorBuilderStrategy.create(b, (BuildMethodInfoBuilderStrategy) b.getBuildMethodInfo()).indent("    ").build();
            }
        });
        javaFile.forEach(f -> writeClass(f, processingEnv.getFiler()));

        return true;

    }

    /**
     * Generate classes with information to generate bots
     * @param fakerElement instance of class inherits from Faker annoted with {@link net.jfaker.annotation.FakerInfo FakerInfo}
     * @param elementUtils used to generate TypeElement
     * @return a list with bots information
     */
    private List<BotClassInfo> buildBotClass(final Element fakerElement, final Elements elementUtils){
        final var fakerInfo = new ClassInfo(fakerElement.toString());
        final var fakerInfoAnnotation = fakerElement.getAnnotation(FakerInfo.class);
        return Stream.of(fakerInfoAnnotation.botsConfiguration())
                .map(b -> {
                    final var generatedClass = new ClassInfo(
                            b.generatedInstance(),
                            elementUtils.getTypeElement(b.generatedInstance())
                    );
                    final var generatedSuperclass = b.generatedInstanceSuperClass().isBlank() ?
                            generatedClass :
                            new ClassInfo(
                                    b.generatedInstanceSuperClass(),
                                    elementUtils.getTypeElement(b.generatedInstanceSuperClass())
                            );
                    final var botSuperClass = new BotSuperClassInfo(
                            b.abstractBotQualifiedName(),
                            generatedSuperclass
                    );
                    final var botSimpleName = b.botClassName().isBlank() ?
                            generatedClass.getSimpleName() + "Bot" :
                            b.botClassName();
                    final var fakerProperty = new FakerPropertyInfo(
                            elementUtils.getTypeElement(fakerInfo.getQualifiedName()).asType(),
                            fakerInfo.getSimpleName()
                    );
                    final var botBuildStrategy = b.botBuildStrategy();
                    final var propsMap = PropertyFactory.generateProps(
                            generatedClass,
                            elementUtils,
                            botBuildStrategy
                    );
                    return BotClassInfo.builder()
                            .withPackageName(b.packageToGenerate())
                            .withSimpleName(botSimpleName)
                            .withBotSuperClassInfo(botSuperClass)
                            .withFaker(fakerProperty)
                            .withProperties(initializeProperties(propsMap, botBuildStrategy))
                            .withSetterBuilder(initializeSetters(propsMap))
                            .withGeneratedClass(generatedSuperclass)
                            .withBuildMethodInfo(BuildFactory.initializeBuildMethod(
                                    generatedClass,
                                    generatedSuperclass,
                                    propsMap.keySet().stream().toList(),
                                    elementUtils,
                                    botBuildStrategy
                                    ))
                            .build();
                }).toList();
    }

    /**
     * extract properties from superclass and subclass
     * @param superclass superclass info
     * @param subclass subclass info
     * @return a methods, constructors, variables, etc. belongs to classes
     */
    private List<? extends Element> getEnclosedElementsFromClasses(final ClassInfo superclass, final ClassInfo subclass){
        return superclass.equals(subclass) ? subclass.getTypeElement().getEnclosedElements() :
         Stream.concat(
                superclass.getTypeElement().getEnclosedElements().stream(),
                subclass.getTypeElement().getEnclosedElements().stream()
        ).toList();
    }

    /**
     * get a list of fields defined to init null in bots
     * @param botBuildStrategy bot configuration
     * @return a list of fields who will init with null value
     */
    private List<String> getFieldsInitNull(final BotBuildStrategy botBuildStrategy){
        final var setterStrategy = botBuildStrategy.setterStrategy();
        final var constructorStrategy = botBuildStrategy.constructorStrategy();
        final var builderStrategy = botBuildStrategy.builderStrategy();
        if (!setterStrategy.ignoreConfig()){
            return List.of(setterStrategy.fieldsInitNull());
        } else if (!constructorStrategy.ignoreConfig()) {
            return List.of(constructorStrategy.fieldsInitNull());
        } else {
            return List.of(builderStrategy.fieldsInitNull());
        }
    }

    /**
     * get fields customization from configurations
     * @param botBuildStrategy bot configuration
     * @return a map where key is field and value is a custom configuration
     */
    private Map<String, String> getFieldCustomization(final BotBuildStrategy botBuildStrategy){
        final var setterStrategy = botBuildStrategy.setterStrategy();
        final var constructorStrategy = botBuildStrategy.constructorStrategy();
        final var builderStrategy = botBuildStrategy.builderStrategy();
        Stream<FieldConfiguration> fieldsStream;
        if (!setterStrategy.ignoreConfig()){
            fieldsStream = Stream.of(setterStrategy.fieldsConfiguration());
        } else if (!constructorStrategy.ignoreConfig()) {
            fieldsStream = Stream.of(constructorStrategy.fieldsConfiguration());
        } else {
            fieldsStream = Stream.of(builderStrategy.fieldsConfiguration());
        }
        return fieldsStream.collect(Collectors.toMap(
                        FieldConfiguration::name,
                        f -> {
                            if (!f.fakerProvider().isBlank()) return f.fakerProvider();
                            if (!f.BotSource().isBlank()) return f.BotSource();
                            return "";
                        }
                )
        );
    }

    /**
     * initialize properties and setterBuilder
     * @param propsMap map with property name and property type
     * @param botBuildStrategy configurations about bot build
     * @return a list with properties belongs to builder
     */
    private List<PropertyInfo> initializeProperties(final Map<String, TypeMirror> propsMap,
                                                    final BotBuildStrategy botBuildStrategy){
        final Map<String, String> fieldCustomization = getFieldCustomization(botBuildStrategy);
        final List<String> nullableFields = getFieldsInitNull(botBuildStrategy);
        return propsMap.entrySet().stream()
                .map(entry ->
                        PropertyInfo.builder()
                                .withName(entry.getKey())
                                .withOriginalType(entry.getValue())
                                .withCustomSource(fieldCustomization.getOrDefault(entry.getKey(), null))
                                .withNullable(nullableFields.contains(entry.getKey()))
                                .build())
                .toList();
    }

    /**
     * initialize properties and setterBuilder
     * @param propsMap map with property name and property type
     * @return a list with methods used to change default value properties belongs to builder
     */
    private List<SetterBuilderMethodInfo> initializeSetters(final Map<String, TypeMirror> propsMap){
        return propsMap.entrySet().stream()
                .map(entry ->
                        SetterBuilderMethodInfo.builder()
                        .withPropertyName(entry.getKey())
                        .withOriginalType(entry.getValue())
                        .build())
                .toList();
    }

    /**
     * Write bot class
     * @param javaFile bot code to write in class
     * @param filer used to create file
     */
    private void writeClass(final JavaFile javaFile, final Filer filer){
        try {
            javaFile.writeTo(filer);
        }catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }

}
