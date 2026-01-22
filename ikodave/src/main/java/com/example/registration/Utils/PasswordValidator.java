package com.example.registration.Utils;

public class PasswordValidator {

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 5;
    }

}
