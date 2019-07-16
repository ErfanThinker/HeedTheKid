package net.crowmaster.cardasmarto.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import net.crowmaster.cardasmarto.contracts.DBContract;
import net.crowmaster.cardasmarto.utils.DBHelper;

public class DebugListContentProvider extends ContentProvider {

    private DBHelper db;
    public static final String AUTHORITY = "net.crowmaster.cardasmarto.DebugListContentProvider";
    private static final String TABLE = DBContract.DataTable.TABLE_NAME;
    private static final int DatID = 202;
    private static final UriMatcher MATCHER;
    public static final class Constants implements BaseColumns {
        public static final Uri DebugURL =Uri.parse("content://" + AUTHORITY + "/"+TABLE);
    }

    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(AUTHORITY,
                TABLE, DatID);
    }


    @Override
    public boolean onCreate() {
        db = new DBHelper(getContext());
        return (db != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE);
        Cursor c=
                qb.query(db.getReadableDatabase(), projection, selection,
                        selectionArgs, null, null, sortOrder);

        if(getContext().getContentResolver() != null) {
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return (c);
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID=
                db.getWritableDatabase().insert(TABLE, null, contentValues);

        if (rowID > 0) {
            Uri url=
                    ContentUris.withAppendedId(SensorDataProvider.Constants.SensorURL,
                            rowID);
            getContext().getContentResolver().notifyChange(url, null);

            return(url);
        }

        try {
            throw new SQLException("Failed to insert row into " + uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int count = db.getWritableDatabase().delete(TABLE, s, strings);

        getContext().getContentResolver().notifyChange(uri, null);

        return(count);

    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {
        int count=
                db.getWritableDatabase()
                        .update(TABLE, values, where, whereArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return(count);
    }
}
