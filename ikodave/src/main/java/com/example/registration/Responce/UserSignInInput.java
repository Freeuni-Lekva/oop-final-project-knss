package com.example.registration.Responce;

public class UserSignInInput {

    private final String username;

    private final String password;

    public UserSignInInput(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
