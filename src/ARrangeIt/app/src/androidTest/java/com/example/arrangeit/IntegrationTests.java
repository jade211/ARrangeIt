package com.example.arrangeit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.arrangeit.Helpers.ensureTestUserLoggedIn;
import static com.example.arrangeit.Helpers.waitFor;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
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

@RunWith(AndroidJUnit4.class)
public class IntegrationTests {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA
    );

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
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
        }
    }

    @Test
    public void testApplicationContext() {
        assert (InstrumentationRegistry.getInstrumentation()
                .getTargetContext()
                .getPackageName()
                .contains("com.example.arrangeit"));
    }


    @Test
    public void testNavigationToRegisterPage() {
        onView(withId(R.id.sign_up)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        Intents.intended(IntentMatchers.hasComponent(RegisterPage.class.getName()));
        onView(withId(R.id.email)).check(matches(isDisplayed()));
        onView(withId(R.id.password)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationToForgotPasswordDialogue() {
        onView(withId(R.id.forgot_password)).check(matches(isDisplayed()));
        onView(withId(R.id.forgot_password)).perform(click());
        onView(withId(R.id.emailBox)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.buttonReset)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.buttonCancel)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.buttonCancel)).inRoot(isDialog()).perform(click());
    }

    @Test
    public void testNavigationLoginFlow() {
        onView(withId(R.id.email)).perform(typeText(TEST_EMAIL), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText(TEST_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.sign_in)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        Intents.intended(IntentMatchers.hasComponent(ARCorePage.class.getName()));
    }

    @Test
    public void testARCorePageUIElements() {
        ensureTestUserLoggedIn();
        ActivityScenario<ARCorePage> scenario = ActivityScenario.launch(ARCorePage.class);
        try {
            onView(withId(R.id.arFragment)).check(matches(isDisplayed()));
            onView(withId(R.id.nav_catalogue)).check(matches(isDisplayed()));
            onView(withId(R.id.nav_measure)).check(matches(isDisplayed()));
            onView(withId(R.id.nav_log_out)).check(matches(isDisplayed()));
        } finally {
            scenario.close();
        }
    }

    @Test
    public void testNavigationToCatalogue() {
        ensureTestUserLoggedIn();
        ActivityScenario<ARCorePage> scenario = ActivityScenario.launch(ARCorePage.class);
        try {
            onView(withId(R.id.nav_catalogue)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
            onView(withId(R.id.closeButton)).check(matches(isDisplayed()));
            onView(withId(R.id.closeButton)).perform(click());
        } finally {
            scenario.close();
        }
    }

    @Test
    public void testNavigationToFurnitureDetail() throws InterruptedException {
        ensureTestUserLoggedIn();
        ActivityScenario<ARCorePage> scenario = ActivityScenario.launch(ARCorePage.class);
        try {
            onView(withId(R.id.nav_catalogue)).perform(click());
            Thread.sleep(2000);
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.findObject(new UiSelector()
                            .className("android.widget.TextView")
                            .textContains("Modern Arm Chair"))
                    .click();
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.itemName)).check(matches(isDisplayed()));
            device.swipe(500, 1500, 500, 500, 20);
            onView(withId(R.id.place_in_ar_button)).check(matches(isDisplayed()));
            onView(withId(R.id.place_in_ar_button)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.bottom_nav_bar)).check(matches(isDisplayed()));

        } catch (UiObjectNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            scenario.close();
        }
    }

    @Test
    public void testNavigationToSavedLayouts(){
        ensureTestUserLoggedIn();
        ActivityScenario<ARCorePage> scenario = ActivityScenario.launch(ARCorePage.class);
        try {
            onView(withId(R.id.nav_screenshots)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.saved_layouts_title)).check(matches(isDisplayed()));
        } finally {
            scenario.close();
        }
    }

}