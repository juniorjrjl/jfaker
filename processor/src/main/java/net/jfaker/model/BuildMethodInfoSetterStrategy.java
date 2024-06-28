package net.jfaker.model;

import java.util.ArrayList;
import java.util.List;

public class BuildMethodInfoSetterStrategy extends BuildMethodInfo{

    private final List<SetterStatement> settersStatement;
    private final List<String> settersValues;

    public static BuildMethodInfoSetterStrategyBuilder builder(){
        return new BuildMethodInfoSetterStrategyBuilder();
    }

    public BuildMethodInfoSetterStrategy(final String name,
                                         final ClassInfo resultClass,
                                         final ClassInfo usedInStatement,
                                         final List<SetterStatement> settersStatement,
                                         final List<String> settersValues) {
        super(name, resultClass, usedInStatement);
        this.settersStatement = settersStatement;
        this.settersValues = settersValues;
    }

    public List<SetterStatement> getSettersStatement() {
        return settersStatement;
    }

    public List<String> getSettersValues() {
        return settersValues;
    }

    public static final class BuildMethodInfoSetterStrategyBuilder {
        private String name;
        private ClassInfo resultClass;
        private ClassInfo usedInStatement;
        private final List<SetterStatement> settersStatement = new ArrayList<>();
        private final List<String> settersValues = new ArrayList<>();

        public BuildMethodInfoSetterStrategyBuilder withName(final String name) {
            this.name = name;
            return this;
        }

        public BuildMethodInfoSetterStrategyBuilder withResultClass(final ClassInfo resultClass) {
            this.resultClass = resultClass;
            return this;
        }

        public BuildMethodInfoSetterStrategyBuilder withUsedInStatement(final ClassInfo usedInStatement) {
            this.usedInStatement = usedInStatement;
            return this;
        }

        public BuildMethodInfoSetterStrategyBuilder withSettersStatement(final List<SetterStatement> settersStatement) {
            this.settersStatement.addAll(settersStatement);
            return this;
        }

        public BuildMethodInfoSetterStrategyBuilder withSettersValues(final List<String> settersValues) {
            this.settersValues.addAll(settersValues);
            return this;
        }

        public BuildMethodInfoSetterStrategy build() {
            return new BuildMethodInfoSetterStrategy(name, resultClass, usedInStatement, settersStatement, settersValues);
        }
    }
}
