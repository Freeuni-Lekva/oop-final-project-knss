package com.example.registration.Responce;

public class UserRegistrationInput {

    private final String mail;
    private final String username;
    private final String password;
    private final String confirmPassword;

    public UserRegistrationInput(String mail, String username, String password, String confirmPassword) {
        this.mail = mail;
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getMail() {
        return mail;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
