package com.rekkursion.puzzlegame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PuzzleGameRanking.db";
    public static final String RANKING_TABLE_NAME = "PuzzleGameRanking";
    public static final String RANKING_TABLE_COL_ID = "_id";
    public static final String RANKING_TABLE_COL_DIFFICULTY = "_difficulty";
    public static final String RANKING_TABLE_COL_COST_TIME = "_cost_time";
    public static final String RANKING_TABLE_COL_MOVED_COUNT = "_moved_count";
    public static final String RANKING_TABLE_COL_DATE = "_date";
    private final Context context;

    // constructor
    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String sqlCreateTableString = "CREATE TABLE IF NOT EXISTS " + RANKING_TABLE_NAME + " (" +
                RANKING_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RANKING_TABLE_COL_COST_TIME + " INTEGER, " +
                RANKING_TABLE_COL_MOVED_COUNT + " INTEGER, " +
                RANKING_TABLE_COL_DATE + " VARCHAR(10), " +
                RANKING_TABLE_COL_DIFFICULTY + " INTEGER" +
                ");";
        sqLiteDatabase.execSQL(sqlCreateTableString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // insert
    public void insertData(RankingRecordItemModel rr) {
        final SQLiteDatabase db = getWritableDatabase();
        final ContentValues cv = new ContentValues();

        cv.put(RANKING_TABLE_COL_DIFFICULTY, rr.getGameDifficulty());
        cv.put(RANKING_TABLE_COL_COST_TIME, rr.getCostTime());
        cv.put(RANKING_TABLE_COL_MOVED_COUNT, rr.getMovedCount());
        cv.put(RANKING_TABLE_COL_DATE, rr.getRecordDateStringByFormat(GameManager.RECORD_DATE_AND_TIME_FORMAT_STRING));
        final long result = db.insert(RANKING_TABLE_NAME, null, cv);

        if(result == -1L)
            Toast.makeText(this.context, "Some error happened when inserting data.", Toast.LENGTH_SHORT).show();

        db.close();
    }

    // read
    public List<RankingRecordItemModel> readData() {
        final String sqlQueryString = "SELECT * FROM " + RANKING_TABLE_NAME;
        return readDataHelper(sqlQueryString);
    }

    // read the data where their game diffs are the same
    public List<RankingRecordItemModel> readData(int designatedDifficulty) {
        final String sqlQueryString = "SELECT * FROM " + RANKING_TABLE_NAME + " WHERE " + RANKING_TABLE_COL_DIFFICULTY + " = '" + String.valueOf(designatedDifficulty) + "'";
        return readDataHelper(sqlQueryString);
    }

    // TODO: no tested
    // delete
    public void deleteData(RankingRecordItemModel rr) {
        final SQLiteDatabase db = getWritableDatabase();
        db.delete(RANKING_TABLE_NAME, RANKING_TABLE_COL_DATE + "=\"" + rr.getRecordDateStringByFormat(GameManager.RECORD_DATE_AND_TIME_FORMAT_STRING) + "\"", null);
    }

    // read data through the query string
    private List<RankingRecordItemModel> readDataHelper(String sqlQueryString) {
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.rawQuery(sqlQueryString, null);
        final List<RankingRecordItemModel> retList = new ArrayList<>();

        if(cursor.moveToFirst()) {
            do {
                final int _difficulty = cursor.getInt(cursor.getColumnIndex(RANKING_TABLE_COL_DIFFICULTY));
                final int _costTime = cursor.getInt(cursor.getColumnIndex(RANKING_TABLE_COL_COST_TIME));
                final int _movedCount = cursor.getInt(cursor.getColumnIndex(RANKING_TABLE_COL_MOVED_COUNT));
                final String _dateString = cursor.getString(cursor.getColumnIndex(RANKING_TABLE_COL_DATE));

                Date _date;
                try {
                    _date = new SimpleDateFormat(GameManager.RECORD_DATE_AND_TIME_FORMAT_STRING).parse(_dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                    _date = Calendar.getInstance().getTime();
                }

                retList.add(new RankingRecordItemModel(_difficulty, _movedCount, _costTime, _date));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return retList;
    }
}
