package ga.agoponenko.rockpaperscissors.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import ga.agoponenko.rockpaperscissors.db.DbSchema.PlayerHistoryTable;
import ga.agoponenko.rockpaperscissors.gamemodel.Move;
import ga.agoponenko.rockpaperscissors.gamemodel.PlayerHistory;

public class PlayerHistoryCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public PlayerHistoryCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public PlayerHistory getPlayerHistory() {
        PlayerHistory ph =
              new PlayerHistory(
                    getString(getColumnIndex(PlayerHistoryTable.Cols.PLAYER_ID)),
                    Move.fromInt(getInt(getColumnIndex(PlayerHistoryTable.Cols.LAST_MOVE))),
                    getString(getColumnIndex(PlayerHistoryTable.Cols.UP_DOWN_HISTORY)),
                    getString(getColumnIndex(PlayerHistoryTable.Cols.WIN_LOSS_HISTORY)));
        return ph;
    }
}
