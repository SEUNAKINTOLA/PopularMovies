package oluwaseun.akintola.movies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import oluwaseun.akintola.movies.data.PopMoviesContract;
import oluwaseun.akintola.movies.domain.MovieBean;
import oluwaseun.akintola.movies.domain.ReviewBean;
import oluwaseun.akintola.movies.domain.TrailerBean;

import static com.android.volley.VolleyLog.TAG;
import static oluwaseun.akintola.movies.MainActivity.buildUrl;

/**
 * Created by AKINTOLA OLUWASEUN on 8/7/2017.
 */

public class MovieDetails extends Activity implements MovieReviewAdapter.ReviewItemListener,  MovieTrailerAdapter.TrailerItemListener{
    private TextView originalTitle;
    private TextView overview;
    private TextView releasedDate;
    int pos;
    private TextView voteAverage;
    private ImageView poster;
    private ImageView backdrop;
    private List<MovieBean.AllResults> movieDetails;
    private final static String BASE_URL = "http://image.tmdb.org/t/p/w185//";

    private int movieIsFavourite = android.R.drawable.btn_star_big_on;
    private int movieIsNotFavourite = android.R.drawable.btn_star_big_off;
    private boolean isFavourite;
    private SharedPreferences preferences;
    public static final String FOLDER = "Posters";
    public static final String FAVORITES = "favourites";



    private RecyclerView recyclerView;
    private List<ReviewBean.AllResults> list;
    private MovieReviewAdapter adapter;
    private String Number_of_Reviews;
    URL reviewUrl;
    public  String seen2, seen3;



