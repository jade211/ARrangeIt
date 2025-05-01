package com.example.arrangeit.helpers;


/**
 * Helper class for validating user email and password
 * input fields.
 */
public class FieldValidatorHelper {

    /**
     * Validates if an email matches standard email format (name@domain.com)
     * @param email (user email entered)
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailPattern);
    }


    /**
     * Validates if a password meets secure complexity requirements
     * Contains at least 8 characters, one uppercase letter, one lowercase letter, one digit and one special character
     * @param password (user password entered)
     * @return true if it meets requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$";
        return password.matches(passwordPattern);
    }


    /**
     * Validates the email and returns an error message if it is invalid.
     * @param email (user email entered)
     * @return Error message if invalid, null if valid
     */
    public static String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email cannot be empty";
        } else if (!isValidEmail(email)) {
            return "Please enter a valid email address";
        } else {
            return null;
        }
    }


    /**
     * Validates a password and returns an error message if invalid.
     * @param password (user password entered)
     * @return Error message if invalid, null if valid
     */
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