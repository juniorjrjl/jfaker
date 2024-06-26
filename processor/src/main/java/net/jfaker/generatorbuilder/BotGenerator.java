package net.jfaker.generatorbuilder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import net.jfaker.generatorbuilder.property.IPropertyInitializer;
import net.jfaker.generatorbuilder.property.NullablePropertyInitializer;
import net.jfaker.model.BotClassInfo;
import net.jfaker.model.BuildMethodInfo;
import net.jfaker.model.FakerPropertyInfo;
import net.jfaker.model.PropertyInfo;
import net.jfaker.model.SetterBuilderMethodInfo;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.function.Supplier;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Abstract class used to generate bot class code
 */
public abstract class BotGenerator<T extends BuildMethodInfo> {

    private final Types types;
    private final List<IPropertyInitializer> propertyInitializers;

    /**
     * @param types used to convert primitive types to Class
     * @param propertyInitializers list of class used to initialize properties in bots
     */
    protected BotGenerator(final Types types, final List<IPropertyInitializer> propertyInitializers){
        this.types = types;
        this.propertyInitializers = propertyInitializers;
    }

    /**
     * Create all code used by bot to work
     * @param botClassInfo information about bot
     * @return code to generate bot class
     */
    public JavaFile.Builder create(final BotClassInfo botClassInfo, final T buildMethodInfo){
        final var botSuperClassInfo = botClassInfo.getBotSuperClassInfo();
        final var fakerExtends = ParameterizedTypeName.get(
                ClassName.get(botSuperClassInfo.getPackageName(), botSuperClassInfo.getSimpleName()),
                ClassName.get(
                        botSuperClassInfo.getGeneratedClass().getPackageName(),
                        botSuperClassInfo.getGeneratedClass().getSimpleName()
                ));
        final var generateBuilderClass = TypeSpec.classBuilder(botClassInfo.getSimpleName())
                .addModifiers(PUBLIC)
                .superclass(fakerExtends)
                .addJavadoc("A bot to generate instance of {@link $N $N} with random data", botClassInfo.getQualifiedName(), botClassInfo.getSimpleName());

        generateBuilderClass.addField(generateFakerProperty(botClassInfo.getFaker()));

        generateBuilderClass.addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(PRIVATE)
                .build());

        botClassInfo.getProperties()
                .forEach(p -> generateBuilderClass.addField(generateProps(botClassInfo, p)));


        generateBuilderClass.addMethod(generateStaticBuilderMethod(botClassInfo));

        botClassInfo.getSettersBuilder()
                .forEach(p -> generateBuilderClass.addMethod(generateBuilderSetter(botClassInfo, p)));

        generateBuilderClass.addMethod(generateBuild(buildMethodInfo));

        return JavaFile.builder(botClassInfo.getPackageName(), generateBuilderClass.build());
    }

    private FieldSpec generateFakerProperty(final FakerPropertyInfo fakerInfo){
        return FieldSpec.builder(TypeName.get(fakerInfo.getType()), fakerInfo.getName(), PRIVATE)
                .initializer("new $N", fakerInfo.getInitialization())
                .build();
    }

    /**
     * generate a static builder method
     * @param botClassInfo bot information
     * @return method builder information
     */
    private MethodSpec generateStaticBuilderMethod(final BotClassInfo botClassInfo){
        return MethodSpec.methodBuilder("builder")
                .addModifiers(PUBLIC, STATIC)
                .returns(ClassName.get(botClassInfo.getPackageName(), botClassInfo.getSimpleName()))
                .addStatement("return new $N()", botClassInfo.getSimpleName())
                .addJavadoc("Static builder to receive instance of {@link $N $N} \n", botClassInfo.getQualifiedName(), botClassInfo.getSimpleName())
                .addJavadoc("@return a bot instance")
                .build();
    }

    /**
     * generate properties used by bot, a properties needs to be a Supplier of property type in generated class
     * @param botClassInfo information about bot
     * @param propertyInfo information about bot property
     * @return property used by bot
     */
    private FieldSpec generateProps(final BotClassInfo botClassInfo, final PropertyInfo propertyInfo){
        final var type = generateSupplierFromType(propertyInfo.originalType());
        final var builder = FieldSpec.builder(type, propertyInfo.name(), PRIVATE);
        final var nullablePropertyInitializer = new NullablePropertyInitializer();
        if (propertyInfo.nullable()){
            nullablePropertyInitializer.setInitializerBlock(propertyInfo, builder);
        } else if (propertyInfo.hasCustomSource() && propertyInfo.customSource().startsWith("faker")){
            builder.initializer("() -> " + propertyInfo.customSource());
        } else {
            propertyInitializers.stream().filter(p -> p.isEligible(botClassInfo, propertyInfo))
                    .findFirst()
                    .orElse(nullablePropertyInitializer)
                    .setInitializerBlock(propertyInfo, builder);
        }
        return builder.build();
    }

    /**
     * Method who generate with Methods to customise some data in instance
     * @param botClassInfo information about bot
     * @param setterBuilderMethodInfo information about setter builder
     * @return with method used by bot
     */
    private MethodSpec generateBuilderSetter(final BotClassInfo botClassInfo, final SetterBuilderMethodInfo setterBuilderMethodInfo){
        final var type = generateSupplierFromType(setterBuilderMethodInfo.getOriginalType());
        return MethodSpec.methodBuilder(setterBuilderMethodInfo.getName())
                .addModifiers(PUBLIC)
                .returns(ClassName.get(botClassInfo.getPackageName(), botClassInfo.getSimpleName()))
                .addParameter(type, setterBuilderMethodInfo.getParameterName(), FINAL)
                .addStatement("this.$N = $N", setterBuilderMethodInfo.getParameterName(), setterBuilderMethodInfo.getParameterName())
                .addStatement("return this")
                .addJavadoc("Override random default value for property $N \n", setterBuilderMethodInfo.getParameterName())
                .addJavadoc("@param $N a callback to change value of property $N \n", setterBuilderMethodInfo.getParameterName(), setterBuilderMethodInfo.getParameterName())
                .addJavadoc("@return a bot instance with new callback for property $N \n", setterBuilderMethodInfo.getParameterName())
                .build();
    }

    /**
     * Method who need be implemented according strategy to generate class instance
     * @param buildMethodInfo information about build's bot method
     * @return build method used by bot to create instances
     */
    protected abstract MethodSpec generateBuild(final T buildMethodInfo);

    /**
     * Build a Supplier used by bot
     * @param type type involved by Supplier
     * @return Supplier used by bot
     */
    private ParameterizedTypeName generateSupplierFromType(final TypeMirror type){
        final var classType = type.getKind().isPrimitive() ? types.boxedClass((PrimitiveType)type).asType() : type;
        return ParameterizedTypeName.get(ClassName.get(Supplier.class), TypeName.get(classType));
    }

}
