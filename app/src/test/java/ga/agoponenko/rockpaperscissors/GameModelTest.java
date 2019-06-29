package ga.agoponenko.rockpaperscissors;

import android.content.Context;
import android.os.Handler;

import org.junit.Before;
import org.junit.Test;

import ga.agoponenko.rockpaperscissors.gamemodel.GameModel;
import ga.agoponenko.rockpaperscissors.gamemodel.GameStore;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GameModelTest {

    GameModel mModel;
    Context mContext;
    private GameStore mStore;
    private GameModelBackgroundThread mTread;
    private Handler mHandler;


    @Before
    public void setup() {
        mContext = mock(Context.class);
        mHandler = mock(Handler.class);
        mTread = mock(GameModelBackgroundThread.class);
        mStore = mock(GameStore.class);
        mModel = new GameModel(mContext,mStore, mTread, mHandler);
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


}
