package ga.agoponenko.rockpaperscissors.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import ga.agoponenko.rockpaperscissors.GameModel;

public class PlayerCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public PlayerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public GameModel.Player getPlayer() {
        String id = getString(getColumnIndex(DbSchema.PlayerTable.Cols.ID));
        String  name = getString(getColumnIndex(DbSchema.PlayerTable.Cols.NAME));
        int playerScore = getInt(getColumnIndex(DbSchema.PlayerTable.Cols.PLAYER_SCORE));
        int engineScore = getInt(getColumnIndex(DbSchema.PlayerTable.Cols.ENGINE_SCORE));

        GameModel.Player player = new GameModel.Player(id);
        player.setEngineScore(engineScore);
        player.setPlayerScore(playerScore);
        player.setName(name);

        return player;
    }
}
