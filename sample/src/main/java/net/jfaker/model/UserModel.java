package net.jfaker.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserModel {

    private long id;

    private String name;

    private Calendar birthday;

    private BigDecimal salary;

    private Date createdAt;

    private OffsetDateTime updatedAt;

    private boolean active;

    private List<ContactModel> contacts;

    private SystemUserModel systemUser;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(final Calendar birthday) {
        this.birthday = birthday;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(final BigDecimal salary) {
        this.salary = salary;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public List<ContactModel> getContacts() {
        return contacts;
    }

    public void setContacts(final List<ContactModel> contacts) {
        this.contacts = contacts;
    }

    public SystemUserModel getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(final SystemUserModel systemUser) {
        this.systemUser = systemUser;
    }
}
