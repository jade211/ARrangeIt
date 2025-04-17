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
import androidx.test.espresso.Root;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.android.material.textfield.TextInputLayout;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class LoginScreenTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Test
    public void testLoginScreenLogoVisible() {
        onView(withId(R.id.logo)).check(matches(isDisplayed()));
    }
    @Test
    public void testLoginScreenEmailVisible() {
        onView(withId(R.id.email)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginScreenPasswordVisible() {
        onView(withId(R.id.password)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginScreenSignInVisible() {
        onView(withId(R.id.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginScreenSignUpVisible() {
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginScreenForgotPasswordVisible() {
        onView(withId(R.id.forgot_password)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserLoginClick() {
        onView(withId(R.id.email))
                .perform(typeText("test@example.com"));
        onView(withId(R.id.password))
                .perform(typeText("Password123!"));
        closeSoftKeyboard();
        onView(withId(R.id.sign_in))
                .perform(click());
    }

    @Test
    public void testSignUpNavigation() {
        onView(withId(R.id.sign_up)).perform(click());
        onView(withText("Create an Account")).check (matches(isDisplayed()));
        onView(withText("Please enter your details to register")).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyEmailValidation() {
        onView(withId(R.id.email)).perform(clearText());
        closeSoftKeyboard();

        onView(withId(R.id.password)).perform(typeText("Password123!"));
        closeSoftKeyboard();

        onView(withId(R.id.sign_in)).perform(click());
        onView(withId(R.id.email_input_layout)).check(matches(hasTextInputLayoutErrorText("Email cannot be empty")));
    }

    @Test
    public void testInvalidEmailFormat() {
        onView(withId(R.id.email)).perform(typeText("invalid-email"));
        closeSoftKeyboard();

        onView(withId(R.id.email_input_layout))
                .check(matches(hasTextInputLayoutErrorText("Please enter a valid email address")));
    }

    @Test
    public void testValidEmailFormat() {
        onView(withId(R.id.email)).perform(typeText("valid@example.com"));
        closeSoftKeyboard();

        onView(withId(R.id.email_input_layout)).check(matches(not(hasErrorText("Please enter a valid email address"))));
    }

    @Test
    public void testForgotPasswordAppears() {
        onView(withId(R.id.forgot_password)).perform(click());
        onView(withId(R.id.emailBox)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonReset)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonCancel)).check(matches(isDisplayed()));
    }

    @Test
    public void testForgotPasswordCancel() {
        onView(withId(R.id.forgot_password)).perform(click());
        onView(withId(R.id.buttonCancel)).perform(click());
        onView(withId(R.id.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testForgotPasswordWithEmptyEmail() {
        onView(withId(R.id.forgot_password)).perform(click());
        onView(withId(R.id.buttonReset)).perform(click());

        onView(withId(R.id.emailErrorText)).check(matches(isDisplayed()));
        onView(withId(R.id.emailErrorText)).check(matches(withText("Email cannot be empty")));
    }

    @Test
    public void testForgotPasswordWithInvalidEmail() {
        onView(withId(R.id.forgot_password)).perform(click());
        onView(withId(R.id.emailBox)).perform(typeText("not-an-email"));
        closeSoftKeyboard();
        onView(withId(R.id.buttonReset)).perform(click());

        onView(withId(R.id.emailErrorText)).check(matches(isDisplayed()));
        onView(withId(R.id.emailErrorText)).check(matches(withText("Please enter a valid email address")));
    }


    @Test
    public void testIncorrectLoginCredentials() {
        onView(withId(R.id.email)).perform(typeText("wrong@example.com"));
        closeSoftKeyboard();
        onView(withId(R.id.password)).perform(typeText("WrongPass123!"));
        closeSoftKeyboard();

        SystemClock.sleep(500);
        onView(withId(R.id.sign_in)).perform(click());
        onView(withId(R.id.sign_in)).check(matches(isDisplayed()));
    }

    @Test
    public void testForgotPasswordWithValidEmail() {
        onView(withId(R.id.forgot_password)).perform(click());
        onView(withId(R.id.emailBox)).perform(typeText("testing@example.com"));
        closeSoftKeyboard();
        onView(withId(R.id.buttonReset)).perform(click());


        SystemClock.sleep(1000);
        onView(withId(R.id.sign_in)).check(matches(isDisplayed()));
    }


    // HELPERS
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