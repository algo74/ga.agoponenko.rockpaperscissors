package ga.agoponenko.rockpaperscissors.gamemodel;

public class HistoryRow {
    public static final int maxH = 8;
    public static final int maxR = 3;

    /**
     * format: m(0,8)r(0,3)l?
     * m :
     * 1 - player played up
     * 2 - down
     * 0 - same
     * Z - player timed out
     * B - game was interrupted
     * l :
     * R - last players move was rock
     * P
     * S
     * r :
     * W - player wins
     * L - player looses
     * D - draw
     */
    private String mKey;
    private String mPlayerId;
    int mUp = 0;
    int mDown = 0;
    int mSame = 0;

    public HistoryRow(String playerId) {
        mPlayerId = playerId;
    }

    public HistoryRow(String playerId, String key) {
        mPlayerId = playerId;
        mKey = key;
    }

    public HistoryRow(String playerId,
                      String key,
                      int up,
                      int down,
                      int same) {
        mPlayerId = playerId;
        mKey = key;
        mUp = up;
        mDown = down;
        mSame = same;
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
