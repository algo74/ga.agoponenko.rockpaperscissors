package ga.agoponenko.rockpaperscissors;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import ga.agoponenko.rockpaperscissors.gamemodel.GameModel;

public class CoverInfoActivity extends AppCompatActivity {
    GameModel mGameModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameModel = GameModel.getInstance(this);
        setContentView(R.layout.activity_cover_info);
        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(mGameModel.getBitmap());
    }
}
