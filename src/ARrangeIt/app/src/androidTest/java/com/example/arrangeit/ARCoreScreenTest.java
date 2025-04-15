package com.example.arrangeit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.arrangeit.FurnitureCatalogueFragmentTest.waitFor;
import static org.hamcrest.CoreMatchers.not;
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
import org.hamcrest.Matcher;
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
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));
    }


    @Test
    public void testFurnitureControlsVisibility() {
        activityRule.getScenario().onActivity(activity -> {
            activity.placedModelsCount = 1;
            activity.showManipulationButtons();
        });
        onView(isRoot()).perform(waitFor(1000));
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
    public void testSignoutButton() {
        onView(withId(R.id.nav_log_out)).perform(click());
        onView(withText("Welcome back")).check(matches(isDisplayed()));

    }

    // MEASURE TOOL COMPONENT UI TESTS
    @Test
    public void testMeasurementControlsInitialState() {
        onView(withId(R.id.clear_button)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testMeasurementCreation() {
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

    @Test
    public void testMeasurementClear() {
        testMeasurementCreation();
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.clear_button)).perform(click());
        onView(withId(R.id.clear_button)).check(matches(not(isDisplayed())));
    }

    // HELPER METHODS
    private int[] getScreenSize() {
        int[] size = new int[2];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager)
                    InstrumentationRegistry.getInstrumentation().getContext()
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
                return "Perform click at coordinates (" + x + ", " + y + ")";
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
}
