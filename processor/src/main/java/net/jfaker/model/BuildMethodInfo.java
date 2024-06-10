package net.jfaker.model;

public abstract class BuildMethodInfo {

    private final String name;
    private final ClassInfo resultClass;
    private final ClassInfo usedInStatement;

    public BuildMethodInfo(final String name,
                           final ClassInfo resultClass,
                           final ClassInfo usedInStatement) {
        this.name = name;
        this.resultClass = resultClass;
        this.usedInStatement = usedInStatement;
    }

    public String getName() {
        return name;
    }

    public ClassInfo getResultClass() {
        return resultClass;
    }

    public ClassInfo getUsedInStatement() {
        return usedInStatement;
    }

}
