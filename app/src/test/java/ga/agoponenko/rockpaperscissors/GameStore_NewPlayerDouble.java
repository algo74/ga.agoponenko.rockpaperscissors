package ga.agoponenko.rockpaperscissors;

import org.junit.Assert;

import java.util.List;

import ga.agoponenko.rockpaperscissors.gamemodel.GameStore;
import ga.agoponenko.rockpaperscissors.gamemodel.HistoryRow;
import ga.agoponenko.rockpaperscissors.gamemodel.Move;
import ga.agoponenko.rockpaperscissors.gamemodel.Player;
import ga.agoponenko.rockpaperscissors.gamemodel.PlayerHistory;

class GameStore_NewPlayerDouble implements GameStore {

    Player player;
    LastAction lastAction = LastAction.init;
    boolean playerIdLoaded = false;
    boolean playerCreated = false;
    private boolean playerUpdated = false;
    private PlayerHistory mPlayerHistory;
    private boolean playerHistoryCreated = false;

    enum LastAction {
        init,
        getGetPreference_currentPlayer,
        createPlayer
    }


    @Override
    public String getPreferences(String key) {
        switch (lastAction) {
            case init:
                Assert.assertEquals("must load player id", key, "currentPlayer");
                lastAction = LastAction.getGetPreference_currentPlayer;
                playerIdLoaded = true;
                return null;
        }
        Assert.fail("Requested preference <" + key + "> after " + lastAction);
        return null;
    }

    @Override
    public void setPreferences(String key, String value) {
        if (key.equals("currentPlayer")) {
            Assert.assertEquals(value, "11");
            Assert.assertTrue(playerUpdated);
        } else {
            Assert.fail();
        }

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
        Assert.assertEquals(playerId, "11");
        Assert.assertTrue(playerHistoryCreated);

        return mPlayerHistory;
    }

    @Override
    public HistoryRow getHistoryRow(String key, String playerId) {
        Assert.fail("should not be called");

        return null;
    }

    @Override
    public void saveHistoryRow(HistoryRow row) {
        Assert.fail("should not be called");

    }
}
