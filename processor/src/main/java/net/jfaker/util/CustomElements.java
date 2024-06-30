package net.jfaker.util;

import net.jfaker.exception.TypeElementNotFoundException;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class CustomElements implements Elements {

    private final Elements elements;

    public CustomElements(final Elements elements){
        this.elements = elements;
    }

    @Override
    public PackageElement getPackageElement(final CharSequence name) {
        return elements.getPackageElement(name);
    }

    @Override
    public TypeElement getTypeElement(final CharSequence name) {
        final var typeElement = elements.getTypeElement(name);
        if (isNull(typeElement)){
            throw new TypeElementNotFoundException("Class '%s' not found".formatted(name));
        }
        return typeElement;
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(final AnnotationMirror a) {
        return elements.getElementValuesWithDefaults(a);
    }

    @Override
    public String getDocComment(final Element e) {
        return elements.getDocComment(e);
    }

    @Override
    public boolean isDeprecated(final Element e) {
        return elements.isDeprecated(e);
    }

    @Override
    public Name getBinaryName(final TypeElement type) {
        return elements.getBinaryName(type);
    }

    @Override
    public PackageElement getPackageOf(final Element e) {
        return elements.getPackageOf(e);
    }

    @Override
    public List<? extends Element> getAllMembers(final TypeElement type) {
        return elements.getAllMembers(type);
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(final Element e) {
        return elements.getAllAnnotationMirrors(e);
    }

    @Override
    public boolean hides(final Element hider, final Element hidden) {
        return elements.hides(hider, hidden);
    }

    @Override
    public boolean overrides(final ExecutableElement overrider, final ExecutableElement overridden, final TypeElement type) {
        return elements.overrides(overrider, overridden, type);
    }

    @Override
    public String getConstantExpression(final Object value) {
        return elements.getConstantExpression(value);
    }

    @Override
    public void printElements(final Writer w, final Element... elements) {
        this.elements.printElements(w, elements);
    }

    @Override
    public Name getName(final CharSequence cs) {
        return elements.getName(cs);
    }

    @Override
    public boolean isFunctionalInterface(final TypeElement type) {
        return elements.isFunctionalInterface(type);
    }
}
