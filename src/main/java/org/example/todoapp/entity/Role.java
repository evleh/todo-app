package org.example.todoapp.entity;

public enum Role {
    USER,
    ADMIN;

    /**
     * Method converts Enum to a String that works together with Spring security "hasRole"-method
     * @return User role in String-format.
     */
    public String toGrantedAuthority() {
        return "ROLE_" + this.name();
    }
}
