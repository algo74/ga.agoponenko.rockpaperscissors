package ga.agoponenko.rockpaperscissors.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import ga.agoponenko.rockpaperscissors.GameModel;
import ga.agoponenko.rockpaperscissors.db.DbSchema.HistoryTable;
import ga.agoponenko.rockpaperscissors.db.DbSchema.PlayerHistoryTable;

public class PlayerHistoryCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public PlayerHistoryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public GameModel.PlayerHistory getPlayerHistory() {
        GameModel.PlayerHistory ph =
              new GameModel.PlayerHistory(
                    getString(getColumnIndex(PlayerHistoryTable.Cols.PLAYER_ID)),
                    GameModel.Move.fromInt(getInt(getColumnIndex(PlayerHistoryTable.Cols.LAST_MOVE))),
                    getString(getColumnIndex(PlayerHistoryTable.Cols.UP_DOWN_HISTORY)),
                    getString(getColumnIndex(PlayerHistoryTable.Cols.WIN_LOSS_HISTORY)));
        return ph;
    }
}
