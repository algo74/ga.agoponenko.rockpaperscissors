package ga.agoponenko.rockpaperscissors.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ga.agoponenko.rockpaperscissors.db.DbSchema.HistoryTable;
import ga.agoponenko.rockpaperscissors.db.DbSchema.PlayerTable;

public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DB_NAME = "dbRPS.db";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PlayerTable.NAME + "(" +
                         "_id integer primary key autoincrement, " +
                         PlayerTable.Cols.NAME + "," +
                         PlayerTable.Cols.ENGINE_SCORE + "," +
                         PlayerTable.Cols.PLAYER_SCORE + ")"
        );
        db.execSQL("create index " + PlayerTable.Indexes.NAME_INDEX +
                         " on " + PlayerTable.NAME + "(" + PlayerTable.Cols.NAME + ")");

        db.execSQL("create table " + HistoryTable.NAME + "(" +
                         "_id integer primary key autoincrement, " +
                         HistoryTable.Cols.PLAYER_ID + "," +
                         HistoryTable.Cols.KEY + "," +
                         HistoryTable.Cols.UP + "," +
                         HistoryTable.Cols.DOWN + "," +
                         HistoryTable.Cols.SAME + "," +
                         "FOREIGN KEY(" +
                            HistoryTable.Cols.PLAYER_ID +
                         ") REFERENCES " + PlayerTable.NAME + "(_id))"
        );
        db.execSQL("create index " + HistoryTable.Indexes.PLAYER_INDEX +
                         " on " + HistoryTable.NAME + "(" + HistoryTable.Cols.PLAYER_ID + ")");
        db.execSQL("create index " + HistoryTable.Indexes.KEY_INDEX +
                         " on " + HistoryTable.NAME + "(" + HistoryTable.Cols.KEY + ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
