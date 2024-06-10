package net.jfaker.model;

public record SetterStatement(String name) {

    public SetterStatement(final String name) {
        this.name = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
