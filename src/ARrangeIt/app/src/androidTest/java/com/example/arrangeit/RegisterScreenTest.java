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

import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.test.espresso.Root;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.Contract;
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
        onView(withId(R.id.password)).perform(clearText(), typeText("short"));
        closeSoftKeyboard();
        onView(withId(R.id.password_input_layout))
                .check(matches(hasTextInputLayoutErrorText(expectedPasswordError)));
    }

    @Test
    public void testPasswordMissingUppercase() {
        onView(withId(R.id.password)).perform(clearText(), typeText("alllowercase1!"));
        closeSoftKeyboard();
        onView(withId(R.id.password_input_layout))
                .check(matches(hasTextInputLayoutErrorText(expectedPasswordError)));
    }

    @Test
    public void testPasswordMissingLowercase() {
        onView(withId(R.id.password)).perform(clearText(), typeText("ALLUPPERCASE1!"));
        closeSoftKeyboard();
        onView(withId(R.id.password_input_layout))
                .check(matches(hasTextInputLayoutErrorText(expectedPasswordError)));
    }

    @Test
    public void testPasswordMissingDigit() {
        onView(withId(R.id.password)).perform(clearText(), typeText("NoDigitsHere!"));
        closeSoftKeyboard();
        onView(withId(R.id.password_input_layout))
                .check(matches(hasTextInputLayoutErrorText(expectedPasswordError)));
    }

    @Test
    public void testPasswordMissingSpecialChar() {
        onView(withId(R.id.password)).perform(clearText(), typeText("MissingSpecial1"));
        closeSoftKeyboard();
        onView(withId(R.id.password_input_layout))
                .check(matches(hasTextInputLayoutErrorText(expectedPasswordError)));
    }

    @Test
    public void testPasswordOnlyDigits() {
        onView(withId(R.id.password)).perform(clearText(), typeText("12345678"));
        closeSoftKeyboard();
        onView(withId(R.id.password_input_layout))
                .check(matches(hasTextInputLayoutErrorText(expectedPasswordError)));
    }

    @Test
    public void testPasswordOnlySpecialChars() {
        onView(withId(R.id.password)).perform(clearText(), typeText("!@#$%^&*"));
        closeSoftKeyboard();
        onView(withId(R.id.password_input_layout))
                .check(matches(hasTextInputLayoutErrorText(expectedPasswordError)));
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

    //HELPERS
    @NonNull
    @Contract(" -> new")
    public static Matcher<Root> isDialog() {
        return new TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(Root root) {
                int type = root.getWindowLayoutParams().get().type;
                return type == WindowManager.LayoutParams.TYPE_APPLICATION ||
                        type == WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is dialog");
            }
        };
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new BoundedMatcher<>(TextInputLayout.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with error text: " + expectedErrorText);
            }

            @Override
            protected boolean matchesSafely(TextInputLayout textInputLayout) {
                CharSequence error = textInputLayout.getError();
                return expectedErrorText.equals(error == null ? "" : error.toString());
            }
        };
    }
}