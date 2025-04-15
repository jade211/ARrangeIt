package com.example.arrangeit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.os.SystemClock;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FurnitureCatalogueFragmentTest {

    @Rule
    public ActivityScenarioRule<ARCorePage> activityRule =
            new ActivityScenarioRule<>(ARCorePage.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Before
    public void openCatalogue() throws UiObjectNotFoundException {
        SystemClock.sleep(2000);
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.findObject(new UiSelector()
                        .resourceId("com.example.arrangeit:id/nav_catalogue"))
                .click();
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));
    }


    @Test
    public void testCatalogueFragmentContents() {
        onView(withText("Furniture Catalogue")).check(matches(isDisplayed()));
        onView(withId(R.id.closeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()));
        onView(withId(R.id.filterIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.filterOptionsLayout)).check(matches(not(isDisplayed())));
        onView(withId(R.id.applyFilterButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.loadingProgressBar)).check(matches(isDisplayed()));
//        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())));
    }

//    @Test
//    public void testNavbarVisibility() {
//        onView(withId(R.id.bottom_nav_bar)).check(matches(isDisplayed()));
//        onView(withId(R.id.nav_catalogue)).check(matches(isDisplayed()));
//    }

//    @Test
//    public void testCloseButtonFunctionality() {
//        onView(withId(R.id.closeButton)).perform(click());
//        onView(withId(R.id.fragment_container)).check(matches(not(isDisplayed())));
//        try {
//            openCatalogue();
//        } catch (UiObjectNotFoundException e) {
//            throw new RuntimeException("Failed to reopen catalogue", e);
//        }
//    }
}