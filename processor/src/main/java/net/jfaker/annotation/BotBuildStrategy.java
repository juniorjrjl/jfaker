package net.jfaker.annotation;

/**
 * strategy used by bot to create instances with random data in build method
 */
public @interface BotBuildStrategy {


    /**
     * configuration for use constructor in bot build method
     */
    ConstructorStrategy constructorStrategy() default @ConstructorStrategy(ignoreConfig = true);

    /**
     * configuration for use setter in bot build method
     */
    SetterStrategy setterStrategy() default @SetterStrategy(ignoreConfig = true);

    /**
     * configuration for use builder in bot build method
     */
    BuilderStrategy builderStrategy() default @BuilderStrategy(ignoreConfig = true);
}