    private MovieTrailerAdapter Traileradapter;
    private RecyclerView TrailerrecyclerView;
    private List<TrailerBean.AllResults> Trailerlist;
    private URL Trailerurl;


    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.details_activity);
        preferences = getSharedPreferences(FAVORITES, MODE_PRIVATE);

        originalTitle = (TextView) findViewById(R.id.title);
        overview  = (TextView) findViewById(R.id.overview);
        releasedDate  = (TextView) findViewById(R.id.date);

        voteAverage  = (TextView) findViewById(R.id.ratings);
        poster  = (ImageView) findViewById(R.id.poster);
        backdrop = (ImageView)findViewById(R.id.backdrop);

        Intent intent = getIntent();
        String seen =  intent.getStringExtra("Details");

        MovieBean models = JSON.parseObject(seen, MovieBean.class);
        List<MovieBean.AllResults> moveDetails = models.getResults();

        pos =  intent.getIntExtra("position", 0);

        movieDetails = moveDetails;

        isFavourite = preferences.getBoolean(movieDetails.get(pos).getId(), false);
        setFavouriteIcon();

        setDetails();

        setReviews();
        setTrailer();
    }



    public void setReviews(){


        recyclerView = (RecyclerView)findViewById(R.id.rv_review);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        adapter = new MovieReviewAdapter(this, list, this);
        recyclerView.setAdapter(adapter);

        String id = movieDetails.get(pos).getId();

        reviewUrl = buildUrl(id + getString(R.string.review_path), this);
        new DownloadTask().execute("new");
        return;
    }




    public void setTrailer(){



        TrailerrecyclerView = (RecyclerView)findViewById(R.id.rv_movies_list);
        TrailerrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        TrailerrecyclerView.setHasFixedSize(true);

        Trailerlist = new ArrayList<>();
        Traileradapter = new MovieTrailerAdapter(this, Trailerlist, this);
        TrailerrecyclerView.setAdapter(Traileradapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String id = movieDetails.get(pos).getId();

        Trailerurl = buildUrl(id + getString(R.string.video_path), this);

        new DownloadTrailerTask().execute("new");
        return;
    }

    @Override
    public void onClick(int index) {
        Intent detailIntent = new Intent(this, TrailerViewActivity.class);
        detailIntent.putExtra("index", index);
        detailIntent.putExtra("seen3", seen3);
        startActivity(detailIntent);
    }


    class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) reviewUrl.openConnection();
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
                    seen2 = new String(outputStream.toByteArray());
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
            if (seen2 != null) {
                list.clear();

                ReviewBean models = JSON.parseObject(seen2, ReviewBean.class);
                List<ReviewBean.AllResults> mod = models.getResults();

                list = mod;
                Log.d(TAG, String.valueOf(list));
                Number_of_Reviews = models.getTotal_results();
                ((TextView)findViewById(R.id.reviewTitle))
                        .setText("Number of Comments(" + Number_of_Reviews + ")");
                adapter.refreshList(list);


            } else {
                Toast.makeText(MovieDetails.this, "Error getting movies", Toast.LENGTH_SHORT).show();

            }
        }
    }




    class DownloadTrailerTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) Trailerurl.openConnection();
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
                    seen3 = new String(outputStream.toByteArray());
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
            if (seen3 != null) {
                Trailerlist.clear();

                TrailerBean models = JSON.parseObject(seen3, TrailerBean.class);
                List<TrailerBean.AllResults> mod = models.getResults();

                Trailerlist = mod;
                Log.d(TAG, String.valueOf(list));
                Traileradapter.refreshList(Trailerlist);


            } else {
                Toast.makeText(MovieDetails.this, "Couldn't get movies", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void setDetails() {
        Typeface TitleFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        originalTitle.setText(movieDetails.get(pos).getOriginal_title());
        originalTitle.setTypeface(TitleFont);

        Typeface overViewFont = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Light.ttf");
        overview.setText(movieDetails.get(pos).getOverview());
        overview.setTypeface(overViewFont);

        String year = movieDetails.get(pos).getRelease_date().substring(0, 4);
        releasedDate.setText("Year: " + year);
        voteAverage.setText(String.valueOf("Rating: " + movieDetails.get(pos).getVote_average()));

        if (PopMoviesAdapter.loadFromSDCard){
            Bitmap bitmap = BitmapFactory.decodeFile(movieDetails.get(pos).getPoster_path());
            poster.setImageBitmap(bitmap);
        }
        else {
            Picasso.with(getBaseContext()).load(BASE_URL+movieDetails.get(pos).getPoster_path())
                    .networkPolicy(NetworkPolicy.OFFLINE).resize(300, 400).into(poster);

            Picasso.with(getBaseContext()).load(BASE_URL+movieDetails.get(pos).getPoster_path())
                    .into(backdrop);
        }
        return;
    }

    public void toggleFavouriteIcon(View view){
        if (!isFavourite)
        {
            ((ImageButton)findViewById(R.id.favoriteLogo))
                    .setImageResource(movieIsFavourite);
            isFavourite = true;
            saveFavouriteSettings();
            saveFavouriteToDb();
        }
        else
        {
            ((ImageButton)findViewById(R.id.favoriteLogo))
                    .setImageResource(movieIsNotFavourite);
            isFavourite = false;
            saveFavouriteSettings();
            Uri uri = PopMoviesContract.MovieEntry.CONTENT_URI;
            getContentResolver().delete(uri, movieDetails.get(pos).getId(), null);
        }
    }

    private void saveFavouriteToDb() {
        String posterSDCardLocation = saveImage();
        ContentValues values = new ContentValues();
        values.put(PopMoviesContract.MovieEntry.COLUMN_TITLE, movieDetails.get(pos).getOriginal_title());
        values.put(PopMoviesContract.MovieEntry.COLUMN_OVERVIEW, movieDetails.get(pos).getOverview());
        values.put(PopMoviesContract.MovieEntry.COLUMN_DATE, movieDetails.get(pos).getRelease_date());
        values.put(PopMoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieDetails.get(pos).getId());
        values.put(PopMoviesContract.MovieEntry.COLUMN_POSTER, posterSDCardLocation);
        values.put(PopMoviesContract.MovieEntry.COLUMN_RATING, movieDetails.get(pos).getVote_average());

        Uri uri = getContentResolver().insert(PopMoviesContract.MovieEntry.CONTENT_URI, values);

        if(uri != null) {
            Toast.makeText(getBaseContext(), getString(R.string.favourite), Toast.LENGTH_LONG).show();
        }
    }

    private String saveImage() {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File imageDir = new File(root, FOLDER);
        imageDir.mkdirs();
        String poster = "poster-" + movieDetails.get(pos).getId() + ".png";
        File file = new File(imageDir, poster);
        if (file.exists())
            file.delete();
        try {
            Bitmap posterBitmap = ((BitmapDrawable)backdrop.getDrawable()).getBitmap();
            FileOutputStream out = new FileOutputStream(file);
            posterBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private void saveFavouriteSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        if (isFavourite){
            editor.putBoolean(movieDetails.get(pos).getId(), isFavourite);
            editor.commit();
        }
        else {
            editor.remove(movieDetails.get(pos).getId());
            editor.commit();
        }

    }

    public void setFavouriteIcon(){
        if (isFavourite) {
            ((ImageButton) findViewById(R.id.favoriteLogo))
                    .setImageResource(movieIsFavourite);
        }
        else
        {
            ((ImageButton)findViewById(R.id.favoriteLogo))
                    .setImageResource(movieIsNotFavourite);
        }
    }


    @Override
    public void onClick(String id, String comment) {
        Intent detailIntent = new Intent(this, ReviewDetailActivity.class);
        detailIntent.putExtra("_id", id);
        detailIntent.putExtra("review", comment);
        startActivity(detailIntent);
    }
}
