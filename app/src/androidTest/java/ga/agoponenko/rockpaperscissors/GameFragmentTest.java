package ga.agoponenko.rockpaperscissors;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ga.agoponenko.rockpaperscissors.db.DbHelper;
import ga.agoponenko.rockpaperscissors.gamemodel.GameModel;
import ga.agoponenko.rockpaperscissors.gamemodel.GameStore;
import ga.agoponenko.rockpaperscissors.gamemodel.Player;

import static android.support.test.espresso.matcher.ViewMatchers.Visibility;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static com.google.common.truth.Truth.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;


@RunWith(AndroidJUnit4.class)
public class GameFragmentTest {

    private Fragment mFragment;

    @Rule
    public final ActivityTestRule<GameActivity> main
          =new ActivityTestRule(GameActivity.class, true,false);
    private GameModel mGameModel;

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        DbHelper.initDatabase(db);
        AndrSQLiteGameStore store =
              AndrSQLiteGameStore.getInstance(InstrumentationRegistry.getInstrumentation().getTargetContext());
        store.injectDb(db);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
              () -> mGameModel = GameModel.getInstance(InstrumentationRegistry.getTargetContext()));
        Player player = mGameModel.newPlayer();
        mGameModel.choosePlayer(player.getId());

        main.launchActivity(null);
        mFragment =
              main.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    //@AfterClass
    //public static void init() {
    //    InstrumentationRegistry.getInstrumentation().getTargetContext().deleteDatabase(DbHelper.DB_NAME);
    //}

    @Test
    public void fragmentIsLoaded() {
        Assert.assertNotNull("Fragment exist", mFragment);
    }

    @Test
    public void buttonNewTurnIsVisible() {
        Button button = mFragment.getView().findViewById(R.id.bNewTurn);
        assertThat(button).isNotNull();
        assertThat(button.getVisibility()).isEqualTo(View.VISIBLE);
    }

    @Test
    public void buttonNewTurnDisappears() {
        Button button = mFragment.getView().findViewById(R.id.bNewTurn);
        assertThat(button).isNotNull();
        assertThat(button.getVisibility()).isEqualTo(View.VISIBLE);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> button.performClick());
        assertThat(button.getVisibility()).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void gameFlow() {
        Button button = mFragment.getView().findViewById(R.id.bNewTurn);
        View bRock = mFragment.getView().findViewById(R.id.bRock);
        assertThat(bRock).isNotNull();
        Assert.assertThat(bRock, withEffectiveVisibility(Visibility.INVISIBLE));
        View bPaper = mFragment.getView().findViewById(R.id.bPaper);
        assertThat(bPaper).isNotNull();
        Assert.assertThat(bPaper, withEffectiveVisibility(Visibility.INVISIBLE));
        View bScissors = mFragment.getView().findViewById(R.id.bScissors);
        Assert.assertThat(bScissors, is(notNullValue(View.class)));
        Assert.assertThat(bScissors, withEffectiveVisibility(Visibility.INVISIBLE));

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> button.performClick());

        await().until(() -> withEffectiveVisibility(Visibility.VISIBLE).matches(bRock));
        Assert.assertThat(bRock, withEffectiveVisibility(Visibility.VISIBLE));
        Assert.assertThat(bPaper, withEffectiveVisibility(Visibility.VISIBLE));
        Assert.assertThat(bScissors, withEffectiveVisibility(Visibility.VISIBLE));

        TextView vCount = mFragment.getView().findViewById(R.id.textViewCountDown);
        Assert.assertThat(vCount, withEffectiveVisibility(Visibility.VISIBLE));

        // now let's check if buttons disappear
        await().until(() -> vCount.getText().equals("0"));
        Assert.assertThat(bRock, withEffectiveVisibility(Visibility.INVISIBLE));
        Assert.assertThat(bPaper, withEffectiveVisibility(Visibility.INVISIBLE));
        Assert.assertThat(bScissors, withEffectiveVisibility(Visibility.INVISIBLE));
        // results shown
        Assert.assertThat(mFragment.getView().findViewById(R.id.engineResultView), isDisplayed());
        Assert.assertThat(mFragment.getView().findViewById(R.id.playerResultView), isDisplayed());
        Assert.assertThat(mFragment.getView().findViewById(R.id.playerMoveView), isDisplayed());
        // new turn button is visible
        await().until(() -> withEffectiveVisibility(Visibility.VISIBLE).matches(button));
        Assert.assertThat(button, isDisplayed());

        // another turn

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> button.performClick());

        await().until(() -> withEffectiveVisibility(Visibility.VISIBLE).matches(bRock));
        Assert.assertThat(bRock, withEffectiveVisibility(Visibility.VISIBLE));
        Assert.assertThat(bPaper, withEffectiveVisibility(Visibility.VISIBLE));
        Assert.assertThat(bScissors, withEffectiveVisibility(Visibility.VISIBLE));

        // use Rock this time
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> bRock.performClick());

        await().until(() -> withEffectiveVisibility(Visibility.INVISIBLE).matches(bRock));

        Assert.assertThat(bRock, withEffectiveVisibility(Visibility.INVISIBLE));
        Assert.assertThat(bPaper, withEffectiveVisibility(Visibility.INVISIBLE));
        Assert.assertThat(bScissors, withEffectiveVisibility(Visibility.INVISIBLE));
        // results shown
        Assert.assertThat(mFragment.getView().findViewById(R.id.engineResultView), isDisplayed());
        Assert.assertThat(mFragment.getView().findViewById(R.id.playerResultView), isDisplayed());
        Assert.assertThat(mFragment.getView().findViewById(R.id.playerMoveView), isDisplayed());

        // new turn button is visible
        await().until(() -> withEffectiveVisibility(Visibility.VISIBLE).matches(button));
        Assert.assertThat(button, isDisplayed());

        // the cover is open
        View info = mFragment.getView().findViewById(R.id.bCoverInfo);
        await().until(() -> withEffectiveVisibility(Visibility.VISIBLE).matches(info));
        View cover = mFragment.getView().findViewById(R.id.cubeViewCover);
        View cube1 = mFragment.getView().findViewById(R.id.cubeView1);
        Assert.assertThat(cover, isDisplayed());
        Assert.assertThat(cover, isCompletelyDisplayed());
        Assert.assertThat(cube1, isDisplayed());
        assertThat(cube1.getX()).isNotEqualTo(cover.getX());
        assertThat(cube1.getY()).isNotEqualTo(cover.getY());
    }
}
