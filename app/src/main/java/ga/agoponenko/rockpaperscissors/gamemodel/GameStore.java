package ga.agoponenko.rockpaperscissors.gamemodel;

import java.util.List;

import ga.agoponenko.rockpaperscissors.gamemodel.HistoryRow;
import ga.agoponenko.rockpaperscissors.gamemodel.Player;
import ga.agoponenko.rockpaperscissors.gamemodel.PlayerHistory;

public interface GameStore {
    String getPreferences(String key);

    void setPreferences(String key, String value);

    List<Player> getPlayers();

    Player getPlayer(String id);

    long createPlayer(Player player);

    void updatePlayer(Player player);

    void deletePlayer(String id);

    long createPlayerHistory(PlayerHistory playerHistory);

    void updatePlayerHistory(PlayerHistory playerHistory);

    void deletePlayerHistory(String playerId);

    PlayerHistory getPlayerHistory(String playerId);

    HistoryRow getHistoryRow(String key, String playerId);

    void saveHistoryRow(HistoryRow row);
}
