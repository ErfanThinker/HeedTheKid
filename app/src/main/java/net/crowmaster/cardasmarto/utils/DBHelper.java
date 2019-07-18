package net.crowmaster.cardasmarto.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.crowmaster.cardasmarto.contracts.DBContract;
import net.crowmaster.cardasmarto.entities.HistoryDetailedEntity;
import net.crowmaster.cardasmarto.entities.HistorySimpleEntity;

import java.util.ArrayList;

/**
 * Created by root on 7/14/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " + DBContract.DataTable.TABLE_NAME +
                        " ( " + DBContract.DataTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DBContract.DataTable.COLUMN_CLIENT_TIME + " INTEGER, " +
                        DBContract.DataTable.COLUMN_SERVER_TIME + " INTEGER, " +
                        DBContract.DataTable.COLUMN_AC_X + " INTEGER, " +
                        DBContract.DataTable.COLUMN_AC_Y + " INTEGER, " +
                        DBContract.DataTable.COLUMN_AC_Z + " INTEGER, " +
                        DBContract.DataTable.BATTERY_LVL + " INTEGER, " +
                        DBContract.DataTable.SYNCED + " INTEGER DEFAULT 0, " +
                        DBContract.DataTable.COLUMN_CHILD_AGE + " INTEGER DEFAULT -1, " +
                        DBContract.DataTable.COLUMN_GENDER + " INTEGER DEFAULT 1, " + //default male
                        DBContract.DataTable.COLUMN_AUTISM_RELATIVES + " INTEGER DEFAULT 0, " +
                        DBContract.DataTable.COLUMN_SESSION_SERIAL + " INTEGER, " +
                        DBContract.DataTable.COLUMN_CHILD_NAME + " TEXT DEFAULT 'No Name Provided', " +
                        DBContract.DataTable.COLUMN_PHONE + " TEXT DEFAULT 'NO Phone Provided', " +
                        DBContract.DataTable.COLUMN_EMAIL + " TEXT DEFAULT 'NO EMAIL Provided', " +
                        DBContract.DataTable.COLUMN_ENCODER_1 + " INTEGER, " +
                        DBContract.DataTable.COLUMN_ENCODER_2 + " INTEGER);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            if (oldVersion == 1) { //for version 2 of database
                sqLiteDatabase.execSQL("ALTER TABLE " + DBContract.DataTable.TABLE_NAME +
                        " ADD COLUMN " + DBContract.DataTable.BATTERY_LVL + " INTEGER DEFAULT 0");
            }
            //for version 3 of database
            sqLiteDatabase.execSQL("ALTER TABLE " + DBContract.DataTable.TABLE_NAME +
                    " ADD COLUMN " + DBContract.DataTable.SYNCED + " INTEGER DEFAULT 0");

            sqLiteDatabase.execSQL("ALTER TABLE " + DBContract.DataTable.TABLE_NAME +
                    " ADD COLUMN " + DBContract.DataTable.COLUMN_CHILD_AGE + " INTEGER DEFAULT -1");

            sqLiteDatabase.execSQL("ALTER TABLE " + DBContract.DataTable.TABLE_NAME +
                    " ADD COLUMN " + DBContract.DataTable.COLUMN_GENDER + " INTEGER DEFAULT 1");

            sqLiteDatabase.execSQL("ALTER TABLE " + DBContract.DataTable.TABLE_NAME +
                    " ADD COLUMN " + DBContract.DataTable.COLUMN_AUTISM_RELATIVES + " INTEGER DEFAULT 0");

            sqLiteDatabase.execSQL("ALTER TABLE " + DBContract.DataTable.TABLE_NAME +
                    " ADD COLUMN " + DBContract.DataTable.COLUMN_EMAIL + " TEXT DEFAULT 'NO EMAIL Provided'");

            sqLiteDatabase.execSQL("ALTER TABLE " + DBContract.DataTable.TABLE_NAME +
                    " ADD COLUMN " + DBContract.DataTable.COLUMN_PHONE + " TEXT DEFAULT 'NO Phone Provided'");
        }
    }


    /**
     * @return ArrayList of {@link net.crowmaster.cardasmarto.entities.HistorySimpleEntity}
     *     which are essentially compressed form of the records in the database which are grouped based on
     *      their session serial
     */
    public ArrayList<HistorySimpleEntity> getSimpleHistoryList() {
        ArrayList<HistorySimpleEntity> result = new ArrayList<HistorySimpleEntity>();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryStr = "SELECT " +
                DBContract.DataTable.COLUMN_SESSION_SERIAL + ", " +
                DBContract.DataTable.COLUMN_CHILD_NAME + ", " +
                "MAX(" + DBContract.DataTable.COLUMN_SERVER_TIME + ") - MIN(" +
                DBContract.DataTable.COLUMN_SERVER_TIME + ") AS " +
                DBContract.DerivedColumns.COLUMN_SEVER_TIME_DURATION + " FROM " +
                DBContract.DataTable.TABLE_NAME + " GROUP BY " +
                DBContract.DataTable.COLUMN_SESSION_SERIAL + " ORDER BY " +
                DBContract.DataTable.COLUMN_SESSION_SERIAL + " DESC"
                ;

        Cursor cur = db.rawQuery(queryStr, null);
        if(cur != null) {
            int[] indices = new int[] {
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_SESSION_SERIAL),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_CHILD_NAME),
                    cur.getColumnIndex(DBContract.DerivedColumns.COLUMN_SEVER_TIME_DURATION)
            };

            cur.moveToFirst();
            while(!cur.isAfterLast()){
                result.add(new HistorySimpleEntity(cur.getLong(indices[0]),
                        cur.getString(indices[1]),
                        cur.getLong(indices[2])));
                cur.moveToNext();
            }
            cur.close();
        }

        db.close();
        return result;
    }

    /**
     * @return ArrayList of {@link net.crowmaster.cardasmarto.entities.HistoryDetailedEntity}
     *     which are essentially the last 100 records in the database used for debug purpose
     */
    public ArrayList<HistoryDetailedEntity> getRecentRecords() {
        ArrayList<HistoryDetailedEntity> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryStr = "SELECT * FROM " +
                DBContract.DataTable.TABLE_NAME + " ORDER BY " +
                DBContract.DataTable.COLUMN_SERVER_TIME + " DESC LIMIT 100"
                ;

        Cursor cur = db.rawQuery(queryStr, null);
        if(cur != null) {
            int[] indices = new int[] {
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_CLIENT_TIME),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_SERVER_TIME),
                    cur.getColumnIndex(DBContract.DataTable.BATTERY_LVL),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_AC_X),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_AC_Y),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_AC_Z),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_ENCODER_1),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_ENCODER_2),
            };

            cur.moveToFirst();
            while(!cur.isAfterLast()){
                result.add(new HistoryDetailedEntity(
                        cur.getLong(indices[0]),
                        cur.getLong(indices[1]),
                        cur.getLong(indices[2]),
                        cur.getLong(indices[3]),
                        cur.getLong(indices[4]),
                        cur.getLong(indices[5]),
                        cur.getLong(indices[6]),
                        cur.getLong(indices[7])
                        ));
                cur.moveToNext();
            }
            cur.close();
        }

        db.close();
        return result;
    }

    /**
     * @return ArrayList of {@link net.crowmaster.cardasmarto.entities.HistorySimpleEntity}
     * This method is used for fetching the new records which are going to be uploaded to the cloud
     */
    public ArrayList<HistoryDetailedEntity> getUnsyncedRecords() {
        ArrayList<HistoryDetailedEntity> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryStr = "SELECT * FROM " +
                DBContract.DataTable.TABLE_NAME + " WHERE " + DBContract.DataTable.SYNCED + "=0 ORDER BY " +
                DBContract.DataTable.COLUMN_PHONE + ", " + DBContract.DataTable.COLUMN_CHILD_NAME  +
                ", " + DBContract.DataTable.COLUMN_SERVER_TIME + " DESC LIMIT 20000"
                ;

        Cursor cur = db.rawQuery(queryStr, null);
        if(cur != null) {
            int[] indices = new int[] {
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_ID),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_CLIENT_TIME),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_SERVER_TIME),
                    cur.getColumnIndex(DBContract.DataTable.BATTERY_LVL),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_AC_X),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_AC_Y),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_AC_Z),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_ENCODER_1),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_ENCODER_2),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_CHILD_NAME),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_PHONE),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_EMAIL),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_AUTISM_RELATIVES),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_GENDER),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_SESSION_SERIAL),
                    cur.getColumnIndex(DBContract.DataTable.COLUMN_CHILD_AGE),
            };

            cur.moveToFirst();
            while(!cur.isAfterLast()){
                result.add(new HistoryDetailedEntity(
                        cur.getLong(indices[0]),
                        cur.getLong(indices[1]),
                        cur.getLong(indices[2]),
                        cur.getLong(indices[3]),
                        cur.getLong(indices[4]),
                        cur.getLong(indices[5]),
                        cur.getLong(indices[6]),
                        cur.getLong(indices[7]),
                        cur.getLong(indices[8]),
                        cur.getString(indices[9]),
                        cur.getString(indices[10]),
                        cur.getString(indices[11]),
                        cur.getInt(indices[12]) == 1,
                        cur.getInt(indices[13]) == 1,
                        cur.getLong(indices[14]),
                        cur.getInt(indices[15])

                ));
                cur.moveToNext();
            }
            cur.close();
        }

        db.close();
        return result;
    }
}
