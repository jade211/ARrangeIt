package com.example.arrangeit;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import android.view.View;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import com.bumptech.glide.Glide;
import org.hamcrest.Matcher;
import org.junit.After;
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

    @Rule
    public GlideTestRule glideTestRule = new GlideTestRule();

    private IdlingResource idlingResource;

    @Before
    public void setUp() throws UiObjectNotFoundException {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            Glide.get(ApplicationProvider.getApplicationContext());
        });

        idlingResource = new GlideIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.findObject(new UiSelector()
                        .resourceId("com.example.arrangeit:id/nav_catalogue"))
                .click();
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));
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

        onView(isRoot()).perform(waitFor(2000));
    }

    @Test
    public void testCatalogueContents() {
        onView(withText("Furniture Catalogue")).check(matches(isDisplayed()));
        onView(withId(R.id.closeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.searchBar)).check(matches(isDisplayed()));
        onView(withId(R.id.filterIcon)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchFunctionality() {
        try {
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.searchBar))
                    .perform(typeText("chair"), pressImeActionButton());
            closeSoftKeyboard();
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("Search test failed: " + e.getMessage());
        }
    }

    @Test
    public void testSortingPriceFunctionality() {
        try {
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.filterIcon)).perform(click());

            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.swipe(500, 1500, 500, 500, 20);

            onView(withId(R.id.sortByPriceSpinner))
                    .check(matches(isDisplayed()))
                    .perform(click());

            onData(allOf(is(instanceOf(String.class)), is("Price: Low to High")))
                    .perform(click());

            onView(withId(R.id.applyFilterButton)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("Sorting Price test failed: " + e.getMessage());
        }
    }

    @Test
    public void testSortingColourFunctionality() {
        try {
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.filterIcon)).perform(click());

            onView(withId(R.id.colourFilterSpinner))
                    .check(matches(isDisplayed()))
                    .perform(click());

            onData(allOf(is(instanceOf(String.class)), is("Blue")))
                    .perform(click());


            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.swipe(500, 1500, 500, 500, 20);

            onView(withId(R.id.applyFilterButton)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("Sorting Colour test failed: " + e.getMessage());
        }
    }


    @Test
    public void testSortingTypeFunctionality() {
        try {
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.filterIcon)).perform(click());

            onView(withId(R.id.typeFilterSpinner))
                    .check(matches(isDisplayed()))
                    .perform(click());

            onData(allOf(is(instanceOf(String.class)), is("Chair")))
                    .perform(click());

            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.swipe(500, 1500, 500, 500, 20);

            onView(withId(R.id.applyFilterButton)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("Sorting Type test failed: " + e.getMessage());
        }
    }

    @Test
    public void testSortingMaxPriceFunctionality() {
        try {
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.filterIcon)).perform(click());

            onView(withId(R.id.priceFilterEditText))
                    .perform(typeText("200"));
            closeSoftKeyboard();

            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.swipe(500, 1500, 500, 500, 20);


            onView(withId(R.id.applyFilterButton)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("Sorting Max Price test failed: " + e.getMessage());
        }
    }

    @Test
    public void testSortingMaxDepthFunctionality() {
        try {
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.filterIcon)).perform(click());

            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.swipe(500, 1500, 500, 500, 20);

            onView(withId(R.id.depthFilterEditText))
                    .perform(typeText("79"));
            closeSoftKeyboard();

            onView(withId(R.id.applyFilterButton)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("Sorting Max Depth test failed: " + e.getMessage());
        }
    }

    @Test
    public void testSortingMaxHeightFunctionality() {
        try {
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.filterIcon)).perform(click());

            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.swipe(500, 1500, 500, 500, 20);

            onView(withId(R.id.heightFilterEditText))
                    .perform(typeText("90"));
            closeSoftKeyboard();

            onView(withId(R.id.applyFilterButton)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("Sorting Max Height test failed: " + e.getMessage());
        }
    }


    @Test
    public void testSortingMaxWidthFunctionality() {
        try {
            onView(isRoot()).perform(waitFor(1000));
            onView(withId(R.id.filterIcon)).perform(click());

            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            device.swipe(500, 1500, 500, 500, 20);

            onView(withId(R.id.widthFilterEditText))
                    .perform(typeText("200"));
            closeSoftKeyboard();

            onView(withId(R.id.applyFilterButton)).perform(click());
            onView(isRoot()).perform(waitFor(2000));
            onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));
        } catch (Exception e) {
            fail("Sorting Max Width test failed: " + e.getMessage());
        }
    }


    @Test
    public void testCloseButtonFunctionality() {
        try {
            onView(withId(R.id.closeButton)).perform(click());
            onView(withId(R.id.fragment_container)).check(matches(not(isDisplayed())));
        } catch (Exception e) {
            fail("Close button test failed: " + e.getMessage());
        }
    }

    // FURNITURE DETAILS FRAGMENT UI TEST
    @Test
    public void testFurnitureDetailNavigation() throws UiObjectNotFoundException {
        onView(isRoot()).perform(waitFor(2000));
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.findObject(new UiSelector()
                        .className("android.widget.TextView")
                        .textContains("Modern Arm Chair"))
                .click();
        onView(withId(R.id.itemName)).check(matches(isDisplayed()));
        onView(withId(R.id.itemPrice)).check(matches(isDisplayed()));
    }

    // HELPERS
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
}
