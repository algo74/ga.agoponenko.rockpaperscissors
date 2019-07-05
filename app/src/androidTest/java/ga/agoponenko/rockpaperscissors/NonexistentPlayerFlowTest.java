package ga.agoponenko.rockpaperscissors;

import android.app.Activity;
import android.app.Instrumentation;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ga.agoponenko.rockpaperscissors.db.DbHelper;
import ga.agoponenko.rockpaperscissors.gamemodel.GameModel;
import ga.agoponenko.rockpaperscissors.gamemodel.Player;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class NonexistentPlayerFlowTest {

    @Rule
    public final IntentsTestRule<EmptyFragmentActivity> main
          =new IntentsTestRule(EmptyFragmentActivity.class, true, false);

    private AndrSQLiteGameStore mStore;
    private Fragment mFragment;
    private GameModel mGameModel;


    @Before
    public void setUp() {
        SQLiteDatabase db = SQLiteDatabase.create(null);
        DbHelper.initDatabase(db);
        mStore = AndrSQLiteGameStore.getInstance(InstrumentationRegistry.getTargetContext());
        mStore.injectDb(db);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
              () -> mGameModel = GameModel.getInstance(InstrumentationRegistry.getTargetContext()));
    }

    private void launchActivity() {
        main.launchActivity(null);
        //mFragment =
        //      main.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    /*
     * When the game starts
     *
     * If stored current player is null
     *
     * "Select player" activity is shown
     */
    @Test
    public void gameStartsNullPlayer() {
        mStore.setPrefPlayer(null);
        launchActivity();

        injectGameFragment();

        intended(hasComponent(PlayersActivity.class.getName()));
    }

    /*
     * When the game starts and current player is null
     *
     * After "Select player" activity is cancelled
     *
     * The game quits
     */
    @Test
    public void gameQuitsNoPlayerSelected() {

        mStore.setPrefPlayer(null);
        launchActivity();

        intending(hasComponent(PlayersActivity.class.getName())).respondWith(new Instrumentation.ActivityResult(
              Activity.RESULT_CANCELED, null));

        injectGameFragment();

        assertActivityFinished();

    }


    /*
     * When the game starts
     *
     * If stored current player doesn't exist
     *
     * "Select player" activity is shown
     */
    @Test
    public void gameStartsNonexistentPlayer() {
        mGameModel.newPlayer();
        Player player = mGameModel.newPlayer();
        mGameModel.choosePlayer(player.getId());
        mStore.deletePlayer(player.getId());

        launchActivity();

        injectGameFragment();

        intended(hasComponent(PlayersActivity.class.getName()));
    }

    /*
     * When the game starts and current player doesn't exist
     *
     * After "Select player" activity is cancelled
     *
     * The game quits
     */
    @Test
    public void gameQuitsNonexistentPlayerNotChanged() {
        mGameModel.newPlayer();
        Player player = mGameModel.newPlayer();
        mGameModel.choosePlayer(player.getId());
        mStore.deletePlayer(player.getId());

        launchActivity();

        intending(hasComponent(PlayersActivity.class.getName())).respondWith(new Instrumentation.ActivityResult(
              Activity.RESULT_CANCELED, null));

        injectGameFragment();

        assertActivityFinished();
    }

    /*
     * When the users exits "Select player" activity
     *
     * If current player is null or doesn't exist
     *
     * The game exits
     */
    @Test
    public void gameQuitsCurrentPlayerDeleted() {
        mGameModel.newPlayer();
        Player player = mGameModel.newPlayer();
        mGameModel.choosePlayer(player.getId());


        launchActivity();
        injectGameFragment();

        intending(hasComponent(PlayersActivity.class.getName())).respondWith(new Instrumentation.ActivityResult(
              Activity.RESULT_CANCELED, null));

        mStore.deletePlayer(player.getId());
        onView(withId(R.id.choose_player)).perform(click());

        assertActivityFinished();
    }

    @Test
    public void gameContinuesPlayerNotSelected() {
        mGameModel.newPlayer();
        Player player = mGameModel.newPlayer();
        mGameModel.choosePlayer(player.getId());

        launchActivity();
        injectGameFragment();

        intending(hasComponent(PlayersActivity.class.getName())).respondWith(new Instrumentation.ActivityResult(
              Activity.RESULT_CANCELED, null));

        onView(withId(R.id.choose_player)).perform(click());

        onView(withId(R.id.playerNameView)).check(matches(withText(player.getName())));
    }


    private void injectGameFragment() {
        onView(withId(R.id.fragment_container)).check(matches(withId(R.id.fragment_container)));
        main.getActivity()
            .getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new GameFragment())
            .commit();
    }

    private void assertActivityFinished() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue("Activity must finish",
                          main.getActivity() == null || main.getActivity().isFinishing());
    }
}
