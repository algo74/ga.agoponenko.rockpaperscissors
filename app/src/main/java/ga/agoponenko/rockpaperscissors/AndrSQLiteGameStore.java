package ga.agoponenko.rockpaperscissors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import ga.agoponenko.rockpaperscissors.db.DbHelper;
import ga.agoponenko.rockpaperscissors.db.DbSchema;
import ga.agoponenko.rockpaperscissors.db.DbSchema.HistoryTable;
import ga.agoponenko.rockpaperscissors.db.DbSchema.PlayerHistoryTable;
import ga.agoponenko.rockpaperscissors.db.HistoryRowCursorWrapper;
import ga.agoponenko.rockpaperscissors.db.PlayerCursorWrapper;
import ga.agoponenko.rockpaperscissors.db.PlayerHistoryCursorWrapper;
import ga.agoponenko.rockpaperscissors.gamemodel.GameStore;
import ga.agoponenko.rockpaperscissors.gamemodel.HistoryRow;
import ga.agoponenko.rockpaperscissors.gamemodel.Player;
import ga.agoponenko.rockpaperscissors.gamemodel.PlayerHistory;

public class AndrSQLiteGameStore implements GameStore {
    private static GameStore ourInstance;

    private final Context mContext;
    private final SQLiteDatabase mDb;

    public static GameStore getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }
        synchronized (AndrSQLiteGameStore.class) {
            if (ourInstance != null) {
                return ourInstance;
            }
            ourInstance = new AndrSQLiteGameStore(context);
            return ourInstance;
        }
    }

    private AndrSQLiteGameStore(Context context) {
        mContext = context.getApplicationContext();
        mDb = new DbHelper(mContext).getWritableDatabase();
    }

    @Override
    public String getPreferences(String key) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                                .getString(key, null);
    }

    @Override
    public void setPreferences(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                         .edit()
                         .putString(key, value)
                         .apply();
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        try (PlayerCursorWrapper cw = queryPlayers(null, null)) {
            cw.moveToFirst();
            while (!cw.isAfterLast()) {
                players.add(cw.getPlayer());
                cw.moveToNext();
            }
        }
        return players;
    }

    @Override
    public Player getPlayer(String id) {
        try (PlayerCursorWrapper cw = queryPlayers(DbSchema.PlayerTable.Cols.ID + " = ?",
                                                   new String[]{id})) {
            cw.moveToFirst();
            return cw.getPlayer();
        }
    }

    @Override
    public long createPlayer(Player player) {
        return mDb.insert(DbSchema.PlayerTable.NAME, null, getContentValues(player));
    }

    @Override
    public void updatePlayer(Player player) {
        mDb.update(DbSchema.PlayerTable.NAME,
                   getContentValues(player),
                   DbSchema.PlayerTable.Cols.ID + " = ?",
                   new String[] {player.getId()});
    }

    @Override
    public void deletePlayer(String id) {
        deletePlayerHistory(id);
        mDb.delete(DbSchema.PlayerTable.NAME,
                   DbSchema.PlayerTable.Cols.ID + " = ?",
                   new String[] {id});
    }

    private PlayerCursorWrapper queryPlayers(String whereClause, String[] whereArgs) {
        Cursor cursor = mDb.query(
              DbSchema.PlayerTable.NAME,
              null,
              whereClause,
              whereArgs,
              null,
              null,
              DbSchema.PlayerTable.Cols.NAME
        );

        return new PlayerCursorWrapper(cursor);
    }

    @Override
    public long createPlayerHistory(PlayerHistory playerHistory) {
        return mDb.insert(DbSchema.PlayerHistoryTable.NAME,
                          null,
                          getContentValues(playerHistory));
    }

    @Override
    public void updatePlayerHistory(PlayerHistory playerHistory) {
        mDb.update(DbSchema.PlayerHistoryTable.NAME,
                   getContentValues(playerHistory),
                   PlayerHistoryTable.Cols.PLAYER_ID + " = ?",
                   new String[] {playerHistory.getPlayerId()});
    }

    @Override
    public void deletePlayerHistory(String playerId) {
        mDb.delete(DbSchema.PlayerHistoryTable.NAME,
                   PlayerHistoryTable.Cols.PLAYER_ID + " = ?",
                   new String[] {playerId});
    }

    @Override
    public PlayerHistory getPlayerHistory(String playerId) {
        try(PlayerHistoryCursorWrapper cw = queryPlayerHistory(
              PlayerHistoryTable.Cols.PLAYER_ID + " = ?",
              new String[] {playerId}
        )) {
            cw.moveToFirst();
            return cw.getPlayerHistory();
        }
    }

    private PlayerHistoryCursorWrapper queryPlayerHistory(String whereClause, String[] whereArgs) {
        Cursor cursor = mDb.query(
              DbSchema.PlayerHistoryTable.NAME,
              null,
              whereClause,
              whereArgs,
              null,
              null,
              null
        );
        return new PlayerHistoryCursorWrapper(cursor);
    }

    /**
     *
     * @param key history key
     * @param playerId id of the player or null for all players
     * @return the history row if exists or null if not
     */
    @Override
    public HistoryRow getHistoryRow(String key, String playerId) {
        try(HistoryRowCursorWrapper cw = queryHistory(
              HistoryTable.Cols.KEY + "=? and " + HistoryTable.Cols.PLAYER_ID + "=?",
              new String[] {key, playerId}
        )) {
            cw.moveToFirst();
            if (cw.isAfterLast()) {
                return null;
            } else {
                return cw.getRow();
            }
        }
    }

    @Override
    public void saveHistoryRow(HistoryRow row) {
        ContentValues contentValues = getContentValues(row);
        int res = mDb.update(
              HistoryTable.NAME,
              contentValues,
              HistoryTable.Cols.KEY + "=? AND " + HistoryTable.Cols.PLAYER_ID + "=?",
              new String[]{row.getKey(), row.getPlayerId()});
        if (res == 0) {
            mDb.insert(HistoryTable.NAME, null, contentValues);
        }
    }

    private HistoryRowCursorWrapper queryHistory(String whereClause, String[] whereArgs) {
        Cursor cursor = mDb.query(
              HistoryTable.NAME,
              null,
              whereClause,
              whereArgs,
              null,
              null,
              null
        );

        return new HistoryRowCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Player player) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.PlayerTable.Cols.NAME, player.getName());
        //values.put(DbSchema.PlayerTable.Cols.ID, player.getId());
        values.put(DbSchema.PlayerTable.Cols.ENGINE_SCORE, player.getEngineScore());
        values.put(DbSchema.PlayerTable.Cols.PLAYER_SCORE, player.getPlayerScore());
        return values;
    }

    private static ContentValues getContentValues(HistoryRow row) {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.KEY, row.getKey());
        values.put(HistoryTable.Cols.UP, row.getUp());
        values.put(HistoryTable.Cols.DOWN, row.getDown());
        values.put(HistoryTable.Cols.SAME, row.getSame());
        values.put(HistoryTable.Cols.PLAYER_ID, row.getPlayerId());
        return values;
    }

    private static ContentValues getContentValues(PlayerHistory ph) {
        ContentValues values = new ContentValues();
        values.put(PlayerHistoryTable.Cols.PLAYER_ID, ph.getPlayerId());
        values.put(PlayerHistoryTable.Cols.UP_DOWN_HISTORY, ph.getUpDownHistory());
        values.put(PlayerHistoryTable.Cols.WIN_LOSS_HISTORY, ph.getWinLossHistory());
        values.put(PlayerHistoryTable.Cols.LAST_MOVE, ph.getLastMove().ordinal());
        return values;
    }

}
