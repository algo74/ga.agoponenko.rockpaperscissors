package ga.agoponenko.rockpaperscissors.gamemodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import java.util.List;

import ga.agoponenko.rockpaperscissors.AndrSQLiteGameStore;
import ga.agoponenko.rockpaperscissors.BuildConfig;
import ga.agoponenko.rockpaperscissors.GameModelBackgroundThread;
import ga.agoponenko.rockpaperscissors.QREncoder;

public class GameModel {

    @SuppressLint("StaticFieldLeak")
    private static GameModel sModel;
    private final GameModelBackgroundThread mBackgroundThread;
    private final Handler mResponseHandler;
    private final QREncoder mEncoder;
    private GameStore mStore;
    private boolean mEngineMoveReady;
    private boolean mEngineMoveShown;
    private Move mEngineMove;
    private String mPlayerId;
    private Bitmap mBitmap;


    private GameModel(Context context) {
        this(AndrSQLiteGameStore.getInstance(context),
             new QREncoder(context),
             new GameModelBackgroundThread(),
             new Handler()
        );
    }

    public GameModel(GameStore store,
                     QREncoder encoder,
                     GameModelBackgroundThread thread,
                     Handler handler) {
        mStore = store;
        mBackgroundThread = thread;
        mBackgroundThread.start();
        mBackgroundThread.getLooper();
        mResponseHandler = handler;
        mEncoder = encoder;
        mPlayerId = mStore.getPrefPlayer();

        if (getCurrentPlayer() == null) {
            //newCurrentPlayer();
        } else {
            if((mStore.getPrefMoveShown())) {
                increaseEngineScore();
                mStore.setPrefMoveShown(false);
            }
            onGameResumed();
        }
    }

    @VisibleForTesting
    public void newCurrentPlayer() {
        mPlayerId = newPlayer().getId();
        mStore.setPrefPlayer(mPlayerId);
    }

    public static GameModel getInstance(Context context) {
        if (sModel != null) {
            return sModel;
        }
        synchronized (GameModel.class) {
            if (sModel == null) {
                sModel = new GameModel(context.getApplicationContext());
            }
            return sModel;
        }
    }

