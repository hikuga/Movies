package com.example.hitesh.movies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hitesh.movies.data.Favourites;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private String sortKey;
    private Movie currentMovie;
    private float user_ratings;
    private byte[] movie_poster;
    private String trailerPath;
    public DetailActivityFragment() {
        this.sortKey = "popularity";
        this.user_ratings = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void getTrailers(){

    }

    public void downloadTrailers(String trailerID){
        // async task
        DownloadVTask downloadTask = new DownloadVTask();
        downloadTask.execute(trailerID);
    }
    /*
    @Override
    public void finish() {
        Intent intent = new Intent();
        //EditText editText= (EditText) findViewById(R.id.returnValue);
        //String string = editText.getText().toString();
        intent.putExtra("returnkey", this.sortKey);
        getActivity().setResult( Activity.RESULT_OK, intent);
        getActivity().finish();
    }
    */

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();


        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Activity.MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
            String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            int idName = prefs.getInt("idName", 0); //0 is the default value.
            Log.e("checkSharedPreference", "getting values from shared preference name - "+name+" idName - "+idName);
        }
        RatingBar ratingBar = (RatingBar)v.findViewById(R.id.ratingBar);
        Movie movie;
        String themovieDBID = "";
        if( intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movie = intent.getExtras().getParcelable("movieinfo");
            this.currentMovie = movie;
            intent.putExtra("movieid", movie.getId());
            final String getStVal = "currsel";
            this.sortKey = intent.getExtras().getString(getStVal);
            ((TextView)v.findViewById(R.id.title) ).setText(movie.getOriginal_title());
            ((TextView)v.findViewById(R.id.title2) ).setText(movie.getOverview());
            final ImageView imv = (ImageView)v.findViewById(R.id.poster);

            if(intent.getExtras().getInt("islocal") != 0 ) {
                Movie m = getMovieByID(movie.getId());
                Bitmap bitmap = BitmapFactory.decodeByteArray(m.getMovie_poster(), 0, m.getMovie_poster().length);
                Log.e("todel_detailedactivity", "moviePoster length = " + m.getMovie_poster().length);
                imv.setImageBitmap(bitmap);
                ratingBar.setRating(m.getApp_user_ratings());
                themovieDBID = m.getId();
            }
            else {
                themovieDBID = movie.getId();
                Log.e("todel", "loading img data from picaso");
                Picasso.with(getActivity())
                        .load("http://image.tmdb.org/t/p/w185//" + movie.getBackdrop_path())
                        .resize(800, 800)
                        .into(imv, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e("todel", "done loading from picasso");
                                Bitmap photo = ((BitmapDrawable) imv.getDrawable()).getBitmap();
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
                                movie_poster = bos.toByteArray();
                                Log.e("todel", "loaded into local movie_poster");
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }


            intent.putExtra("currsel", this.sortKey);
        }
        Log.e("checkDataTransfer", "activity2::onCreateView recieved " + this.sortKey);

        final String currSel = this.sortKey;

        Button back = (Button)v.findViewById(R.id.button);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent mainActivityIntent = new Intent(getContext(), MainActivity.class);
                mainActivityIntent.putExtra("currsel", currSel);
                mainActivityIntent.putExtra("userrating", user_ratings);
                Log.e("checkratingBar", "sending user ratings as " + user_ratings);
                Log.e("backbutton", "current movie here is " + currentMovie.getTitle());

                startActivity(mainActivityIntent);
            }
        });
        Button trailerButton = (Button) v.findViewById(R.id.trailerButton);
        trailerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e("todel", "playing from " + getContext().getFilesDir() + trailerPath);
                File downloadFile = new File(getContext().getFilesDir() + "/" + trailerPath);
                downloadFile.setReadable(true);
                if (downloadFile.exists())
                    Log.e("todel", "File exists at path: " + downloadFile.getPath());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(downloadFile));   //parse(getContext().getFilesDir()+trailerPath));
                intent.setDataAndType(Uri.fromFile(downloadFile), "video/mp4");
            startActivity(intent);
        }
        });

        // get user ratings
        DownloadWebpageTask downloadTask = new DownloadWebpageTask();
        downloadTask.execute(themovieDBID);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.e("checkratingBar", "setting user ratings to " + rating);
                user_ratings = rating;
                //to-do store this movie in db
                insertMovieDB();
                //movie.setApp_user_ratings( rating);
            }
        });
        return v;
    }

    public Movie getMovieByID(String movieId){
        Cursor c =
                getActivity().getContentResolver().query(Favourites.FavouriteEntry.CONTENT_URI,
                        null,
                        //new String[]{Favourites.FavouriteEntry._ID,Favourites.FavouriteEntry.COLUMN_DESCRIPTION,Favourites.FavouriteEntry.COLUMN_VERSION_NAME},
                        null,
                        //new String[]{Favourites.FavouriteEntry._ID,Favourites.FavouriteEntry.COLUMN_MOVIE_ID, movieId},
                        null,
                        null);
        if (c == null) {
            Log.e("reloadFavs", "cursor is null");
            return null;
        }
        else {
            while(c.moveToNext()) {
                Log.e("todel", "iterating for id = "+c.getString(1));
                if(c.getString(1).equals( movieId)) {
                    Movie m = new Movie();
                    m.setId(c.getString(1));
                    m.setOverview(c.getString(2));
                    m.setTitle(c.getString(3));
                    m.setMovie_poster(c.getBlob(4));
                    m.setVote_average(0.0);
                    m.setVote_count(0);
                    m.setPopularity(c.getDouble(6));
                    m.setApp_user_ratings(c.getFloat(7));
                    //favouriteMovies.add(m);
                    Log.e("todel","found match here for "+movieId);
                    return m;
                }
            }

        }
        Log.e("todel", "found not match for "+movieId);
    return null;
    }

    public void insertMovieDB(){
        ContentValues movieValues = new ContentValues();

        //TODO filll up data in movieValues here
        movieValues.put(Favourites.FavouriteEntry.COLUMN_MOVIE_ID,currentMovie.getId());
        movieValues.put(Favourites.FavouriteEntry.COLUMN_OVERVIEW, currentMovie.getOverview());
        movieValues.put(Favourites.FavouriteEntry.COLUMN_TITLE, currentMovie.getTitle());

        movieValues.put(Favourites.FavouriteEntry.COLUMN_IMAGE, movie_poster);
        movieValues.put(Favourites.FavouriteEntry.COLUMN_POPULARITY, currentMovie.getPopularity());
        movieValues.put(Favourites.FavouriteEntry.COLUMN_USER_RATINGS, this.user_ratings);
        movieValues.put(Favourites.FavouriteEntry.COLUMN_VOTE_COUNT, currentMovie.getVote_count());
        movieValues.put(Favourites.FavouriteEntry.COLUMN_VOTE_AVG, currentMovie.getVote_average());
        movieValues.put(Favourites.FavouriteEntry.COLUMN_BACKDROPPATH, currentMovie.getBackdrop_path());
        movieValues.put(Favourites.FavouriteEntry.COLUMN_ADULT, currentMovie.getAdult());
        movieValues.put(Favourites.FavouriteEntry.COLUMN_RELEASE_DATE, currentMovie.getRelease_date());
        Log.e("todel", "saved movie with ID->"+currentMovie.getId());
        getActivity().getContentResolver().insert(Favourites.FavouriteEntry.CONTENT_URI, movieValues);
    }


    // takes movieId as a the param
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = DownloadWebpageTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params)
        {
            if (params.length == 0) {
                return null;
            }
            Log.e("todel", "calling with params "+params[0]);
           // final String sortBy = params[0]+".desc";
            final String apiKey = getResources().getString(R.string.api_key) ; // "08158a0a95f48cf8eb04e0b22571a8d0";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/"+params[0]+"/videos?";
                //final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                  //      .appendQueryParameter(SORT_PARAM, sortBy)
                        .appendQueryParameter(API_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                return (MovieHelper.getTrailerIds(buffer.toString())).get(0);

            }
            catch(JSONException jexc){
                Log.e(LOG_TAG, "jsonexception", jexc);
                return null;
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            //return null;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String trailerID) {
            Log.e("todel", "extracted trailerId here= "+trailerID);
            downloadTrailers(trailerID);
            }


    }
    private class DownloadVTask extends AsyncTask<String, Void, MovieHelper> {
        private final String LOG_TAG = DownloadVTask.class.getSimpleName();

        @Override
        protected MovieHelper doInBackground(String... movies)
        {
            String movieID = movies[0];
            Log.e("todel", "start Video downloadcalling with params "+movieID);
            final String apiKey = getResources().getString(R.string.api_key) ; // "08158a0a95f48cf8eb04e0b22571a8d0";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                final String MOVIES_BASE_URL =
                        "http://youtube.com/watch?v="+movieID+"?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        //.appendQueryParameter(API_PARAM, apiKey)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //File download = new File(getContext().getFilesDir()+"/"+ movieID);
                //File download = File.createTempFile(movieID + ".mp4", null, getContext().getCacheDir());
                        //+ "/download/");
                /*
                if (!download.exists()) {
                    Log.e("todel","download doesnt exist");
                    download.mkdir();
                }
                */
               // String strDownloaDuRL = download.getName(); //+"/" + movieID;
                trailerPath = movieID;
                //trailerPath = download.getPath();
                Log.e("todel", "start downloading video"+trailerPath);
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                FileOutputStream output =  getContext().openFileOutput(movieID, Context.MODE_PRIVATE);//new FileOutputStream(download);
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    output.write(line.getBytes());
                }
                if (buffer.length() == 0) {
                    return null;
                }
                output.flush();
                output.close();
                inputStream.close();

                //MovieHelper movieData = new MovieHelper(buffer.toString(), params[0]);
                //return movieData;
                Log.e("todel", "done downloading video"+trailerPath);
                return null;
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
           // return null;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(MovieHelper movieData) {
            //uploadImg( movieData.getMoviesListLookup().get(currSelection) );
            Log.e("todel", "done downloading trailer");

        }


    }

}


/// todel
