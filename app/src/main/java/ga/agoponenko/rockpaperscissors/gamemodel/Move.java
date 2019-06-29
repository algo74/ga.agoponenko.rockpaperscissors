package ga.agoponenko.rockpaperscissors.gamemodel;

public enum Move {
    ROCK, PAPER, SCISSORS;

    public static final int size = Move.values().length;
    public static final Move[] val = Move.values();

    public static Move fromInt(int i) {
        if (i == -1) {
            return null;
        } else {
            return val[i];
        }
    }

    public Move next() {
        return val[(ordinal() + 1) % size];
    }

    public String toLetter() {
        final String[] Letters = {"R", "P", "S"};
        return Letters[ordinal()];
    }
}