    public void prepareEngineMove(final MoveCallback callback) {

        if (mEngineMoveShown) {
            //Log.d("prepareEngineMove", "Move was shown - force onPlayerNotMoved()");
            onPlayerNotMoved();
        }

        mBackgroundThread.queueTask(new Runnable() {
            @Override
            public void run() {

                // response to run on the main thread
                Runnable response = new Runnable() {
                    @Override
                    public void run() {
                        if (mEngineMoveReady) {
                            callback.onEngineMoveReady(mEngineMove);
                        } else {
                            //Log.d("prepareEngineMove", "restarting because of conflict");
                            prepareEngineMove(callback);
                        }
                    }
                };

                // task to run on the background thread
                if (mEngineMoveReady) {
                    mResponseHandler.post(response);
                } else {
                    PlayerHistory playerHistory = getCurrentPlayerHistory();
                    int up = 0, down = 0, same = 0;
                    //Log.d("Full history",
                    //      playerHistory.upDownHistory+playerHistory
                    //      .winLossHistory+playerHistory.lastMove.toLetter());
                    for (int i = 0; i < playerHistory.upDownHistory.length(); i++) {
                        String u = playerHistory.upDownHistory.substring(i);
                        for (int j = 0; j <= playerHistory.winLossHistory.length(); j++) {
                            String w = playerHistory.winLossHistory.substring(j);
                            for (String l : new String[]{"", playerHistory.lastMove.toLetter()}) {
                                String key = u + w + l;
                                //Log.d("History row", key);
                                HistoryRow row = mStore.getHistoryRow(key, mPlayerId);
                                if (row != null) {
                                    //Log.d("History row",
                                    //      key + ": " + row.mSame + " " + row.mUp + " " + row
                                    //      .mDown);
                                    up += row.getUp();
                                    down += row.getDown();
                                    same += row.getSame();
                                }
                            }
                        }
                    }

                    Move move;

                    int profitUp = same - down;
                    int profitDown = up - same;
                    int profitSame = down - up;

                    //Log.d("Totals", same + " " + up + " " + down);
                    //Log.d("Profits", profitSame + " " + profitUp + " " + profitDown);

                    if (profitDown > profitSame) {
                        if (profitUp > profitDown) {
                            move = playerHistory.lastMove.next();
                        } else { // profitSame < profitUp <= profitDown
                            if (profitUp < profitDown) {
                                move = playerHistory.lastMove.next().next();
                            } else { // profitSame < profitUp == profitDown
                                move = Math.random() < 0.5 ?
                                      playerHistory.lastMove.next() :
                                      playerHistory.lastMove.next().next();
                            }
                        }
                    } else if (profitDown == profitSame) {
                        if (profitUp > profitSame) {
                            move = playerHistory.lastMove.next();
                        } else if (profitUp == profitSame) { // all equal
                            move = Move.val[(int) (Math.random() * Move.size)];
                        } else { //profitUp < profitSame == profitDown
                            move = Math.random() < 0.5 ?
                                  playerHistory.lastMove :
                                  playerHistory.lastMove.next().next();
                        }
                    } else { // profitDown < profitSame
                        if (profitUp > profitSame) {
                            move = playerHistory.lastMove.next();
                        } else if (profitUp == profitSame) {
                            // profitDown < profitSame == profitUp
                            move = Math.random() < 0.5 ?
                                  playerHistory.lastMove :
                                  playerHistory.lastMove.next();
                        } else { // profitSame is highest
                            move = playerHistory.lastMove;
                        }
                    }

                    // prepare QR code
                    long hint = move.ordinal() + 3 * (long) (Math.random() * 10000000000.0);
                    mBitmap = mEncoder.encodeHint(hint);
                    mEngineMove = move;
                    mEngineMoveReady = true;
                    mResponseHandler.post(response);
                }
            }
        });


    }

    private void increaseEngineScore() {
        Player player = getCurrentPlayer();
        player.increaseEngineScore(1);
        updatePlayer(player);
    }

    private void increasePlayerScore() {
        Player player = getCurrentPlayer();
        player.increasePlayerScore(1);
        updatePlayer(player);
    }

    private void setEngineMoveShown(boolean b) {
        mEngineMoveShown = b;
        if (b) {
            mStore.setPrefMoveShown(true);
        } else {
            mStore.setPrefMoveShown(false);
        }
    }

    public void onEngineMoveShown() {
        setEngineMoveShown(true);
    }

    /**
     * @return WIN if player wins, LOSS if player looses
     */
    public Result onPlayerMove(final Move playerMove) {
        if (BuildConfig.DEBUG && !mEngineMoveReady) {
            throw new AssertionError("Player moved while engine wasn't ready");
        }
        mEngineMoveReady = false;
        setEngineMoveShown(false);
        final Result result;
        if (playerMove == mEngineMove) {
            result = Result.DRAW;
        } else if (playerMove.next() == mEngineMove) {
            increaseEngineScore();
            result = Result.LOSS;
        } else {
            increasePlayerScore();
            result = Result.WIN;
        }

        mBackgroundThread.queueTask(new Runnable() {
            @Override
            public void run() {
                PlayerHistory playerHistory = getCurrentPlayerHistory();
                // save history
                //updateStatistics(playerMove);

                int upDown;
                if (playerMove == playerHistory.lastMove) {
                    upDown = 0;
                } else if (playerMove == playerHistory.lastMove.next()) {
                    upDown = 1;
                } else {
                    upDown = 2;
                }
                for (int i = 0; i < playerHistory.upDownHistory.length(); i++) {
                    String u = playerHistory.upDownHistory.substring(i);
                    for (int j = 0; j <= playerHistory.winLossHistory.length(); j++) {
                        String w = playerHistory.winLossHistory.substring(j);
                        for (String l : new String[]{"", playerHistory.lastMove.toLetter()}) {
                            String key = u + w + l;
                            HistoryRow row = mStore.getHistoryRow(key, mPlayerId);
                            if (row == null) {
                                row = new HistoryRow(mPlayerId, key);
                            }
                            // forget old
                            row.mDown = forget_some(row.mDown);
                            row.mSame = forget_some(row.mSame);
                            row.mUp = forget_some(row.mUp);
                            // remember new
                            switch (upDown) {
                                case 0:
                                    row.mSame += 3;
                                    break;
                                case 1:
                                    row.mUp += 3;
                                    break;
                                default:
                                    row.mDown += 3;
                            }
                            mStore.saveHistoryRow(row);
                        }
                    }
                }

                //updateHistory(playerMove, result);
                playerHistory.setUpDownHistory(
                      PlayerHistory.chomp(playerHistory.getUpDownHistory() + upDown,
                                          HistoryRow.maxH));
                playerHistory.setWinLossHistory(
                      PlayerHistory.chomp(playerHistory.getWinLossHistory() + result.toLetter(),
                            HistoryRow.maxR));
                playerHistory.lastMove = playerMove;
                mStore.updatePlayerHistory(playerHistory);
            }
        });

        return result;
    }

