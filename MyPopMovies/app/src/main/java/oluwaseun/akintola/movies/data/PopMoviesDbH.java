package oluwaseun.akintola.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AKINTOLA OLUWASEUN on 8/1/2017.
 */

public class PopMoviesDbH extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favourites.db";
    private static final int VERSION = 2;

    public PopMoviesDbH(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + PopMoviesContract.MovieEntry.TABLE_NAME + " (" +
                PopMoviesContract.MovieEntry._ID                + " INTEGER PRIMARY KEY, " +
                PopMoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PopMoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                PopMoviesContract.MovieEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                PopMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                PopMoviesContract.MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                PopMoviesContract.MovieEntry.COLUMN_RATING    + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
