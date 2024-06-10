package net.jfaker.model;

public class SystemUserModel {

    private final String login;

    private final String password;

    private final boolean active;

    public SystemUserModel(final String login, final String password, final boolean active) {
        this.login = login;
        this.password = password;
        this.active = active;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return active;
    }
}
