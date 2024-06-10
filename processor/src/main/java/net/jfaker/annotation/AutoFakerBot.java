package net.jfaker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Annotation to define bot configuration to generate a random instance of some class
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface AutoFakerBot {

    /**
     *Qualified name of class who will be inherited by all bots
     * by default bots will use a {@link net.jfaker.bot.AbstractBot AbstractBot}
     */
    String abstractBotQualifiedName() default "net.jfaker.bot.AbstractBot";

    /**
     * A qualified name of class returned by bot (ex.: com.my.domain.model.UserModel)
     */
    String generatedInstance();

    /**
     * A package where your bot will be generated (ex.: com.my.domain.bot)
     */
    String packageToGenerate();

    /**
     * A qualified name of superclass defined in generatedInstance (use only your instance have superclass and you want a polymorphic bot)
     */
    String generatedInstanceSuperClass() default "";

    /**
     * A Bot ClassName, if not defined a bot will be generated with your Class plus 'Bot' suffix
     * ex.: Class User generate a UserBot class bot
     */
    String botClassName() default "";

    /**
     * configuration about build method used by bot
     */
    BotBuildStrategy botBuildStrategy() default @BotBuildStrategy;

}
