package oluwaseun.akintola.movies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AKINTOLA OLUWASEUN on 8/1/2017.
 */

public class PopMoviesContract {
    public static final String AUTHORITY = "oluwaseun.akintola.movies";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVOURITES = "favourites";

    public static class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_FAVOURITES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MOVIE_ID = "unique_id";
        public static final String COLUMN_POSTER = "poster_url";
        public static final String COLUMN_RATING = "voter_average";
    }
}
