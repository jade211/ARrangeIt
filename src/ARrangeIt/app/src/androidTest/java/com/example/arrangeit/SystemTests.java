package com.example.arrangeit;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.arrangeit.Helpers.clickXY;
import static com.example.arrangeit.Helpers.getScreenSize;
import static com.example.arrangeit.Helpers.loginTestUser;
import static com.example.arrangeit.Helpers.testPlaceFurniture;
import static com.example.arrangeit.Helpers.waitFor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.os.SystemClock;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SystemTests {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    private FirebaseAuth firebaseAuth;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "Password123!";

    @Before
    public void setup() {
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
        }
        Intents.init();
    }

    @After
    public void cleanup() {
        Intents.release();
    }
    @After
    public void tearDown() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // user registration
    @Test
    public void testUserRegistrationFlow() {
        onView(withId(R.id.sign_up)).perform(click());
        onView(withId(R.id.sign_up)).check(matches(isDisplayed()));

        // enter registration details
        onView(withId(R.id.email)).perform(typeText(TEST_EMAIL + System.currentTimeMillis()), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(TEST_PASSWORD), closeSoftKeyboard());

        // click sign up and redirected back to sign in
        onView(withId(R.id.sign_up)).perform(click());
        onView(withId(R.id.sign_in)).check(matches(isDisplayed()));


    }

    // successful login
    @Test
    public void testLoginFlow() {
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.email)).perform(typeText(TEST_EMAIL), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(TEST_PASSWORD), closeSoftKeyboard());

        onView(withId(R.id.sign_in)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
        Intents.intended(IntentMatchers.hasComponent(ARCorePage.class.getName()));
    }

    // forgot password
    @Test
    public void testForgotPasswordFlow() {
        onView(withId(R.id.forgot_password)).perform(click());
        onView(withId(R.id.emailBox)).check(matches(isDisplayed()));
        onView(withId(R.id.emailBox)).perform(typeText(TEST_EMAIL), closeSoftKeyboard());
        onView(withId(R.id.buttonReset)).perform(click());
        onView(withId(R.id.emailBox)).check(matches(isDisplayed()));
    }

    // arcore end-to-end placement test
    @Test
    public void testFurniturePlacementFlow() throws UiObjectNotFoundException {
        loginTestUser();
        onView(isRoot()).perform(waitFor(1000));

        // navigate to furniture catalogue
        onView(withId(R.id.nav_catalogue)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        onView(isRoot()).perform(waitFor(2000));

        // click on a furniture item
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.findObject(new UiSelector()
                        .className("android.widget.TextView")
                        .textContains("Modern Arm Chair"))
                .click();
        onView(isRoot()).perform(waitFor(2000));
        device.swipe(500, 1500, 500, 500, 20);
        onView(withId(R.id.place_in_ar_button)).check(matches(isDisplayed()));

        // place in AR clicked
        onView(withId(R.id.place_in_ar_button)).perform(click());
        onView(isRoot()).perform(waitFor(5000));


        // verify furniture model is in environment
        int[] screenSize = getScreenSize();
        int centerX = screenSize[0]/2;
        int centerY = screenSize[1]/2;
        onView(isRoot()).perform(waitFor(2000));
        onView(isRoot()).perform(clickXY(centerX, centerY));
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.model_counter))
                .check(matches(allOf(
                        isDisplayed(),
                        withText("Models: 1")
                )));
    }


    // arcore end-to-end saved layouts test
    @Test
    public void testSavedLayoutsFlow() throws UiObjectNotFoundException {
        loginTestUser();
        onView(isRoot()).perform(waitFor(1000));
        testPlaceFurniture();
        onView(withId(R.id.save_button)).perform(click());
        onView(withHint("Enter layout name")).perform(typeText("Test Layout 1"));
        onView(withText("Save")).perform(click());
        onView(isRoot()).perform(waitFor(10000));
        onView(withId(R.id.nav_screenshots)).perform(click());
        onView(withId(R.id.screenshots_grid))
                .check(matches(hasMinimumChildCount(1)));

        onView(allOf(withText("Test Layout 1"), isDisplayed()))
                .check(matches(isDisplayed()));
    }

    // test measure tool end-to-end
    @Test
    public void testMeasurementTool() {
        loginTestUser();
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.nav_measure)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        int[] screenSize = getScreenSize();
        int screenWidth = screenSize[0];
        int screenHeight = screenSize[1];
        int safeAreaBottom = screenHeight - 200; // ** navbar area ignored

        int x1 = new Random().nextInt(screenWidth - 200) + 100;
        int y1 = new Random().nextInt(safeAreaBottom - 200) + 100;
        int x2 = new Random().nextInt(screenWidth - 200) + 100;
        int y2 = new Random().nextInt(safeAreaBottom - 200) + 100;


        onView(isRoot()).perform(clickXY(x1, y1));
        SystemClock.sleep(3000);
        onView(isRoot()).perform(clickXY(x2, y2));
        onView(withId(R.id.clear_button)).check(matches(isDisplayed()));

    }

    // test logout functionality
    @Test
    public void testLogout() {
        loginTestUser();
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.nav_log_out)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.sign_in)).check(matches(isDisplayed()));
    }

    // test catalogue filters
    @Test
    public void testMultipleFilterCombination() {
        loginTestUser();
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.nav_catalogue)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.filterIcon)).perform(click());

        // Apply multiple filters
        onView(withId(R.id.typeFilterSpinner)).perform(click());
        onData(is("Chair")).perform(click());

        onView(withId(R.id.colourFilterSpinner)).perform(click());
        onData(is("Grey")).perform(click());

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.swipe(500, 1500, 500, 500, 20);

        onView(withId(R.id.priceFilterEditText)).perform(typeText("200"), closeSoftKeyboard());
        onView(withId(R.id.applyFilterButton)).perform(click());

        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.recyclerView)).check(matches(hasMinimumChildCount(1)));
    }


}
