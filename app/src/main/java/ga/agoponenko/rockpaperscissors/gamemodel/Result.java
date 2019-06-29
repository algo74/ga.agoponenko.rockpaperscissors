package ga.agoponenko.rockpaperscissors.gamemodel;

public enum Result {
    WIN, LOSS, DRAW;

    public static Result fromInt(int i) {
        if (i == -1) {
            return null;
        } else {
            return Result.values()[i];
        }
    }

    public String toLetter() {
        final String[] Letters = {"W", "L", "D"};
        return Letters[ordinal()];
    }
}
