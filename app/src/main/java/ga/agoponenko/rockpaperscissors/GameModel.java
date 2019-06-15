package ga.agoponenko.rockpaperscissors;

public class GameModel {
    private static GameModel mModel = new GameModel();

    private GameStore mStore = GameStore.getInstance();

    public static GameModel getInstance() {
        return mModel;
    }

    private GameModel() {
        super();
    }

    public void prepareEngineMove(MoveCallback callback) {
        callback.onEngineMoveReady(Move.val[(int)(Math.random() * Move.size)]);
    }

    public enum Move {
        ROCK, PAPER, SCISSORS;

        public static final int size = Move.values().length;
        public static final Move[] val = Move.values();

        public Move next() {
            return val[(ordinal() + 1) % size];
        }
    }

    public interface MoveCallback {
        void onEngineMoveReady(Move m);
    }
}
