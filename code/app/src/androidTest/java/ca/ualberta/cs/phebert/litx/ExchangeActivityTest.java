package ca.ualberta.cs.phebert.litx;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class ExchangeActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void forBorrow() {
        onView(withId(R.id.exchange_home)).perform(click());
        onView(withId(R.id.pick_borrower)).perform(click());
        onView(withId(R.id.recieve_book)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.pick_borrower)).perform(click());
        onView(withId(R.id.hand_over_book)).perform(click());
        Espresso.pressBack();
    }

    @Test
    public void forOwner() {
        onView(withId(R.id.exchange_home)).perform(click());
        onView(withId(R.id.pick_owner)).perform(click());
        onView(withId(R.id.recieve_book)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.pick_owner)).perform(click());
        onView(withId(R.id.hand_over_book)).perform(click());
        Espresso.pressBack();
    }

    @Test
    public void clickBook() {
        onView(withId(R.id.accept_home)).perform(click());
        onView(withId(R.id.status_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }



}