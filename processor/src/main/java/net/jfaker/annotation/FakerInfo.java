package net.jfaker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Used to indicate a custom implementation of net.datafaker.Faker who will be instantiated in generated bots
 * and for configure bots
 */

@Target(TYPE)
@Retention(SOURCE)
public @interface FakerInfo {

    /**
     * Configuration to generate bots
     */
    AutoFakerBot[] botsConfiguration();

}
