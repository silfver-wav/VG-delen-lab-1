package com.example.databasinterface.model;

/**
 * Representation of a user
 */
public class User {

    private String email;
    private String username;

    public User(String email, String username) {
        this.email = email;
        this.username = username;
        System.out.println(this.username + this.email);
    }

    public User(String username) {
        this(null, username);
    }

    public String getEmail() {
        return email;
    }
    public String getUsername() { return username; }

    @Override
    public String toString() {
        return username;
    }
}
