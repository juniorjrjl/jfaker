package net.jfaker.model;

import java.util.ArrayList;
import java.util.List;

public class BuildMethodInfoBuilderStrategy extends BuildMethodInfo{

    private final String builderMethodName;
    private final List<SetterBuilderStatement> settersBuilderStatement;
    private final List<String> settersValues;
    private final String buildMethodName;
    private final BuilderInstantiateMethod builderInstantiateMethod;

    public static BuildMethodInfoBuilderStrategyBuilder builder(){
        return new BuildMethodInfoBuilderStrategyBuilder();
    }

    public BuildMethodInfoBuilderStrategy(final String name,
                                          final ClassInfo resultClass,
                                          final ClassInfo usedInStatement,
                                          final String builderMethodName,
                                          final List<SetterBuilderStatement> settersBuilderStatement,
                                          final List<String> settersValues,
                                          final String buildMethodName,
                                          final BuilderInstantiateMethod builderInstantiateMethod) {
        super(name, resultClass, usedInStatement);
        this.builderMethodName = builderMethodName;
        this.settersBuilderStatement = settersBuilderStatement;
        this.settersValues = settersValues;
        this.buildMethodName = buildMethodName;
        this.builderInstantiateMethod = builderInstantiateMethod;
    }

    public String getBuilderMethodName() {
        return builderMethodName;
    }

    public List<SetterBuilderStatement> getSettersBuilderStatement() {
        return settersBuilderStatement;
    }

    public List<String> getSettersValues() {
        return settersValues;
    }

    public String getBuildMethodName() {
        return buildMethodName;
    }

    public BuilderInstantiateMethod getBuilderInstantiateMethod() {
        return builderInstantiateMethod;
    }

    public static final class BuildMethodInfoBuilderStrategyBuilder {
        private String name;
        private ClassInfo resultClass;
        private ClassInfo usedInStatement;
        private String builderMethodName;
        private final List<SetterBuilderStatement> settersBuilderStatement = new ArrayList<>();
        private final List<String> settersValues = new ArrayList<>();
        private String buildMethodName;
        private BuilderInstantiateMethod builderInstantiateMethod;

        public BuildMethodInfoBuilderStrategyBuilder withName(final String name) {
            this.name = name;
            return this;
        }

        public BuildMethodInfoBuilderStrategyBuilder withResultClass(final ClassInfo resultClass) {
            this.resultClass = resultClass;
            return this;
        }

        public BuildMethodInfoBuilderStrategyBuilder withUsedInStatement(final ClassInfo usedInStatement) {
            this.usedInStatement = usedInStatement;
            return this;
        }

        public BuildMethodInfoBuilderStrategyBuilder withBuilderMethodName(final String builderMethodName) {
            this.builderMethodName = builderMethodName;
            return this;
        }

        public BuildMethodInfoBuilderStrategyBuilder withSettersStatement(final List<SetterBuilderStatement> settersStatement) {
            this.settersBuilderStatement.addAll(settersStatement);
            return this;
        }

        public BuildMethodInfoBuilderStrategyBuilder withSettersValues(final List<String> settersValues) {
            this.settersValues.addAll(settersValues);
            return this;
        }

        public BuildMethodInfoBuilderStrategyBuilder withBuildMethodName(final String buildMethodName) {
            this.buildMethodName = buildMethodName;
            return this;
        }

        public BuildMethodInfoBuilderStrategyBuilder withBuilderInstantiateMethod(final BuilderInstantiateMethod builderInstantiateMethod){
            this.builderInstantiateMethod = builderInstantiateMethod;
            return this;
        }

        public BuildMethodInfoBuilderStrategy build() {
            return new BuildMethodInfoBuilderStrategy(name,
                    resultClass,
                    usedInStatement,
                    builderMethodName,
                    settersBuilderStatement,
                    settersValues,
                    buildMethodName,
                    builderInstantiateMethod
            );
        }
    }
}
