package net.jfaker.util;

public final class StringUtil {

    private StringUtil(){

    }

    public static String firstLetterToLowerCase(final String sentence){
        return sentence.substring(0, 1).toLowerCase() + sentence.substring(1);
    }

    public static String firstLetterToUpperCase(final String sentence){
        return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
    }

}
