package net.jfaker.model;

import static net.jfaker.util.StringUtil.firstLetterToUpperCase;

public class SetterBuilderStatement {

    private final String name;

    public SetterBuilderStatement(final String prefix, final String name) {
        this.name = prefix + firstLetterToUpperCase(name);
    }

    public String getName() {
        return name;
    }

}
