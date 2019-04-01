package ca.ualberta.cs.phebert.litx;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class PhotoTests {

    @Rule
    public ActivityTestRule<SearchActivity> activityRule = new ActivityTestRule<>(SearchActivity.class);

    @Test
    public void clickBook() {
        onView(withId(R.id.search_results))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void bookViewTests() {
        clickBook();
        onView(withId(R.id.bookImage)).perform(click());
        onView(withId(R.id.donePhotoButton)).perform(click());

    }

}