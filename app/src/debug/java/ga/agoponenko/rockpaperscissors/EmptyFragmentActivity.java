package ga.agoponenko.rockpaperscissors;

import android.support.v4.app.Fragment;

public class EmptyFragmentActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new Fragment();
    }
}
