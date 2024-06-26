package net.jfaker.factory;

import net.jfaker.annotation.BotBuildStrategy;
import net.jfaker.model.ClassInfo;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static javax.lang.model.element.ElementKind.CONSTRUCTOR;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static net.jfaker.util.StringUtil.firstLetterToLowerCase;
import static net.jfaker.util.ValidationUtil.validateBuilderStrategy;

/**
 * class for extract properties from class generated by bot
 */
public final class PropertyFactory {

    private PropertyFactory(){

    }

    /**
     * method to receive information about class generated by bot and configuration about build method
     * @param generatedClass class generated by bot
     * @param elementUtils class used to create type element from qualified name
     * @param botBuildStrategy configuration about bot
     * @return elements used by bot
     */
    public static Map<String, TypeMirror> generateProps(final ClassInfo generatedClass,
                                                        final Elements elementUtils,
                                                        final BotBuildStrategy botBuildStrategy){
        final var setterStrategy = botBuildStrategy.setterStrategy();
        final var constructorStrategy = botBuildStrategy.constructorStrategy();
        final var builderStrategy = botBuildStrategy.builderStrategy();
        if (!setterStrategy.ignoreConfig()){
            final List<String> settersToIgnore = Arrays.stream(setterStrategy.settersToIgnore()).toList();
            final var elements = getEnclosedElementsFromClasses(
                    generatedClass.getQualifiedName(),
                    elementUtils,
                    e -> e.getModifiers().contains(PUBLIC) &&
                            e.getKind() == METHOD &&
                            e.getSimpleName().toString().startsWith("set") &&
                            !settersToIgnore.contains(e.getSimpleName().toString())
            );
            return extractPropsFromSetters(elements);
        }else if (!constructorStrategy.ignoreConfig()) {
            final var argsNames = List.of(constructorStrategy.argsNames());
            final var elements = getEnclosedElementsFromClasses(
                    generatedClass.getQualifiedName(),
                    elementUtils,
                    e -> e.getModifiers().contains(PUBLIC) &&
                            e.getKind() == CONSTRUCTOR
            );
            return extractPropsFromConstructor(elements, argsNames);

        } else {
            validateBuilderStrategy(builderStrategy);
            final var prefix = builderStrategy.prefixMethods();
            final List<String> methodsToIgnore = Arrays.stream(builderStrategy.methodsToIgnore()).toList();
            final var elements = getEnclosedElementsFromClasses(
                    builderStrategy.builderQualifiedName(),
                    elementUtils,
                    e -> e.getModifiers().contains(PUBLIC) &&
                            e.getKind() == METHOD &&
                            e.getSimpleName().toString().startsWith(prefix) &&
                            !methodsToIgnore.contains(e.getSimpleName().toString())
            );
            return extractPropsFromBuilder(elements, prefix);
        }
    }

    /**
     * recursive method to extract elements from class generated by bot and all superclass
     * @param classQualifiedName qualified name of class used to extract elements
     * @param elementUtils class used to create type element from qualified name
     * @param strategyFilter filter used to extract only needed elements
     * @return elements used by bot
     */
    private static List<? extends Element> getEnclosedElementsFromClasses(final String classQualifiedName,
                                                                          final Elements elementUtils,
                                                                          final Predicate<? super Element> strategyFilter){
        final var clasTypeElement = elementUtils.getTypeElement(classQualifiedName);
        final var superClassQualifiedName = clasTypeElement.getSuperclass().toString();
        final List<? extends Element> elements = elementUtils.getTypeElement(classQualifiedName)
                .getEnclosedElements()
                .stream()
                .filter(strategyFilter)
                .collect(toCollection(ArrayList::new));
        if (superClassQualifiedName.equals("java.lang.Object")){
            return elements;
        } else {
            return Stream.concat(
                    elements.stream(),
                    getEnclosedElementsFromClasses(superClassQualifiedName, elementUtils, strategyFilter).stream()
            ).toList();
        }
    }

    /**
     * extract properties information from setter methods in class returned by bots
     * @param elements elements belong to class generated by bot
     * @return all properties belong to class generated by bot (ignored properties is included in result).
     */
    private static Map<String, TypeMirror> extractPropsFromSetters(final List<? extends Element> elements){
        return elements.stream()
                .map(m -> (ExecutableElement) m)
                .collect(Collectors.toMap(
                                m -> firstLetterToLowerCase(m.getSimpleName().toString().replace("set", "")),
                                m -> m.getParameters().get(0).asType(),
                                (typeMirror, typeMirror2) -> typeMirror,
                                LinkedHashMap::new
                        )
                );
    }

    /**
     * extract properties information from constructor. A constructor selected is a constructor have name args in same order
     * of argsName, if list is empty find by constructor have more arguments in class
     * @param elements elements belong to class generated by bot
     * @param  argsNames list with args names used to filter selected constructor
     * @return all properties belong to class generated by bot
     */
    private static Map<String, TypeMirror> extractPropsFromConstructor(final List<? extends Element> elements,
                                                                       final List<String> argsNames){
        final var constructorStream = elements.stream().map(c -> (ExecutableElement)c);
        if (argsNames.isEmpty()){
            return constructorStream
                    .max(Comparator.comparingInt(c -> c.getParameters().size()))
                    .orElseThrow()
                    .getParameters()
                    .stream()
                    .collect(Collectors.toMap(
                                    v -> v.getSimpleName().toString(),
                                    VariableElement::asType,
                                    (typeMirror, typeMirror2) -> typeMirror,
                                    LinkedHashMap::new
                            )
                    );
        } else {
            return constructorStream
                    .filter(c -> c.getParameters().stream().map(a -> a.asType().toString()).toList().equals(argsNames))
                    .findFirst()
                    .orElseThrow()
                    .getParameters()
                    .stream()
                    .collect(Collectors.toMap(
                                    v -> v.getSimpleName().toString(),
                                    VariableElement::asType,
                                    (typeMirror, typeMirror2) -> typeMirror,
                                    LinkedHashMap::new
                            )
                    );
        }
    }

    /**
     * extract properties information from builder class to obtain properties
     * @param elements elements belong to class generated by bot
     * @param prefix prefix used in setter builder methods ex.: withName
     * @return all properties belong to class generated by bot (ignored properties is included in result).
     */
    private static Map<String, TypeMirror> extractPropsFromBuilder(final List<? extends Element> elements,
                                                                   final String prefix){
        return elements.stream()
                .map(m -> (ExecutableElement) m)
                .collect(Collectors.toMap(
                                m -> firstLetterToLowerCase(m.getSimpleName().toString().replace(prefix, "")),
                                m -> m.getParameters().get(0).asType(),
                                (typeMirror, typeMirror2) -> typeMirror,
                                LinkedHashMap::new
                        )
                );
    }

}
