package ca.ualberta.cs.phebert.litx;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;



public class MyBooksActivityTest {

    private String stringTitle = "I am espresso";
    private String stringAuthor = "Story of the espresso";
    private String stringISBN = "1111111111";

    @Rule
    public ActivityTestRule<MyBooksActivity> activityRule = new ActivityTestRule<>(MyBooksActivity.class);

    @Test
    public void spinnerTest() {
        onView(withId(R.id.spinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.spinner)).perform(click());
        onData(anything()).atPosition(2).perform(click());
        onView(withId(R.id.spinner)).perform(click());
        onData(anything()).atPosition(3).perform(click());
        onView(withId(R.id.spinner)).perform(click());
        onData(anything()).atPosition(4).perform(click());
    }
    @Test
    public void AddBookTest() {
        onView(withId(R.id.btnAddNew)).perform(click());
        onView(withId(R.id.editTitle)).perform(typeText(stringTitle), closeSoftKeyboard());
        onView(withId(R.id.editAuthor)).perform(typeText(stringAuthor), closeSoftKeyboard());
        onView(withId(R.id.editISBN)).perform(typeText(stringISBN), closeSoftKeyboard());
        onView(withId(R.id.btnISBN)).perform(click());
        Espresso.pressBack();
    }

}