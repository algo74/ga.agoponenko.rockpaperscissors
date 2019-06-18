package ga.agoponenko.rockpaperscissors;

public class GameModel {
    private static GameModel mModel = new GameModel();

    private GameStore mStore = GameStore.getInstance();
    private boolean mEngineMoveReady;
    private boolean mEngineMoveShown;
    private int mPlayerScore = 0;
    private int mEngineScore = 0;
    private Move mEngineMove;

    public static GameModel getInstance() {
        return mModel;
    }

    private GameModel() {
        super();
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
        return mPlayerScore;
    }

    public int getEngineScore() {
        return mEngineScore;
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
            mEngineScore++;
            return Result.LOSS;
        } else {
            mPlayerScore++;
            return Result.WIN;
        }
    }

    public void onPlayerNotMoved() {
        mEngineMoveReady = false;
        mEngineMoveShown = false;
        mEngineScore++;
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
