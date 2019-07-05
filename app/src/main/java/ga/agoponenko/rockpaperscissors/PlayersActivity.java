package ga.agoponenko.rockpaperscissors;

import android.support.v4.app.Fragment;

public class PlayersActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PlayersFragment();
    }
}
