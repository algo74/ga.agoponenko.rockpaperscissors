package ga.agoponenko.rockpaperscissors;

class GameStore {
    private static final GameStore ourInstance = new GameStore();

    static GameStore getInstance() {
        return ourInstance;
    }

    private GameStore() {
    }
}
