package com.example.arrangeit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

import static com.example.arrangeit.Helpers.clickXY;
import static com.example.arrangeit.Helpers.getScreenSize;
import static com.example.arrangeit.Helpers.moveTo;
import static com.example.arrangeit.Helpers.touchDown;
import static com.example.arrangeit.Helpers.touchUp;
import static com.example.arrangeit.Helpers.waitFor;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import com.google.ar.sceneform.math.Quaternion;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import java.util.Random;

public class FurniturePlacementTest {
    @Rule
    public ActivityScenarioRule<ARCorePage> activityRule =
            new ActivityScenarioRule<>(ARCorePage.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.CAMERA);


    @Test
    public void testPlaceFurniture() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        onView(withId(R.id.nav_catalogue)).perform(click());
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.fragment_container)).check(matches(isDisplayed()));
        String[] furnitureItems = {
                "Modern Arm Chair",
                "Aidian Corner Storage Sofa Bed",
                "Alana Bedside Table"
        };

        int randomIndex = new Random().nextInt(furnitureItems.length);
        String selectedItem = furnitureItems[randomIndex];
        device.findObject(new UiSelector()
                        .className("android.widget.TextView")
                        .textContains(selectedItem))
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
        onView(isRoot()).perform(waitFor(2000));
        onView(isRoot()).perform(clickXY(centerX, centerY));
        onView(isRoot()).perform(waitFor(2000));
        onView(withId(R.id.model_counter))
                .check(matches(allOf(
                        isDisplayed(),
                        withText("Models: 1")
                )));
    }

    @Test
    public void testFurnitureMovementControls() throws Exception {
        onView(isRoot()).perform(waitFor(2000));
        testPlaceFurniture();
        int[] screenSize = getScreenSize();
        int centerX = screenSize[0]/2;
        int centerY = screenSize[1]/2;

        // test move function
        onView(withId(R.id.move_button)).perform(click());
        onView(isRoot()).perform(waitFor(500));

        int moveSteps = 5;
        int moveDistance = 200;
        for (int i = 0; i <= moveSteps; i = i + 1) {
            int currentX = centerX + (i * moveDistance / moveSteps);
            if (i == 0) {
                onView(isRoot()).perform(touchDown(currentX, centerY));
            } else {
                onView(isRoot()).perform(moveTo(currentX, centerY));
            }
            onView(isRoot()).perform(waitFor(1000));
        }
        onView(isRoot()).perform(touchUp());
        onView(isRoot()).perform(waitFor(2000));

        onView(isRoot()).perform(clickXY(centerX, centerY));
        onView(isRoot()).perform(waitFor(3000));
        onView(withId(R.id.furniture_controls)).check(matches(isDisplayed()));

//        // test rotate function
//        onView(withId(R.id.rotate_button)).perform(click());
//        onView(isRoot()).perform(waitFor(1000));
//        final float[] initialRotation = new float[4];
//        activityRule.getScenario().onActivity(activity -> {
//            if (activity.currentFurnitureNode != null) {
//                Quaternion quaternion = activity.currentFurnitureNode.getWorldRotation();
//                initialRotation[0] = quaternion.x;
//                initialRotation[1] = quaternion.y;
//                initialRotation[2] = quaternion.z;
//                initialRotation[3] = quaternion.w;
//            }
//        });
//
//        int radius = 200;
//        int rotationSteps = 36;
//        for (int i = 0; i <= rotationSteps; i++) {
//            double angle = (i * 360.0) / rotationSteps;
//            double radians = Math.toRadians(angle);
//            int x = (int) (centerX + radius * Math.cos(radians));
//            int y = (int) (centerY + radius * Math.sin(radians));
//
//            if (i == 0) {
//                onView(isRoot()).perform(touchDown(x, y));
//            } else {
//                onView(isRoot()).perform(moveTo(x, y));
//            }
//            onView(isRoot()).perform(waitFor(50));
//        }
//        onView(isRoot()).perform(touchUp());
//        onView(isRoot()).perform(waitFor(3000));
//
//        // compare initial rotation to new rotation
//        activityRule.getScenario().onActivity(activity -> {
//            if (activity.currentFurnitureNode != null) {
//                Quaternion newRotation = activity.currentFurnitureNode.getWorldRotation();
//                float epsilon = 0.0001f;
//                boolean rotationChanged = Math.abs(initialRotation[0] - newRotation.x) > epsilon ||
//                        Math.abs(initialRotation[1] - newRotation.y) > epsilon ||
//                        Math.abs(initialRotation[2] - newRotation.z) > epsilon ||
//                        Math.abs(initialRotation[3] - newRotation.w) > epsilon;
//
//                assertTrue("Rotation should change", rotationChanged);
//            }
//        });

        // test rotate function
        onView(withId(R.id.rotate_button)).perform(click());
        onView(isRoot()).perform(waitFor(1000));

        // Get initial rotation
        final float[] initialRotation = new float[4];
        activityRule.getScenario().onActivity(activity -> {
            if (activity.currentFurnitureNode != null) {
                Quaternion quaternion = activity.currentFurnitureNode.getWorldRotation();
                initialRotation[0] = quaternion.x;
                initialRotation[1] = quaternion.y;
                initialRotation[2] = quaternion.z;
                initialRotation[3] = quaternion.w;
            }
        });

        // Simulate touch drag to rotate
        int startX = centerX;
        int startY = centerY;
        int endX = centerX + 200; // Move 200 pixels to the right

        // Perform touch down
        onView(isRoot()).perform(touchDown(startX, startY));
        onView(isRoot()).perform(waitFor(50));

        // Perform drag
        onView(isRoot()).perform(moveTo(endX, startY));
        onView(isRoot()).perform(waitFor(50));

        // Perform touch up
        onView(isRoot()).perform(touchUp());
        onView(isRoot()).perform(waitFor(1000));

        // Compare initial rotation to new rotation
        activityRule.getScenario().onActivity(activity -> {
            if (activity.currentFurnitureNode != null) {
                Quaternion newRotation = activity.currentFurnitureNode.getWorldRotation();
                float epsilon = 0.0001f;
                boolean rotationChanged = Math.abs(initialRotation[0] - newRotation.x) > epsilon ||
                        Math.abs(initialRotation[1] - newRotation.y) > epsilon ||
                        Math.abs(initialRotation[2] - newRotation.z) > epsilon ||
                        Math.abs(initialRotation[3] - newRotation.w) > epsilon;

                assertTrue("Rotation should change after touch drag", rotationChanged);
            }
        });

        // test delete
        onView(withId(R.id.delete_button)).perform(click());
        onView(isRoot()).perform(waitFor(1000));
        onView(withId(R.id.model_counter)).check(matches(not(isDisplayed())));
        onView(withId(R.id.furniture_controls)).check(matches(not(isDisplayed())));
    }

}
