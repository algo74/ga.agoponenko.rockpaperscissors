package ga.agoponenko.rockpaperscissors;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.WriterException;

import java.util.List;

public class GameModel {
    private static final String PREF_CURRENT_PLAYER = "currentPlayer";
    private static GameModel sModel;

    private GameStore mStore;
    //private Context mContext;
    private boolean mEngineMoveReady;
    private boolean mEngineMoveShown;
    private Move mEngineMove;
    private String mPlayerId;
    private Bitmap mBitmap;

    public static GameModel getInstance(Context context) {
        if(sModel !=null) {
            return sModel;
        }
        synchronized (GameModel.class) {
            if (sModel == null) {
                sModel = new GameModel(context.getApplicationContext());
            }
            return sModel;
        }
    }

    private GameModel(Context context) {
        mStore = GameStore.getInstance(context);
        mPlayerId = mStore.getPreferences(PREF_CURRENT_PLAYER);
        if (mPlayerId == null) {
            mPlayerId = newPlayer().getId();
            mStore.setPreferences(PREF_CURRENT_PLAYER, mPlayerId);
        }
        onGameResumed();
    }

    public void prepareEngineMove(MoveCallback callback) {
        if (mEngineMoveReady) {
            callback.onEngineMoveReady(mEngineMove);
        } else {
            PlayerHistory playerHistory = getCurrentPlayerHistory();
            int up = 0, down = 0, same = 0;
            //Log.d("Full history",
            //      playerHistory.upDownHistory+playerHistory.winLossHistory+playerHistory.lastMove.toLetter());
            for(int i = 0; i < playerHistory.upDownHistory.length() ; i++) {
                String u = playerHistory.upDownHistory.substring(i);
                for(int j = 0; j <= playerHistory.winLossHistory.length(); j++) {
                    String w = playerHistory.winLossHistory.substring(j);
                    for (String l : new String[]{"", playerHistory.lastMove.toLetter()}) {
                        String key = u + w + l;
                        //Log.d("History row", key);
                        HistoryRow row = mStore.getHistoryRow(key, mPlayerId);
                        if (row != null) {
                            //Log.d("History row",
                            //      key + ": " + row.mSame + " " + row.mUp + " " + row.mDown);
                            up += row.mUp;
                            down += row.mDown;
                            same += row.mSame;
                        }
                    }
                }
            }

            Move move;

            int profitUp = same - down;
            int profitDown = up - same;
            int profitSame = down - up;

            Log.d("Totals", same + " " + up + " " + down);
            Log.d("Profits", profitSame + " " + profitUp + " " + profitDown);

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
                    move = Move.val[(int)(Math.random() * Move.size)];
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
            try {
                mBitmap = QREncoder.encodeAsBitmap("" + hint);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            mEngineMove = move;
            mEngineMoveReady = true;
            callback.onEngineMoveReady(move);
        }
    }

    private void increaseEngineScore() {
        Player player = getCurrentPlayer();
        player.mEngineScore++;
        updatePlayer(player);
    }

    private void increasePlayerScore() {
        Player player = getCurrentPlayer();
        player.mPlayerScore++;
        updatePlayer(player);
    }

    public boolean isEngineMoveReady() {
        return mEngineMoveReady;
    }

    public void onEngineMoveShown() {
        mEngineMoveShown = true;
    }

    public boolean isEngineMoveShown() {
        return mEngineMoveShown;
    }

    /**
     * @return WIN if player wins, LOSS if player looses
     */
    public Result onPlayerMove(Move playerMove) {
        if (BuildConfig.DEBUG && !mEngineMoveReady) {
            throw new AssertionError("Player moved while engine wasn't ready");
        }
        mEngineMoveReady = false;
        mEngineMoveShown = false;
        Result result;
        if (playerMove == mEngineMove) {
            result = Result.DRAW;
        } else if (playerMove.next() == mEngineMove) {
            increaseEngineScore();
            result = Result.LOSS;
        } else {
            increasePlayerScore();
            result = Result.WIN;
        }

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
        for(int i = 0; i < playerHistory.upDownHistory.length() ; i++) {
            String u = playerHistory.upDownHistory.substring(i);
            for(int j = 0; j <= playerHistory.winLossHistory.length(); j++) {
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
        playerHistory.upDownHistory = chomp(playerHistory.upDownHistory + upDown, HistoryRow.maxH);
        playerHistory.winLossHistory = chomp(playerHistory.winLossHistory + result.toLetter(), HistoryRow.maxR);
        playerHistory.lastMove = playerMove;
        mStore.updatePlayerHistory(playerHistory);

        return result;
    }

    private int forget_some(int i) {
        return i > 0 ? i-1 : 0;
    }

    public void onPlayerNotMoved() {
        mEngineMoveReady = false;
        mEngineMoveShown = false;
        increaseEngineScore();
        /*
         * update history
         * "win-loss" and "last move" are not updated, only "up-down"
         *
         * player's statistics in not updated either
         */
        PlayerHistory playerHistory = getCurrentPlayerHistory();
        playerHistory.upDownHistory = chomp(playerHistory.upDownHistory + "Z", HistoryRow.maxH);
        mStore.updatePlayerHistory(playerHistory);
    }

    private void onGameResumed() {
        PlayerHistory playerHistory = getCurrentPlayerHistory();
        if (playerHistory.upDownHistory.charAt(playerHistory.upDownHistory.length()-1) != 'B') {
            playerHistory.upDownHistory = chomp(playerHistory.upDownHistory + "B", HistoryRow.maxH);
            mStore.updatePlayerHistory(playerHistory);
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     *
     * @param id id of new players
     * @return true if player was switched, false if the players stays the same
     */
    public boolean choosePlayer(String id) {
        if(id.equals(mPlayerId)) {
            return false;
        } else {
            // finish everything for the previous player
            if (mEngineMoveReady && mEngineMoveShown) {
                onPlayerNotMoved();
            }
            // switch to new player
            mPlayerId = id;
            mEngineMoveReady = false;
            mEngineMoveShown = false;
            mStore.setPreferences(PREF_CURRENT_PLAYER, id);
            onGameResumed();

            return true;
        }
    }

    public Player getCurrentPlayer() {
        return getPlayer(mPlayerId);
    }

    public PlayerHistory getCurrentPlayerHistory() {
        return mStore.getPlayerHistory(mPlayerId);
    }

    public List<Player> getPlayers() {
        return mStore.getPlayers();
    }

    public Player getPlayer(String id) {return mStore.getPlayer(id);}

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

    private String chomp(String s, int i) {
        if(s.length() > i) {
            return s.substring(s.length() - i);
        } else {
            return s;
        }
    }

    public static class Player {

        private String mName;
        private int mPlayerScore;
        private int mEngineScore;
        private String mId;

        public Player(String id) {
            mId = id;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public int getPlayerScore() {
            return mPlayerScore;
        }

        public void setPlayerScore(int playerScore) {
            mPlayerScore = playerScore;
        }

        public int getEngineScore() {
            return mEngineScore;
        }

        public void setEngineScore(int engineScore) {
            mEngineScore = engineScore;
        }

        public String getId() {
            return mId;
        }

        private void setID(String id) {
            mId = id;
        }
    }

    public static class PlayerHistory {

        private String playerId;
        private Move lastMove;
        private String upDownHistory;
        private String winLossHistory;

        public PlayerHistory(String playerId,
                             Move lastMove,
                             String upDownHistory,
                             String winLossHistory) {
            this.playerId = playerId;
            this.lastMove = lastMove;
            this.upDownHistory = upDownHistory;
            this.winLossHistory = winLossHistory;
        }

        public PlayerHistory(String playerId) {
            this.playerId = playerId;
            this.lastMove = Move.ROCK;
            this.upDownHistory = "B";
            this.winLossHistory = "";
        }

        public String getPlayerId() {
            return playerId;
        }

        public Move getLastMove() {
            return lastMove;
        }

        public void setLastMove(Move lastMove) {
            this.lastMove = lastMove;
        }

        public String getUpDownHistory() {
            return upDownHistory;
        }

        public void setUpDownHistory(String upDownHistory) {
            this.upDownHistory = upDownHistory;
        }

        public String getWinLossHistory() {
            return winLossHistory;
        }

        public void setWinLossHistory(String winLossHistory) {
            this.winLossHistory = winLossHistory;
        }
    }

    public static class HistoryRow {
        public static final int maxH = 8;
        public static final int maxR = 3;

        /**
         * format: m(0,8)r(0,3)l?
         * m :
         *      1 - player played up
         *      2 - down
         *      0 - same
         *      Z - player timed out
         *      B - game was interrupted
         * l :
         *      R - last players move was rock
         *      P
         *      S
         * r :
         *      W - player wins
         *      L - player looses
         *      D - draw
         */
        private String mKey;
        private String mPlayerId;
        private int mUp = 0;
        private int mDown = 0;
        private int mSame = 0;

        public HistoryRow(String playerId) {
            mPlayerId = playerId;
        }

        public HistoryRow(String playerId, String key) {
            mPlayerId = playerId;
            mKey = key;
        }

        public String getKey() {
            return mKey;
        }

        public void setKey(String key) {
            mKey = key;
        }

        public int getUp() {
            return mUp;
        }

        public void setUp(int up) {
            mUp = up;
        }

        public int getDown() {
            return mDown;
        }

        public void setDown(int down) {
            mDown = down;
        }

        public int getSame() {
            return mSame;
        }

        public void setSame(int same) {
            mSame = same;
        }

        public String getPlayerId() {
            return mPlayerId;
        }
    }

    public  enum Result {
        WIN, LOSS, DRAW;

        public static Result fromInt(int i) {
            if (i == -1) {
                return null;
            } else {
                return  Result.values()[i];
            }
        }
        public String toLetter() {
            final String[] Letters = {"W", "L", "D"};
            return Letters[ordinal()];
        }
    }

    public enum Move {
        ROCK, PAPER, SCISSORS;

        public static final int size = Move.values().length;
        public static final Move[] val = Move.values();

        public Move next() {
            return val[(ordinal() + 1) % size];
        }
        public static Move fromInt(int i) {
            if (i == -1) {
                return null;
            } else {
                return  val[i];
            }
        }
        public String toLetter() {
            final String[] Letters = {"R", "P", "S"};
            return Letters[ordinal()];
        }
    }

    public interface MoveCallback {
        void onEngineMoveReady(Move m);
    }
}
