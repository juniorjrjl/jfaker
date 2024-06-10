package net.jfaker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Annotation to define bot property configuration
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface FieldConfiguration {

    /**
     * a property name you will define a custom configuration ex.: name
     */
    String name();

    /**
     * String with code to access some provider in your custom Faker
     * ex.: faker.name().fullName()
     */
    String fakerProvider() default "";

    /**
     * A String with a bot ClassName will be used to generate an instance to set in property
     * You can use this prop to set value for a Class or java.util.List of some class
     * ex.: UserBot
     */
    String BotSource() default "";

}
