package ga.agoponenko.rockpaperscissors;

public class PlayerModel {

    private String mName;
    private int mPlayerScore;
    private int mEngineScore;
    private final int mId;

    public PlayerModel(int id) {
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

    public int getId() {
        return mId;
    }
}
