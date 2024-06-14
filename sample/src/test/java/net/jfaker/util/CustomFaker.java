package net.jfaker.util;

import net.datafaker.Faker;
import net.jfaker.annotation.AutoFakerBot;
import net.jfaker.annotation.BotBuildStrategy;
import net.jfaker.annotation.BuilderStrategy;
import net.jfaker.annotation.ConstructorStrategy;
import net.jfaker.annotation.FakerInfo;
import net.jfaker.annotation.FieldConfiguration;
import net.jfaker.annotation.SetterStrategy;

import static net.jfaker.model.BuilderInstantiateMethod.DIRECT_INSTANTIATE;

@FakerInfo(
        botsConfiguration = {
                @AutoFakerBot(
                        generatedInstance = "net.jfaker.model.UserModel",
                        abstractBotQualifiedName = "net.jfaker.util.CustomAbstractBot",
                        packageToGenerate = "net.autofaker.bot",
                        botBuildStrategy = @BotBuildStrategy(
                                setterStrategy = @SetterStrategy(
                                        fieldsConfiguration = {
                                                @FieldConfiguration(name = "systemUser", botSource = "SystemUserModelBot"),
                                                @FieldConfiguration(name = "contacts", botSource = "ContactModelBot")
                                        }
                                )
                        )
                ),
                @AutoFakerBot(
                        generatedInstance = "net.jfaker.model.ContactModel",
                        packageToGenerate = "net.autofaker.bot",
                        botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy)
                ),
                @AutoFakerBot(
                        generatedInstance = "net.jfaker.model.SystemUserModel",
                        packageToGenerate = "net.autofaker.bot",
                        botBuildStrategy = @BotBuildStrategy(constructorStrategy = @ConstructorStrategy)
                ),
                @AutoFakerBot(
                        generatedInstance = "net.jfaker.model.BookModel",
                        packageToGenerate = "net.autofaker.bot",
                        botBuildStrategy = @BotBuildStrategy(setterStrategy = @SetterStrategy)
                ),
                @AutoFakerBot(
                        generatedInstance = "net.jfaker.model.CarModel",
                        packageToGenerate = "net.autofaker.bot",
                        botBuildStrategy = @BotBuildStrategy(
                                builderStrategy = @BuilderStrategy(
                                        builderQualifiedName = "net.jfaker.model.CarModel.CarModelBuilder",
                                        instantiateMethod = DIRECT_INSTANTIATE
                                )
                        )
                ),
                @AutoFakerBot(
                        generatedInstance = "net.jfaker.model.DogModel",
                        generatedInstanceSuperClass = "net.jfaker.model.AnimalModel",
                        packageToGenerate = "net.autofaker.bot",
                        botBuildStrategy = @BotBuildStrategy(setterStrategy = @SetterStrategy)
                )
        }
)
public class CustomFaker extends Faker {
}
