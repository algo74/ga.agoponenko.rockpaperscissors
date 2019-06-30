package ga.agoponenko.rockpaperscissors;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import com.google.zxing.WriterException;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import ga.agoponenko.rockpaperscissors.gamemodel.GameModel;
import ga.agoponenko.rockpaperscissors.gamemodel.GameStore;
import ga.agoponenko.rockpaperscissors.gamemodel.HistoryRow;
import ga.agoponenko.rockpaperscissors.gamemodel.Move;
import ga.agoponenko.rockpaperscissors.gamemodel.Player;
import ga.agoponenko.rockpaperscissors.gamemodel.PlayerHistory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameModelTest {

    GameModel mModel;
    Context mContext;
    private GameModelBackgroundThread mTread;
    private Handler mHandler;
    private QREncoder mEncoder;
    private boolean mCallbackCalled;

    private int mInt1;
    private int mInt2;
    private Move mMove;


    @Before
    public void setup() {
        mContext = mock(Context.class);
        mHandler = mock(Handler.class);
        mEncoder = mock(QREncoder.class);
        mTread = new GameModelBackgroundThread_PrepareMoveDouble();
        //when(mHandler.post(any(Runnable.class))).thenAnswer(new Answer<Object>() {
        //    @Override
        //    public Object answer(InvocationOnMock invocation) throws Throwable {
        //        return null;
        //    }
        //});

    }

    @Test
    public void onCreated_playerHistoryIsUpdatedWhenShould() {
        PlayerHistory history = new PlayerHistory("10", Move.PAPER, "010", "WLD");
        Player player = new Player("10");
        player.setName("TestPlayer");
        GameStore store = mock(GameStore.class);
        //when(store.getPlayer("10")).thenReturn(player);
        when(store.getPreferences("currentPlayer")).thenReturn("10");
        when(store.getPlayerHistory("10")).thenReturn(history);
        new GameModel(store, mEncoder, mTread, mHandler);
        verify(store).getPreferences("currentPlayer");
        verify(store).getPlayerHistory("10");
        verify(store).updatePlayerHistory(history);
        Assert.assertEquals("history updated", "010B", history.getUpDownHistory());
    }

    @Test
    public void onCreated_playerHistoryIsNotUpdatedWhenShouldNot() {
        PlayerHistory history = new PlayerHistory("10", Move.PAPER, "010B", "WLD");
        Player player = new Player("10");
        player.setName("TestPlayer");
        GameStore store = mock(GameStore.class);
        //when(store.getPlayer("10")).thenReturn(player);
        when(store.getPreferences("currentPlayer")).thenReturn("10");
        when(store.getPlayerHistory("10")).thenReturn(history);
        new GameModel(store, mEncoder, mTread, mHandler);
        verify(store).getPreferences("currentPlayer");
        verify(store).getPlayerHistory("10");
        //Assert.assertEquals("history updated", "010B", history.getUpDownHistory());
    }

    @Test
    public void onCreated_moveFinalized() {
        PlayerHistory history = new PlayerHistory("10", Move.PAPER, "010", "WLD");
        Player player = new Player("10");
        player.setName("TestPlayer");
        player.setEngineScore(100);
        player.setPlayerScore(100);
        GameStore store = mock(GameStore.class);
        when(store.getPreferences("mEngineMoveShown")).thenReturn("T");
        when(store.getPlayer("10")).thenReturn(player);
        when(store.getPreferences("currentPlayer")).thenReturn("10");
        when(store.getPlayerHistory("10")).thenReturn(history);
        new GameModel(store, mEncoder, mTread, mHandler);
        verify(store).getPreferences("currentPlayer");
        verify(store).getPlayerHistory("10");
        verify(store).updatePlayer(player);
        verify(store).setPreferences("mEngineMoveShown", "F");
        Assert.assertEquals(player.getEngineScore(), 101);
        Assert.assertEquals(player.getPlayerScore(), 100);
    }

    @Test
    public void onCreated_newPlayerCreated() {
        GameStore store = new GameStore_NewPlayerDouble();

        new GameModel(store, mEncoder, mTread, mHandler);

    }

    @Test
    public void prepareEngineMove_withDouble() {
        mCallbackCalled = false;

        GameStore store = new GameStore_MoveTestDouble();

        GameModel model = new GameModel(store, mEncoder, mTread, mHandler);

        when(mHandler.post(any(Runnable.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable r = invocation.getArgument(0);
                r.run();
                return null;
            }
        });

        model.prepareEngineMove(new GameModel.MoveCallback() {
            @Override
            public void onEngineMoveReady(Move m) {
                Assert.assertTrue(m == Move.SCISSORS);
                mCallbackCalled = true;

            }
        });

        Assert.assertTrue("Callback must be called", mCallbackCalled);
    }

    @Test
    public void prepareEngineMove_withMock() {
        mCallbackCalled = false;

        GameStore store = mock(GameStore.class);


        when(mHandler.post(any(Runnable.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable r = invocation.getArgument(0);
                r.run();
                return null;
            }
        });

        when(store.getPreferences("currentPlayer")).thenReturn("33");
        when(store.getPreferences("mEngineMoveShown")).thenReturn("F");
        when(store.getPlayerHistory("33")).thenReturn(new PlayerHistory("33",
                                                                        Move.SCISSORS,
                                                                        "12B",
                                                                        "L"));
        when(store.getHistoryRow("B", "33")).thenReturn(new HistoryRow("33", "B", 0, 0, 1));
        when(store.getHistoryRow("BS", "33")).thenReturn(new HistoryRow("33", "B", 0, 2, 0));
        when(store.getHistoryRow("2BLS", "33")).thenReturn(new HistoryRow("33", "B", 2, 0, 0));


        GameModel model = new GameModel(store, mEncoder, mTread, mHandler);

        GameModel.MoveCallback mc = new GameModel.MoveCallback() {
            @Override
            public void onEngineMoveReady(Move m) {
                Assert.assertEquals("Correct move is chosen", m, Move.PAPER);
                mCallbackCalled = true;

            }
        };

        for (int i = 0; i < 100; i++) {
            model.prepareEngineMove(mc);
        }

        Assert.assertTrue("Callback must be called", mCallbackCalled);
    }

    @Test
    public void prepareEngineMove_equalChoices() {

        mInt1 = 0;
        mInt2 = 0;

        GameStore store = mock(GameStore.class);

        when(mHandler.post(any(Runnable.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable r = invocation.getArgument(0);
                r.run();
                return null;
            }
        });

        when(store.getPreferences("currentPlayer")).thenReturn("33");
        when(store.getPreferences("mEngineMoveShown")).thenReturn("F");
        when(store.getPlayerHistory("33")).thenReturn(new PlayerHistory("33",
                                                                        Move.SCISSORS,
                                                                        "12B",
                                                                        "L"));
        when(store.getHistoryRow("B", "33")).thenReturn(new HistoryRow("33",
                                                                       "B",
                                                                       4,
                                                                       0,
                                                                       2));


        GameModel.MoveCallback mc = new GameModel.MoveCallback() {
            @Override
            public void onEngineMoveReady(Move m) {
                switch (m) {
                    case ROCK:
                        mInt1++;
                        break;
                    case PAPER:
                        mInt2++;
                        break;
                    default:
                        Assert.fail("Impossible move is chosen: " + m);
                }
            }
        };

        for (int i = 0; i < 100; i++) {
            GameModel model = new GameModel(store, mEncoder, mTread, mHandler);
            model.prepareEngineMove(mc);
        }

        int max = Math.max(mInt1, mInt2);
        int min = Math.min(mInt1, mInt2);
        Assert.assertTrue("ROCK (" + mInt1 + ") and PAPER (" + mInt2 + ") should be approximately " +
                                "same",
                          min > max - min);
    }

    @Test
    public void prepareEngineMove_moveIsNotRecalculated() {

        GameStore store = mock(GameStore.class);

        when(mHandler.post(any(Runnable.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable r = invocation.getArgument(0);
                r.run();
                return null;
            }
        });

        when(store.getPreferences("currentPlayer")).thenReturn("33");
        when(store.getPreferences("mEngineMoveShown")).thenReturn("F");
        when(store.getPlayerHistory("33")).thenReturn(new PlayerHistory("33",
                                                                        Move.SCISSORS,
                                                                        "12B",
                                                                        "L"));
        when(store.getHistoryRow("B", "33")).thenReturn(new HistoryRow("33",
                                                                       "B",
                                                                       0,
                                                                       0,
                                                                       0));

        GameModel model = new GameModel(store, mEncoder, mTread, mHandler);

        model.prepareEngineMove(new GameModel.MoveCallback() {
            @Override
            public void onEngineMoveReady(Move m) {
                mMove = m;
            }
        });

        GameModel.MoveCallback mc = new GameModel.MoveCallback() {
            @Override
            public void onEngineMoveReady(Move m) {
                Assert.assertSame("Move should be same", m, mMove);
            }
        };

        for (int i = 0; i < 100; i++) {
            model.prepareEngineMove(mc);
        }
    }

}
