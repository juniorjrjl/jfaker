package net.jfaker.model;

import java.util.ArrayList;
import java.util.List;

public class BuildMethodInfoConstructorStrategy extends BuildMethodInfo{

    private final List<String> settersValues;

    public static BuildMethodConstructorStrategyBuilder builder(){
        return new BuildMethodConstructorStrategyBuilder();
    }

    public BuildMethodInfoConstructorStrategy(final String name,
                                              final ClassInfo resultClass,
                                              final ClassInfo usedInStatement,
                                              final List<String> settersValues) {
        super(name, resultClass, usedInStatement);
        this.settersValues = settersValues;
    }

    public List<String> getSettersValues() {
        return settersValues;
    }

    public static final class BuildMethodConstructorStrategyBuilder {
        private String name;
        private ClassInfo resultClass;
        private ClassInfo usedInStatement;
        private final List<String> settersValues = new ArrayList<>();

        public BuildMethodConstructorStrategyBuilder withName(final String name) {
            this.name = name;
            return this;
        }

        public BuildMethodConstructorStrategyBuilder withResultClass(final ClassInfo resultClass) {
            this.resultClass = resultClass;
            return this;
        }

        public BuildMethodConstructorStrategyBuilder withUsedInStatement(final ClassInfo usedInStatement) {
            this.usedInStatement = usedInStatement;
            return this;
        }

        public BuildMethodConstructorStrategyBuilder withSettersValues(final List<String> settersValues) {
            this.settersValues.addAll(settersValues);
            return this;
        }

        public BuildMethodInfoConstructorStrategy build() {
            return new BuildMethodInfoConstructorStrategy(name, resultClass, usedInStatement, settersValues);
        }
    }

}
