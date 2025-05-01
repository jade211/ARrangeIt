package com.example.arrangeit;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import org.hamcrest.Description;
import org.hamcrest.Matcher;


public class Helpers {

    static void loginTestUser() {
        onView(withId(R.id.email)).perform(typeText("test@example.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("Password123!"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.sign_in)).perform(click());
    }

    static void ensureTestUserLoggedIn() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                "test@example.com",
                "Password123!"
        );

        // Wait for authentication to complete
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    static void testPasswordValidation(String password, String expectedPasswordError) {
        onView(withId(R.id.password)).perform(clearText(), typeText(password));
        closeSoftKeyboard();

        onView(withId(R.id.password_input_layout)).check(matches(hasTextInputLayoutErrorText(expectedPasswordError)));
    }

    static ViewAction touchDown(final int x, final int y) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() {
                return isRoot();
            }
            @Override public String getDescription() {
                return "Touch down at (" + x + "," + y + ")";
            }
            @Override public void perform(UiController uiController, View view) {
                MotionEvent down = MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN,
                        x, y, 0
                );
                view.dispatchTouchEvent(down);
            }
        };
    }

    static ViewAction moveTo(final int x, final int y) {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() {
                return isRoot();
            }
            @Override public String getDescription() {
                return "Move to (" + x + "," + y + ")";
            }
            @Override public void perform(UiController uiController, View view) {
                MotionEvent move = MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_MOVE,
                        x, y, 0
                );
                view.dispatchTouchEvent(move);
            }
        };
    }

    static ViewAction touchUp() {
        return new ViewAction() {
            @Override public Matcher<View> getConstraints() { return isRoot(); }
            @Override public String getDescription() { return "Touch up"; }
            @Override public void perform(UiController uiController, View view) {
                MotionEvent up = MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP,
                        0, 0, 0
                );
                view.dispatchTouchEvent(up);
            }
        };
    }

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
    public static int[] getScreenSize() {
        int[] size = new int[2];
        androidx.test.InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager)
                    androidx.test.InstrumentationRegistry.getInstrumentation().getContext()
                            .getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            size[0] = metrics.widthPixels;
            size[1] = metrics.heightPixels;
        });
        return size;
    }

    public static ViewAction clickXY(final int x, final int y) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Perform clicks at coordinates (" + x + ", " + y + ")";
            }

            @Override
            public void perform(UiController uiController, View view) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                float screenX = location[0] + x;
                float screenY = location[1] + y;

                MotionEvent down = MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_DOWN,
                        screenX,
                        screenY,
                        0
                );
                MotionEvent up = MotionEvent.obtain(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_UP,
                        screenX,
                        screenY,
                        0
                );
                view.dispatchTouchEvent(down);
                view.dispatchTouchEvent(up);
                uiController.loopMainThreadForAtLeast(200);
            }
        };
    }

    public static void testPlaceFurniture() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        onView(withId(R.id.nav_catalogue)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));
        device.findObject(new UiSelector()
                        .className("android.widget.TextView")
                        .textContains("Modern Arm Chair"))
                .click();
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.itemName)).check(matches(isDisplayed()));
        onView(withId(R.id.itemPrice)).check(matches(isDisplayed()));
        device.swipe(500, 1500, 500, 500, 20);
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.place_in_ar_button)).perform(click());

        int[] screenSize = getScreenSize();
        int centerX = screenSize[0]/2;
        int centerY = screenSize[1]/2;
        onView(isRoot()).perform(waitFor(5000));
        onView(isRoot()).perform(clickXY(centerX, centerY));
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.model_counter))
                .check(matches(allOf(
                        isDisplayed(),
                        withText("Models: 1")
                )));
    }
}
