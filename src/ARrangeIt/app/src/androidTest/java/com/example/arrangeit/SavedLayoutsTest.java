package com.example.arrangeit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.arrangeit.Helpers.ensureTestUserLoggedIn;
import static com.example.arrangeit.Helpers.testPlaceFurniture;
import static com.example.arrangeit.Helpers.waitFor;
import static org.hamcrest.Matchers.allOf;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class SavedLayoutsTest {
    @Rule
    public ActivityScenarioRule<ARCorePage> activityRule =
            new ActivityScenarioRule<>(ARCorePage.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    private static final String TEST_LAYOUT_NAME = "Test Living Room";

    private UiDevice device;

    @Before
    public void setUp() throws UiObjectNotFoundException {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        ensureTestUserLoggedIn();
        testPlaceFurniture();
    }


    @Test
    public void testCompleteFlowFromPlacementToSavedLayouts() {
        onView(withId(R.id.save_button)).perform(click());
        onView(withHint("Enter layout name")).perform(typeText(TEST_LAYOUT_NAME));
        onView(withText("Save")).perform(click());
        onView(isRoot()).perform(waitFor(10000));
        onView(withId(R.id.nav_screenshots)).perform(click());
        verifySavedLayoutDisplay();
    }

    private void verifySavedLayoutDisplay() {
        onView(withId(R.id.screenshots_grid))
                .check(matches(hasMinimumChildCount(1)));

        onView(allOf(withText(TEST_LAYOUT_NAME), isDisplayed()))
                .check(matches(isDisplayed()));
    }
}