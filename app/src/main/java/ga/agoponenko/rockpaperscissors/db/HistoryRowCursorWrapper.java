package ga.agoponenko.rockpaperscissors.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import ga.agoponenko.rockpaperscissors.GameModel;
import ga.agoponenko.rockpaperscissors.db.DbSchema.HistoryTable;

public class HistoryRowCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public HistoryRowCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public GameModel.HistoryRow getRow() {
        GameModel.HistoryRow row =
              new GameModel.HistoryRow(getString(getColumnIndex(HistoryTable.Cols.PLAYER_ID)));
        row.setKey(getString(getColumnIndex(HistoryTable.Cols.KEY)));
        row.setDown(getInt(getColumnIndex(HistoryTable.Cols.DOWN)));
        row.setUp(getInt(getColumnIndex(HistoryTable.Cols.UP)));
        row.setSame(getInt(getColumnIndex(HistoryTable.Cols.SAME)));
        return row;
    }
}
