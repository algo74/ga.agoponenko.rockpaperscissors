package ga.agoponenko.rockpaperscissors;

import org.junit.Assert;

import java.util.List;

import ga.agoponenko.rockpaperscissors.gamemodel.GameStore;
import ga.agoponenko.rockpaperscissors.gamemodel.HistoryRow;
import ga.agoponenko.rockpaperscissors.gamemodel.Move;
import ga.agoponenko.rockpaperscissors.gamemodel.Player;
import ga.agoponenko.rockpaperscissors.gamemodel.PlayerHistory;

class GameStore_MoveTestDouble implements GameStore {

    Player player;
    LastAction lastAction = LastAction.init;
    boolean playerIdLoaded = false;
    boolean playerCreated = false;
    private boolean playerUpdated = false;
    private PlayerHistory mPlayerHistory = new PlayerHistory("22",
                                                             Move.PAPER,
                                                             "0102B",
                                                             "WDL");
    private boolean playerHistoryCreated = false;

    enum LastAction {
        init,
        getPreference_currentPlayer,
        getPreference_mEngineMoveShown,
        createPlayer
    }



    private String getPreferences(String key) {
        switch (lastAction) {
            case init:
                Assert.assertEquals("must load player id", key, "currentPlayer");
                lastAction = LastAction.getPreference_currentPlayer;
                playerIdLoaded = true;
                return "22";
            case getPreference_currentPlayer:
                Assert.assertEquals("must check if player engine was shown", key,
                                    "mEngineMoveShown");
                lastAction = LastAction.getPreference_mEngineMoveShown;
                return "F";
        }
        Assert.fail("Requested preference <" + key + "> after " + lastAction);
        return null;
    }

    @Override
    public String getPrefPlayer() {
        return getPreferences("currentPlayer");
    }

    @Override
    public void setPrefPlayer(String id) {
        Assert.assertEquals(id, "11");
        Assert.assertTrue(playerUpdated);
    }

    @Override
    public boolean getPrefMoveShown() {
        return getPreferences("mEngineMoveShown") == "T";
    }

    @Override
    public void setPrefMoveShown(boolean shown) {
        Assert.fail();
    }

    @Override
    public List<Player> getPlayers() {
        Assert.fail("should not be called");
        return null;
    }

    @Override
    public Player getPlayer(String id) {
        Assert.fail("should not be called");

        return null;
    }

    @Override
    public long createPlayer(Player player) {
        Assert.assertEquals("must create player with zero player score",
                            player.getPlayerScore(),
                            0);
        Assert.assertEquals("must create player with zero engine score",
                            player.getEngineScore(),
                            0);
        Assert.assertNull("must create player with no id",
                          player.getId());
        Assert.assertTrue("must first try to load player id", playerIdLoaded);
        lastAction = LastAction.createPlayer;
        playerCreated = true;
        return 11;
    }

    @Override
    public void updatePlayer(Player player) {
        Assert.assertTrue("must first create player", playerCreated);
        Assert.assertTrue("must use the same id", player.getId().equals("11"));
        Assert.assertEquals("must create player with zero player score",
                            player.getPlayerScore(),
                            0);
        Assert.assertEquals("must create player with zero engine score",
                            player.getEngineScore(),
                            0);
        playerUpdated = true;
    }

    @Override
    public void deletePlayer(String id) {
        Assert.fail("should not be called");

    }

    @Override
    public long createPlayerHistory(PlayerHistory playerHistory) {
        Assert.assertTrue("player should be updated first", playerUpdated);
        Assert.assertEquals("history should be empty", playerHistory.getUpDownHistory(), "B");
        Assert.assertEquals("history should be empty", playerHistory.getWinLossHistory(), "");
        Assert.assertEquals("history should be empty", playerHistory.getLastMove(), Move.ROCK);
        mPlayerHistory = playerHistory;
        playerHistoryCreated = true;
        return 0;
    }

    @Override
    public void updatePlayerHistory(PlayerHistory playerHistory) {
        Assert.fail("should not be called");

    }

    @Override
    public void deletePlayerHistory(String playerId) {
        Assert.fail("should not be called");

    }

    @Override
    public PlayerHistory getPlayerHistory(String playerId) {
        Assert.assertEquals(playerId, "22");
        return mPlayerHistory;
    }

    @Override
    public HistoryRow getHistoryRow(String key, String playerId) {
        //Assert.fail("should not be called");
        HistoryRow h = new HistoryRow(playerId, key);
        switch (key) {
            case "0102BWDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "0102BWDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "102BWDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "102BWDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "02BWDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "02BWDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "2BWDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "2BWDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "BWDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "BWDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;

            case "0102BDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "0102BDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "102BDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "102BDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "02BDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "02BDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "2BDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "2BDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "BDL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "BDLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;

            case "0102BL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "0102BLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "102BL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "102BLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "02BL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "02BLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "2BL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "2BLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "BL":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "BLP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;

            case "0102B":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "0102BP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "102B":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "102BP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "02B":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "02BP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "2B":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "2BP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "B":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;
            case "BP":
                h.setUp(1);
                h.setDown(0);
                h.setSame(1);
                return h;

            case "" :
                Assert.fail("empty history key");
                return null;
        }
        Assert.fail("Unknown player history key: <" + key + ">");
        return null;
    }

    @Override
    public void saveHistoryRow(HistoryRow row) {
        Assert.fail("should not be called");

    }
}
