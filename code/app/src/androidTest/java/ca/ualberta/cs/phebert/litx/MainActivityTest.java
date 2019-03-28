package ca.ualberta.cs.phebert.litx;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class MainActivityTest {

    private String stringToBeTyped;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityButtonsTest() {
        onView(withId(R.id.profile_home)).perform(click());
        onView(withId(R.id.EditButton)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.my_books_home)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.accept_home)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.requests_home)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.search_home)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.exchange_home)).perform(click());
        Espresso.pressBack();
    }

}