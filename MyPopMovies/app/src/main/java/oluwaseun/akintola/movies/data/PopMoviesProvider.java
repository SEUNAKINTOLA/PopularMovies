package oluwaseun.akintola.movies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import oluwaseun.akintola.movies.R;

/**
 * Created by AKINTOLA OLUWASEUN on 8/1/2017.
 */

public class PopMoviesProvider extends ContentProvider {
    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;
    private Context context;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopMoviesContract.AUTHORITY, PopMoviesContract.PATH_FAVOURITES, TASKS);
        uriMatcher.addURI(PopMoviesContract.AUTHORITY, PopMoviesContract.PATH_FAVOURITES + "/#", TASK_WITH_ID);

        return uriMatcher;
    }

    private PopMoviesDbH movieDbHelper;


    @Override
    public boolean onCreate() {
        context = getContext();
        movieDbHelper = new PopMoviesDbH(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {


        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case TASKS:
                retCursor =  db.query(PopMoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // return Cursor
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TASKS:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(PopMoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(PopMoviesContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert new row into Uri: " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted;
        // starts as 0

        switch (match) {
            case TASKS:
                tasksDeleted = db.delete(PopMoviesContract.MovieEntry.TABLE_NAME, "unique_id=" + selection, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            Toast.makeText(context, context.getString(R.string.unfavourite), Toast.LENGTH_SHORT).show();
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
