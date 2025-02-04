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
    }

    @Test
    public void testValidPassword() {
        assertTrue(FieldValidatorHelper.isValidPassword("password123"));
        assertTrue(FieldValidatorHelper.isValidPassword("123456"));
    }

    @Test
    public void testInvalidPassword() {
        assertFalse(FieldValidatorHelper.isValidPassword("123"));
        assertFalse(FieldValidatorHelper.isValidPassword(""));
        assertFalse(FieldValidatorHelper.isValidPassword(null));
    }
}
