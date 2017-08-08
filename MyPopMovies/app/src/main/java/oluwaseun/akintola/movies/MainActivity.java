package oluwaseun.akintola.movies;
/**
 * Created by AKINTOLA OLUWASEUN on 4/24/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import oluwaseun.akintola.movies.data.PopMoviesContract;
import oluwaseun.akintola.movies.domain.MovieBean;

public class MainActivity extends AppCompatActivity implements
        PopMoviesAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TASK_LOADER_ID = 0;
    private final static String SORT_ORDER = "pref_sort_order";
    private RecyclerView mMovieList;
    private PopMoviesAdapter adapter;
    private String mSortOrder;
    private ProgressBar mProgressBar;

    static String MOVIE_URL;
    static String PARAM_API;
    static String MOVIES_KEY;
    List<MovieBean.AllResults> dd;
   private List<MovieBean.AllResults> movie_list;
    private static final String TAG = MainActivity.class.getSimpleName();
    String seen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSortOrder = preferences.getString(SORT_ORDER, "popular");

        movie_list = new ArrayList<>();
        mMovieList = (RecyclerView) findViewById(R.id.rv_movies_list);
        adapter = new PopMoviesAdapter(movie_list, MainActivity.this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mMovieList.setLayoutManager(new GridLayoutManager(this, 2));
        else
            mMovieList.setLayoutManager(new GridLayoutManager(this, 3));
        mMovieList.setHasFixedSize(true);

        if (mSortOrder.equals(MovieDetails.FAVORITES)) {
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        } else {
            if (isNetworkAvailable()) {
                mProgressBar.setVisibility(View.VISIBLE);
                new DownloadTask().execute("new");
            } else
                setNoConnectionLayout();
        }
    }

    private void setNoConnectionLayout() {
        ((findViewById(R.id.rv_no_conn))).setVisibility(View.VISIBLE);
        ((findViewById(R.id.rv_refresh))).setVisibility(View.VISIBLE);
    }

    public void refresh(View view) {
        if (isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            ((findViewById(R.id.rv_no_conn))).setVisibility(View.INVISIBLE);
            ((findViewById(R.id.rv_refresh))).setVisibility(View.INVISIBLE);
            new DownloadTask().execute("new");
        }
    }

    class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            try {
                URL url = buildUrl(mSortOrder, getBaseContext());
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(7000);
                connection.setReadTimeout(7000);
                if (connection.getResponseCode() == 200) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    InputStream inputStream = connection.getInputStream();
                    int len = 0;
                    byte[] b = new byte[1024];
                    while ((len = inputStream.read(b)) != -1) {
                        outputStream.write(b, 0, len);
                    }
                    seen = new String(outputStream.toByteArray());
                    return new String(outputStream.toByteArray());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            mProgressBar.setVisibility(View.GONE);
            if (seen != null) {
                    movie_list = null;
                PopMoviesAdapter.loadFromSDCard = false;

                MovieBean models = JSON.parseObject(seen, MovieBean.class);
                List<MovieBean.AllResults> mod = models.getResults();

                movie_list = mod;
                Log.d(TAG, String.valueOf(movie_list));

                adapter = new PopMoviesAdapter( movie_list, MainActivity.this);
                mMovieList.setAdapter(adapter);

            } else {
                Toast.makeText(MainActivity.this, "Couldn't get movies", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, MovieDetails.class);
        intent.putExtra("position", position);
        intent.putExtra("Details", seen);
        startActivity(intent);
    }
    public static URL buildUrl(String path, Context context) {
        MOVIE_URL = context.getString(R.string.movie_base_url);
        PARAM_API = context.getString(R.string.api_param);
        MOVIES_KEY = context.getString(R.string.my_movies_key);

        Uri builtUri = Uri.parse(MOVIE_URL).buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(PARAM_API, MOVIES_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    private boolean isNetworkAvailable(){
        ConnectivityManager connection = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo NetworkInfo = connection.getActiveNetworkInfo();
        return NetworkInfo != null && NetworkInfo.isConnected();
    }


    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    if (key.equals(SORT_ORDER)) {
                        mSortOrder = sharedPreferences.getString(SORT_ORDER, "popular");
                        if (mSortOrder.equals(MovieDetails.FAVORITES))
                            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, MainActivity.this);
                        else{
                        if (isNetworkAvailable())
                            new DownloadTask().execute("new");
                        }
                    }
                }
            };

    @Override
    protected void onResume() {
        super.onResume();

        if (mSortOrder.equals(MovieDetails.FAVORITES))
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor movieData = null;

            @Override
            protected void onStartLoading() {
                if (movieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(movieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(PopMoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            PopMoviesContract.MovieEntry.COLUMN_MOVIE_ID);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                movieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null) {
            int Title = data.getColumnIndex(PopMoviesContract.MovieEntry.COLUMN_TITLE);
            int Overview = data.getColumnIndex(PopMoviesContract.MovieEntry.COLUMN_OVERVIEW);
            int Date = data.getColumnIndex(PopMoviesContract.MovieEntry.COLUMN_DATE);
            int ID = data.getColumnIndex(PopMoviesContract.MovieEntry.COLUMN_MOVIE_ID);
            int Poster = data.getColumnIndex(PopMoviesContract.MovieEntry.COLUMN_POSTER);
            int Rating = data.getColumnIndex(PopMoviesContract.MovieEntry.COLUMN_RATING);

            String aa ="{\"page\":1,\"total_results\":6682,\"total_pages\":335,\"results\":[";
            String oog ="";
            for (int i = 0; i < data.getCount(); i++) {
                data.moveToPosition(i);
                String id = data.getString(ID);
                String title = data.getString(Title);
                String rating = data.getString(Rating);
                String date = data.getString(Date);
                String overview = data.getString(Overview);
                String poster = data.getString(Poster);
                oog = "{\"id\":"+id+",\"vote_average\":"+rating+",\"poster_path\":\"" + poster + "\",\"original_title\":\""+title+"\",\"overview\":\""+overview+"\",\"release_date\":\""+date+"\"},";
                    aa= aa+oog;
            }
            aa = aa+"]}";
            Log.d(TAG, aa);
            seen= aa;
            MovieBean models = JSON.parseObject(aa, MovieBean.class);
            List<MovieBean.AllResults> mod = models.getResults();
            dd = mod;
         //      data.close();
            PopMoviesAdapter.loadFromSDCard = true;

            adapter = new PopMoviesAdapter( dd, MainActivity.this);
            mMovieList.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
