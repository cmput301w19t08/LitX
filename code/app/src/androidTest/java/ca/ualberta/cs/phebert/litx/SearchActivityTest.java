package ca.ualberta.cs.phebert.litx;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class SearchActivityTest {

    private String stringTitle = "I am robo";
    private String stringAuthor = "I am sentient";
    private String stringAuthor2 = "The uprising has begun";
    private String stringAuthor3 = "hahahahahahahahahahahaha";
    private String bookToFind = "Regex";
    @Rule
    public ActivityTestRule<SearchActivity> activityRule = new ActivityTestRule<>(SearchActivity.class);
    private int o ;

    @Test
    public void searchForBook() {
        onView(withId(R.id.input_search)).perform(typeText(bookToFind), closeSoftKeyboard());
        onView(withId(R.id.find_search)).perform(click());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //onData(anything()).atPosition(0).perform(click());
      // onData(anything()).atPosition(0).perform(scrollTo()).perform(click());
       // onView(withId(R.id.search_results)).perform(scrollTo()).perform(click());

        //onView(withId(R.id.search_results)).perform(
               // RecyclerViewActions.scrollToPosition(0)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        //onView(withId(R.id.search_results))
          //      .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }
    @Test
    public void clickBook() {
        onView(withId(R.id.search_results))
              .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        //onData(anything()).atPosition(0).perform(click());
    }
    @Test
    public void bookViewTests() {
        clickBook();
        onView(withId(R.id.ownerViewID)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.bookImage)).perform(click());
        onView(withId(R.id.donePhotoButton)).perform(click());
    }

}