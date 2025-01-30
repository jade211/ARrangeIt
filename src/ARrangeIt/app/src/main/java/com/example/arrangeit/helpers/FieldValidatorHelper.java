package com.example.arrangeit.helpers;

public class FieldValidatorHelper {

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailPattern);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
