package ga.agoponenko.rockpaperscissors.gamemodel;

public class Player {

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

    void increasePlayerScore(int value) {
        mPlayerScore += value;
    }

    public int getEngineScore() {
        return mEngineScore;
    }

    public void setEngineScore(int engineScore) {
        mEngineScore = engineScore;
    }

    void increaseEngineScore(int value) {
        mEngineScore += value;
    }

    public String getId() {
        return mId;
    }

    void setID(String id) {
        mId = id;
    }
}
