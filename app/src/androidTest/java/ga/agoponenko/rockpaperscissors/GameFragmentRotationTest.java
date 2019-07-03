package ga.agoponenko.rockpaperscissors;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ga.agoponenko.rockpaperscissors.db.DbHelper;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withAlpha;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.google.common.truth.Truth.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.not;

public class GameFragmentRotationTest {
    private Fragment mFragment;

    @Rule
    public final ActivityTestRule<GameActivity> main
          =new ActivityTestRule(GameActivity.class, true,false);

    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        DbHelper.initDatabase(db);
        AndrSQLiteGameStore store = AndrSQLiteGameStore.getInstance(main.getActivity());
        store.injectDb(db);
        main.launchActivity(null);
        mFragment =
              main.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Test
    public void initialStage() {
        rotateAndWait();
        Button button = mFragment.getView().findViewById(R.id.bNewTurn);
        assertThat(button).isNotNull();
        assertThat(button.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void lastStage() {
        onView(withId(R.id.engineResultView)).check(new EmptyImageViewAssertion());
        onView(withId(R.id.playerResultView)).check(new EmptyImageViewAssertion());
        onView(withId(R.id.playerMoveView)).check(new EmptyImageViewAssertion());
        onView(withId(R.id.bNewTurn)).perform(click());
        onView(withId(R.id.bNewTurn)).check(matches(isDisplayed()));
        onView(withId(R.id.engineResultView)).check(new VisibleImageViewAssertion());
        onView(withId(R.id.playerResultView)).check(new VisibleImageViewAssertion());
        onView(withId(R.id.playerMoveView)).check(new VisibleImageViewAssertion());
        rotate();
        onView(withId(R.id.bNewTurn)).check(matches(isDisplayed()));
        onView(withId(R.id.engineResultView)).check(new VisibleImageViewAssertion());
        onView(withId(R.id.playerResultView)).check(new VisibleImageViewAssertion());
        onView(withId(R.id.playerMoveView)).check(new VisibleImageViewAssertion());
    }

    private void rotateAndWait() {
        Activity oldActivity = main.getActivity();
        rotate();
        await().until(() -> main.getActivity() != oldActivity);
        await().until(() -> main.getActivity() != null);
        await().until(() -> main.getActivity().getSupportFragmentManager() != null);
        await().until(() -> main.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null);
        mFragment =
              main.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }


    private void rotate() {
        int target=
              (getOrientation()== Configuration.ORIENTATION_LANDSCAPE ?
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        main.getActivity().setRequestedOrientation(target);
    }

    private int getOrientation() {
        return(InstrumentationRegistry
              .getTargetContext()
              .getResources()
              .getConfiguration()
              .orientation);
    }

    static class EmptyImageViewAssertion implements ViewAssertion {
        @Override
        public void check(View view,
                          NoMatchingViewException noViewFoundException) {
            Assert.assertTrue(view instanceof ImageView);
            ImageView imageView = (ImageView) view;
            Assert.assertEquals(imageView.getDrawable(), null);
        }
    }

    static class VisibleImageViewAssertion implements ViewAssertion {
        @Override
        public void check(View view,
                          NoMatchingViewException noViewFoundException) {
            Assert.assertTrue(view instanceof ImageView);
            ImageView imageView = (ImageView) view;
            Assert.assertNotEquals(imageView.getDrawable(), null);
            Assert.assertNotEquals(imageView.getAlpha(), 0.0f);
            Assert.assertNotEquals(imageView.getImageAlpha(), 0);
        }
    }
}
