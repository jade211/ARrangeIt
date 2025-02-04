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
        if (password == null) {
            return false;
        }
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$";
        return password.matches(passwordPattern);
    }

    public static String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email cannot be empty";
        } else if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        } else {
            return null;
        }
    }

    // Validate password
    public static String validatePassword(String password) {
        if (password.isEmpty()) {
            return "Password cannot be empty";
        } else if (!isValidPassword(password)) {
            return "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character.";
        } else {
            return null;
        }
    }
}