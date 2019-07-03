package ga.agoponenko.rockpaperscissors;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GameActivityTest {

    @Rule
    public final ActivityTestRule<GameActivity> main
          =new ActivityTestRule(GameActivity.class, true);

    @Test
    public void testFragmentIsLoaded() {
        Fragment fragment =
              main.getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Assert.assertNotNull("Fragment exist", fragment);
    }
}
