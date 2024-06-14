package net.jfaker.util;

import javax.lang.model.element.TypeElement;

public class ClassInfoUtil {

    private ClassInfoUtil(){

    }

    public static boolean extendsClass(final TypeElement element, final String superclassQualifiedName){
        return element.getSuperclass().toString().equals(superclassQualifiedName);
    }

    public static boolean noExtendsClass(final TypeElement element, final String superclassQualifiedName){
        return ! extendsClass(element, superclassQualifiedName);
    }

}
