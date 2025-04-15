package com.example.arrangeit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.arrangeit.FurnitureCatalogueFragmentTest.waitFor;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.bumptech.glide.Glide;
import com.example.arrangeit.helpers.MarkerLineView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ARCoreScreenTest {

    @Rule
    public ActivityScenarioRule<ARCorePage> activityRule =
            new ActivityScenarioRule<>(ARCorePage.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Rule
    public GlideTestRule glideTestRule = new GlideTestRule();

    private IdlingResource idlingResource;

    @Before
    public void setUp() {
        idlingResource = new GlideIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @After
    public void tearDown() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                Glide.get(ApplicationProvider.getApplicationContext()).clearMemory()
        );
        new Thread(() ->
                Glide.get(ApplicationProvider.getApplicationContext()).clearDiskCache()
        ).start();
        onView(isRoot()).perform(waitFor(1000));
    }

    @Test
    public void testNavbarVisibility() {
        onView(withId(R.id.bottom_nav_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_catalogue)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_measure)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_log_out)).check(matches(isDisplayed()));
    }

    @Test
    public void testARSurfaceVisibility() {
        onView(withId(R.id.arFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void testFurnitureControlsInitialState() {
        onView(withId(R.id.furniture_controls)).check(matches(not(isDisplayed())));
        onView(withId(R.id.model_counter)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testCatalogueToggle() {
        onView(withId(R.id.nav_catalogue)).perform(click());
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));
    }


    @Test
    public void testFurnitureControlsVisibility() {
        activityRule.getScenario().onActivity(activity -> {
            activity.placedModelsCount = 1;
            activity.showManipulationButtons();
        });

        onView(withId(R.id.furniture_controls)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_button)).check(matches(isDisplayed()));
        onView(withId(R.id.rotate_button)).check(matches(isDisplayed()));
        onView(withId(R.id.move_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testModelCounterVisibility() {
        activityRule.getScenario().onActivity(activity -> {
            activity.placedModelsCount = 1;
            activity.updateModelCounter();
        });

        onView(withId(R.id.model_counter))
                .check(matches(isDisplayed()))
                .check(matches(withText("Models: 1")));

        onView(withId(R.id.clear_all_models_button))
                .check(matches(isDisplayed()));
        onView(withId(R.id.clear_all_models_button)).perform(click());

        onView(withText("Clear All Models"))
                .check(matches(isDisplayed()));
        onView(withText("Are you sure you want to remove all placed models?"))
                .check(matches(isDisplayed()));

        onView(withText("Clear All"))
                .check(matches(isDisplayed()))
                .perform(click());
        onView(withId(R.id.model_counter)).check(matches(not(isDisplayed())));
        activityRule.getScenario().onActivity(activity -> {
            assertEquals(0, activity.placedModelsCount);
            assertTrue(activity.placedFurnitureNodes.isEmpty());
        });
    }

    @Test
    public void testLogoutButton() {
        onView(withId(R.id.nav_log_out)).perform(click());
        onView(withText("Welcome back")).check(matches(isDisplayed()));

    }

}
