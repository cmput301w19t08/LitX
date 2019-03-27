package ca.ualberta.cs.phebert.litx;

import org.junit.Rule;
import org.junit.Test;

//import androidx.test.espresso.Espresso;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

/*import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;*/
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.anything;
//import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;


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
    public void changeText_sameActivity() {
        onView(withId(R.id.input_search)).perform(typeText(bookToFind), closeSoftKeyboard());
        onView(withId(R.id.find_search)).perform(click());
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      //  onData(anything()).atPosition(0).perform(click());
     //   onData(anything()).atPosition(0).perform(scrollTo()).perform(click());
       // onView(withId(R.id.search_results)).perform(scrollTo()).perform(click());


        //onView(withId(R.id.search_results)).perform(
          //      RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

}