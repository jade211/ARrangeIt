package com.example.arrangeit;

import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.arrangeit.Helpers.hasTextInputLayoutErrorText;
import static com.example.arrangeit.Helpers.testPasswordValidation;

import android.os.SystemClock;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegisterScreenTest {
    @Rule
    public ActivityScenarioRule<RegisterPage> activityRule =
            new ActivityScenarioRule<>(RegisterPage.class);

    String expectedPasswordError = "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character.";
    @Test
    public void testRegisterScreenLogoVisible() {
        onView(withId(R.id.logo)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterScreenEmailVisible() {
        onView(withId(R.id.email)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterScreenPasswordVisible() {
        onView(withId(R.id.password)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterScreenSignUpButtonVisible() {
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterScreenSignInLinkVisible() {
        onView(withId(R.id.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterScreenTitleVisible() {
        onView(withText("Create an Account")).check(matches(isDisplayed()));
    }

    @Test
    public void testRegisterScreenSubtitleVisible() {
        onView(withText("Please enter your details to register")).check(matches(isDisplayed()));
    }

    @Test
    public void testSignInNavigation() {
        onView(withId(R.id.sign_in)).perform(click());
        SystemClock.sleep(1000);
        onView(withText("Welcome back")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyEmailValidation() {
        onView(withId(R.id.email)).perform(clearText());
        closeSoftKeyboard();

        onView(withId(R.id.password)).perform(typeText("Password123!"));
        closeSoftKeyboard();

        onView(withId(R.id.sign_up)).perform(click());
        onView(withId(R.id.email_input_layout)).check(matches(hasTextInputLayoutErrorText("Email cannot be empty")));
    }

    @Test
    public void testInvalidEmailFormats() {
        String[] invalidEmails = {
                "invalidemail",
                "missing@dot",
                "missing.domain@",
                "@missing.namepart",
                "spaces in@email.com",
        };

        for (String email : invalidEmails) {
            onView(withId(R.id.email)).perform(clearText(), typeText(email));
            closeSoftKeyboard();
            onView(withId(R.id.email_input_layout)).check(matches(hasTextInputLayoutErrorText("Please enter a valid email address")));
        }
    }

    @Test
    public void testValidEmailFormats() {
        String[] validEmails = {
                "test@example.com",
                "firstname.lastname@example.com",
                "email@subdomain.example.com",
                "1234567890@example.com",
        };

        for (String email : validEmails) {
            onView(withId(R.id.email)).perform(clearText(), typeText(email));
            closeSoftKeyboard();
            onView(withId(R.id.email_input_layout)).check(matches(not(hasErrorText("Please enter a valid email address"))));
        }
    }

    @Test
    public void testEmptyPasswordValidation() {
        onView(withId(R.id.email)).perform(typeText("test@example.com"));
        closeSoftKeyboard();
        onView(withId(R.id.password)).perform(clearText());
        closeSoftKeyboard();
        onView(withId(R.id.sign_up)).perform(click());
        onView(withId(R.id.password_input_layout)).check(matches(hasTextInputLayoutErrorText("Password cannot be empty")));
    }


    @Test
    public void testPasswordTooShort() {

        testPasswordValidation("short", expectedPasswordError);
    }

    @Test
    public void testPasswordMissingUppercase() {
        testPasswordValidation("alllowercase1!", expectedPasswordError);
    }

    @Test
    public void testPasswordMissingLowercase() {
        testPasswordValidation("ALLUPPERCASE1!", expectedPasswordError);
    }

    @Test
    public void testPasswordMissingDigit() {
        testPasswordValidation("NoDigitsHere!", expectedPasswordError);
    }

    @Test
    public void testPasswordMissingSpecialChar() {
        testPasswordValidation("MissingSpecial1", expectedPasswordError);
    }

    @Test
    public void testPasswordOnlyDigits() {
        testPasswordValidation("12345678", expectedPasswordError);
    }

    @Test
    public void testPasswordOnlySpecialChars() {
        testPasswordValidation("!@#$%^&*", expectedPasswordError);
    }


    @Test
    public void testValidPasswordFormat() {
        onView(withId(R.id.password)).perform(typeText("StrongPass123!"));
        closeSoftKeyboard();

        onView(withId(R.id.password_input_layout)).check(matches(not(hasErrorText(expectedPasswordError))));
    }

    @Test
    public void testRealTimeEmailValidation() {
        onView(withId(R.id.email)).perform(typeText("invalid"));
        closeSoftKeyboard();

        onView(withId(R.id.email_input_layout))
                .check(matches(hasTextInputLayoutErrorText("Please enter a valid email address")));

        onView(withId(R.id.email)).perform(clearText(), typeText("valid@example.com"));
        closeSoftKeyboard();

        onView(withId(R.id.email_input_layout)).check(matches(not(hasErrorText("Please enter a valid email address"))));
    }

    @Test
    public void testRealTimePasswordValidation() {
        onView(withId(R.id.password)).perform(typeText("weak"));
        closeSoftKeyboard();

        onView(withId(R.id.password_input_layout))
                .check(matches(hasTextInputLayoutErrorText("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character.")));

        onView(withId(R.id.password)).perform(clearText(), typeText("StrongPass123!"));
        closeSoftKeyboard();

        onView(withId(R.id.password_input_layout)).check(matches(not(hasErrorText("Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a digit, and a special character."))));
    }

    @Test
    public void testSuccessfulRegistrationNavigation() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        onView(withId(R.id.email)).perform(typeText(uniqueEmail));
        closeSoftKeyboard();
        onView(withId(R.id.password)).perform(typeText("Password123!"));
        closeSoftKeyboard();
        onView(withId(R.id.sign_up)).perform(click());
        SystemClock.sleep(1000);
        onView(withText("Welcome back")).check(matches(isDisplayed()));
    }

    @Test
    public void testDuplicateEmailRegistration() {
        String existingEmail = "test@gmail.com";

        onView(withId(R.id.email)).perform(typeText(existingEmail));
        closeSoftKeyboard();
        onView(withId(R.id.password)).perform(typeText("Password123!"));
        closeSoftKeyboard();
        onView(withId(R.id.sign_up)).perform(click());
        SystemClock.sleep(1000);
        onView(withId(R.id.email_input_layout))
                .check(matches(hasTextInputLayoutErrorText("This email is already in use. Please use a different email.")));
    }



}