    private int forget_some(int i) {
        return i > 0 ? i - 1 : 0;
    }

    public void onPlayerNotMoved() {
        mEngineMoveReady = false;
        setEngineMoveShown(false);
        increaseEngineScore();
        /*
         * update history
         * "win-loss" and "last move" are not updated, only "up-down"
         *
         * player's statistics in not updated either
         */
        PlayerHistory playerHistory = getCurrentPlayerHistory();
        playerHistory.upDownHistory = PlayerHistory.chomp(playerHistory.upDownHistory + "Z", HistoryRow.maxH);
        mStore.updatePlayerHistory(playerHistory);
    }

    private void onGameResumed() {
        PlayerHistory playerHistory = getCurrentPlayerHistory();
        if (playerHistory.upDownHistory.charAt(playerHistory.upDownHistory.length() - 1) != 'B') {
            playerHistory.upDownHistory = PlayerHistory.chomp(playerHistory.upDownHistory + "B",
                                                 HistoryRow.maxH);
            mStore.updatePlayerHistory(playerHistory);
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * @param id id of new players
     * @return true if player was switched, false if the players stays the same
     */
    public boolean choosePlayer(String id) {
        if (id.equals(mPlayerId)) {
            return false;
        } else {
            // finish everything for the previous player
            if (mEngineMoveReady && mEngineMoveShown) {
                onPlayerNotMoved();
            }
            // switch to new player
            mPlayerId = id;
            mEngineMoveReady = false;
            setEngineMoveShown(false);
            mStore.setPrefPlayer(id);
            onGameResumed();

            return true;
        }
    }

    public Player getCurrentPlayer() {
        Player player = getPlayer(mPlayerId);
        //if (player == null) {
        //    newCurrentPlayer();
        //    player = getPlayer(mPlayerId);
        //}
        return player;
    }

    private PlayerHistory getCurrentPlayerHistory() {
        return mStore.getPlayerHistory(mPlayerId);
    }

    public List<Player> getPlayers() {
        return mStore.getPlayers();
    }

    public Player getPlayer(String id) {
        return mStore.getPlayer(id);
    }

    public Player newPlayer() {
        Player player = new Player(null);
        player.setPlayerScore(0);
        player.setEngineScore(0);
        player.setName("Player");
        String id = "" + mStore.createPlayer(player);
        player.setID(id);
        player.setName("Player " + id);
        updatePlayer(player);
        PlayerHistory ph = new PlayerHistory(id);
        mStore.createPlayerHistory(ph);
        return player;
    }

    public void updatePlayer(Player player) {
        mStore.updatePlayer(player);
    }

    public void deletePlayer(String id) {
        mStore.deletePlayer(id);
    }

    public interface MoveCallback {
        void onEngineMoveReady(Move m);
    }

}
