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
import ga.agoponenko.rockpaperscissors.db.PlayerCursorWrapper;

class GameStore {
    private static GameStore ourInstance;

    private final Context mContext;
    private final SQLiteDatabase mDb;

    static GameStore getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }
        synchronized (GameStore.class) {
            if (ourInstance != null) {
                return ourInstance;
            }
            ourInstance = new GameStore(context);
            return ourInstance;
        }
    }

    private GameStore(Context context) {
        mContext = context.getApplicationContext();
        mDb = new DbHelper(mContext).getWritableDatabase();
    }

    public String getPreferences (String key) {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                                .getString(key, null);
    }

    public void setPreferences(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                         .edit()
                         .putString(key, value)
                         .apply();
    }

    public List<GameModel.Player> getPlayers() {
        List<GameModel.Player> players = new ArrayList<>();
        try (PlayerCursorWrapper cw = queryPlayers(null, null)) {
            cw.moveToFirst();
            while (!cw.isAfterLast()) {
                players.add(cw.getPlayer());
                cw.moveToNext();
            }
        }
        return players;
    }

    public GameModel.Player getPlayer(String id) {
        try (PlayerCursorWrapper cw = queryPlayers(DbSchema.PlayerTable.Cols.ID + " = ?",
                                                   new String[]{id})) {
            cw.moveToFirst();
            return cw.getPlayer();
        }
    }

    public long createPlayer(GameModel.Player player) {
        return mDb.insert(DbSchema.PlayerTable.NAME, null, getContentValues(player));
    }

    public void updatePlayer(GameModel.Player player) {
        mDb.update(DbSchema.PlayerTable.NAME,
                   getContentValues(player),
                   DbSchema.PlayerTable.Cols.ID + " = ?",
                   new String[] {player.getId()});
    }

    public void deletePlayer(String id) {
        // TODO: delete history
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

    private static ContentValues getContentValues(GameModel.Player player) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.PlayerTable.Cols.NAME, player.getName());
        //values.put(DbSchema.PlayerTable.Cols.ID, player.getId());
        values.put(DbSchema.PlayerTable.Cols.ENGINE_SCORE, player.getEngineScore());
        values.put(DbSchema.PlayerTable.Cols.PLAYER_SCORE, player.getPlayerScore());
        return values;
    }

}
