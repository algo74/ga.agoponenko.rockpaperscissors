package ga.agoponenko.rockpaperscissors.gamemodel;

public class PlayerHistory {

    String playerId;
    Move lastMove;
    String upDownHistory;
    String winLossHistory;

    public static String chomp(String s, int i) {
        if (s.length() > i) {
            return s.substring(s.length() - i);
        } else {
            return s;
        }
    }

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
