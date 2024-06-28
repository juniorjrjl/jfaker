package net.jfaker.model;

import javax.lang.model.element.TypeElement;
import java.util.Objects;
import java.util.stream.Stream;

import static javax.lang.model.element.ElementKind.RECORD;

/**
 * Class to represent Class read by processor
 */
public class ClassInfo {

    private final String packageName;
    private final String simpleName;
    private final String qualifiedName;
    private final TypeElement typeElement;
    private final boolean isInnerClass;

    /**
     * Instantiate class using full package name and className
     * @param qualifiedName className with package
     */
    public ClassInfo(final String qualifiedName) {
        this.qualifiedName = qualifiedName;
        this.packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
        this.simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
        this.isInnerClass = false;
        this.typeElement = null;
    }

    /**
     * Instantiate class using full package name and className and TypeElement
     * @param qualifiedName className with package
     * @param typeElement class TypeElement
     */
    public ClassInfo(final String qualifiedName, final TypeElement typeElement) {
        this.qualifiedName = qualifiedName;
        this.packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
        this.simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
        this.isInnerClass = false;
        this.typeElement = typeElement;
    }

    /**
     * Instantiate class using full package name and className and TypeElement
     * @param qualifiedName className with package
     * @param typeElement class TypeElement
     * @param isInnerClass is class is in same file another one and he is inside another
     */
    public ClassInfo(final String qualifiedName, final TypeElement typeElement, final boolean isInnerClass) {
        this.qualifiedName = qualifiedName;
        final var splitQualifiedName = qualifiedName.split("\\.");
        if (isInnerClass) {
            this.simpleName = String.join(".", Stream.of(splitQualifiedName).skip(splitQualifiedName.length - 2L).toList());
            this.packageName = String.join(".", Stream.of(splitQualifiedName).skip(2).toList());
        } else {
            this.packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
            this.simpleName = qualifiedName.substring(qualifiedName.lastIndexOf(".") + 1);
        }
        this.isInnerClass = isInnerClass;
        this.typeElement = typeElement;
    }

    /**
     * Instantiate class using className and package name separated
     * @param packageName package where class is located
     * @param simpleName className
     */
    public ClassInfo(final String packageName, final String simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.qualifiedName = packageName + "." + simpleName;
        this.isInnerClass = false;
        this.typeElement = null;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public boolean isInnerClass() {
        return isInnerClass;
    }

    public boolean isARecord(){
        return typeElement.getKind() == RECORD;
    }

    public boolean isNotARecord(){
        return !isARecord();
    }

}
