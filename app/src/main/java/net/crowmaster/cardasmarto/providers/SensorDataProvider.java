package net.crowmaster.cardasmarto.providers;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import net.crowmaster.cardasmarto.contracts.DBContract;
import net.crowmaster.cardasmarto.utils.DBHelper;

import java.util.ArrayList;

/**
 * Created by root on 7/14/16.
 * This class is used for updating the chart in the {@link net.crowmaster.cardasmarto.fragments.TestPerformerFragment}
 * and storing the incoming data from the car to the database at
 * {@link net.crowmaster.cardasmarto.services.DataCollectorService#OnResponse}
 */


public class SensorDataProvider extends ContentProvider {
    private DBHelper db;
    public static final String AUTHORITY = "net.crowmaster.cardasmarto.SensorDataProvider";
    private static final String TABLE = DBContract.DataTable.TABLE_NAME;
    private static final int DatID = 201;
    private static final UriMatcher MATCHER;
    public static final class Constants implements BaseColumns {
        public static final Uri SensorURL =Uri.parse("content://" + AUTHORITY + "/"+TABLE);
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

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase mdb = db.getWritableDatabase();

        mdb.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            mdb.setTransactionSuccessful();
            return results;
        } finally {
            mdb.endTransaction();
        }
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
                    ContentUris.withAppendedId(Constants.SensorURL,
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
        String whereStr = where == null? DBContract.DataTable.COLUMN_ID + "= ?" : where;
        int count=
                db.getWritableDatabase()
                        .update(TABLE, values, whereStr, whereArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return(count);
    }
}
