package ca.ualberta.cs.phebert.litx;

import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class ProfileActivityTest {

    private String stringToBeTyped;

    @Rule
    public ActivityTestRule<ProfileActivity> activityRule = new ActivityTestRule<>(ProfileActivity.class);

    @Before
    public void initValidString() {
        stringToBeTyped = "Espresso";
    }

    @Test
    public void changeText_sameActivity() {
        onView(withId(R.id.EditButton)).perform(click());
        onView(withId(R.id.emailEdit)).perform(typeText(stringToBeTyped), closeSoftKeyboard());
        onView(withId(R.id.phoneEdit)).perform(typeText(stringToBeTyped), closeSoftKeyboard());
        onView(withId(R.id.UserEdit)).perform(typeText(stringToBeTyped), closeSoftKeyboard());
    }

}