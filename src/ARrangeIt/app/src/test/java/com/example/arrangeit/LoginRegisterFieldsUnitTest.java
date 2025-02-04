package com.example.arrangeit;

import static org.junit.Assert.*;
import org.junit.Test;
import com.example.arrangeit.helpers.FieldValidatorHelper;


public class LoginRegisterFieldsUnitTest {

    @Test
    public void testValidEmail() {
        assertTrue(FieldValidatorHelper.isValidEmail("test@example.com"));
        assertTrue(FieldValidatorHelper.isValidEmail("user@domain.org"));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(FieldValidatorHelper.isValidEmail("invalid-email"));
        assertFalse(FieldValidatorHelper.isValidEmail("user@.com"));
        assertFalse(FieldValidatorHelper.isValidEmail(""));
        assertFalse(FieldValidatorHelper.isValidEmail(null));
        assertFalse(FieldValidatorHelper.isValidEmail("user@com"));
    }

    @Test
    public void testValidPassword() {
        assertTrue(FieldValidatorHelper.isValidPassword("Password123!"));
        assertTrue(FieldValidatorHelper.isValidPassword("Hello@123456"));
    }

    @Test
    public void testInvalidPassword() {
        assertFalse(FieldValidatorHelper.isValidPassword("123"));
        assertFalse(FieldValidatorHelper.isValidPassword(""));
        assertFalse(FieldValidatorHelper.isValidPassword(null));
        assertFalse(FieldValidatorHelper.isValidPassword("password"));
        assertFalse(FieldValidatorHelper.isValidPassword("PASSWORD1"));
        assertFalse(FieldValidatorHelper.isValidPassword("Password!"));
        assertFalse(FieldValidatorHelper.isValidPassword("Password1"));
    }

    @Test
    public void testValidEmail_ReturnValidEmail() {
        assertNull(FieldValidatorHelper.validateEmail("test@example.com"));
    }

    @Test
    public void testValidEmail_ReturnEmptyEmail() {
        assertEquals("Email cannot be empty", FieldValidatorHelper.validateEmail(""));
    }

    @Test
    public void testValidEmail_ReturnInvalidEmail() {
        assertEquals("Please enter a valid email address", FieldValidatorHelper.validateEmail("invalid-email"));
    }



    @Test
    public void testValidPassword_ReturnValidPassword() {
        assertNull(FieldValidatorHelper.validatePassword("Password1!"));
    }

    @Test
    public void testValidPassword_ReturnEmptyPassword() {
        assertEquals("Password cannot be empty", FieldValidatorHelper.validatePassword(""));
    }


    @Test
    public void testValidPassword_ReturnInvalidPassword() {
        assertEquals(
                "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character.", FieldValidatorHelper.validatePassword("weak")
        );
    }
}


