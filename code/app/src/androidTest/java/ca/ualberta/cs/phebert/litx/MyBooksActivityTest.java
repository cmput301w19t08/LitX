package ca.ualberta.cs.phebert.litx;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;



public class MyBooksActivityTest {

    private String stringTitle = "I am robo";
    private String stringAuthor = "I am sentient";
    private String stringAuthor2 = "The uprising has begun";
    private String stringAuthor3 = "hahahahahahahahahahahaha";
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
        onView(withId(R.id.editAuthor)).perform(typeText(stringAuthor2), closeSoftKeyboard());
        onView(withId(R.id.editAuthor)).perform(typeText(stringAuthor3), closeSoftKeyboard());
        onView(withId(R.id.editISBN)).perform(typeText(stringISBN), closeSoftKeyboard());
        Espresso.pressBack();
    }

}