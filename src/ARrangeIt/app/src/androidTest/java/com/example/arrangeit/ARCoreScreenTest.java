package com.example.arrangeit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ARCoreScreenTest {

    @Rule
    public ActivityScenarioRule<ARCorePage> activityRule =
            new ActivityScenarioRule<>(ARCorePage.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.CAMERA);
    @Test
    public void testNavbarVisibility() {
        onView(withId(R.id.bottom_nav_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_catalogue)).check(matches(isDisplayed()));
    }
}
