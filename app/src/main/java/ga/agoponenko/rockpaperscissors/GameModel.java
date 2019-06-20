package ga.agoponenko.rockpaperscissors;

import android.content.Context;

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
            mPlayerId = "6";
        }
        onGameResumed();
    }

    public void prepareEngineMove(MoveCallback callback) {
        if (mEngineMoveReady) {
            callback.onEngineMoveReady(mEngineMove);
        } else {
            Move move = Move.val[(int)(Math.random() * Move.size)];
            mEngineMove = move;
            mEngineMoveReady = true;
            callback.onEngineMoveReady(move);
        }
    }

    public int getPlayerScore() {
        return getCurrentPlayer().mPlayerScore;
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

    public int getEngineScore() {
        return getCurrentPlayer().mEngineScore;
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

    /*
     * returns WIN if player wins, LOSS if player looses
     */
    public Result onPlayerMove(Move playerMove) {
        if (BuildConfig.DEBUG && !mEngineMoveReady) {
            throw new AssertionError("Player moved while engine wasn't ready");
        }
        mEngineMoveReady = false;
        mEngineMoveShown = false;
        if (playerMove == mEngineMove) {
            return Result.DRAW;
        } else if (playerMove.next() == mEngineMove) {
            increaseEngineScore();
            return Result.LOSS;
        } else {
            increasePlayerScore();
            return Result.WIN;
        }
    }

    public void onPlayerNotMoved() {
        mEngineMoveReady = false;
        mEngineMoveShown = false;
        increaseEngineScore();
    }

    public void onGameResumed() {

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
        return player;
    }

    public void updatePlayer(Player player) {
        mStore.updatePlayer(player);
    }

    public void deletePlayer(String id) {
        mStore.deletePlayer(id);
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

    public  enum Result {
        WIN, LOSS, DRAW;

        public static Result fromInt(int i) {
            if (i == -1) {
                return null;
            } else {
                return  Result.values()[i];
            }
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
    }

    public interface MoveCallback {
        void onEngineMoveReady(Move m);
    }
}
