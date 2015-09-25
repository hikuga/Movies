package com.example.hitesh.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private String currSelection;
    private TableLayout mLayout;
    private ArrayList<Movie> currentMovies;

    public MainActivityFragment() {
        this.currSelection = "popularity";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("checkDataTransfer", "onActivityResult:: data = " + data.getStringExtra("sortcr"));
        this.currSelection = data.getStringExtra("sortcr");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("currselection", this.currSelection);
        outState.putParcelableArrayList("currmovies", this.currentMovies);
        Log.e("checksavedinstance", "onSaveInstanceState::saving current movies" + this.currentMovies.size());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        if(savedInstanceState == null) {
            Log.e("checksavedinstance", "onCreate:: savedInstanceState is null");
        }
        if(savedInstanceState != null && !savedInstanceState.containsKey("currmovies"))
            Log.e("checksavedinstance", "onCreate:: savedInstanceState contains no such key here");
        if(savedInstanceState == null || !savedInstanceState.containsKey("currmovies")) {
            Log.e("checksavedinstance", "oncreate::nothing in saved instance");
            this.currentMovies = null;
            //this.currSelection = "popularity";
        }
        else {
            this.currentMovies = savedInstanceState.getParcelableArrayList("currmovies");
            this.currSelection = savedInstanceState.getString("currselection");
            Log.e("checksavedinstance", "oncreate::using values from saved instance");
        }
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("uploadImg", "in menu current selection before = "+this.currSelection);
        switch (item.getItemId()) {
            case R.id.action_popularity:
                if( this.currSelection != "popularity") {
                    this.currSelection = "popularity";
                    reload();
                }
                return true;
            case R.id.action_votes:
                Log.e("uploadImg", "action_votes in menu current selection before = "+this.currSelection);
                if( this.currSelection != "vote_count") {
                    this.currSelection = "vote_count";
                    reload();
                }
                return true;
            case R.id.action_settings:
                // Settings option clicked.
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reload(){
        DownloadWebpageTask downloadTask = new DownloadWebpageTask();
        downloadTask.execute(this.currSelection);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mLayout = (TableLayout) v.findViewById(R.id.tableLayout);
        DownloadWebpageTask downloadTask = new DownloadWebpageTask();
        // downloadTask.ImageDownloader((ImageView) temp);
        if( this.currentMovies == null ) {
            Log.e("checksavedinstance", "onCreateView::nothing in saved instance");
            downloadTask.execute(this.currSelection);
        }
        else {
            Log.e("checksavedinstance", "onCreateView::loading from saved instance");
            this.uploadImg(this.currentMovies);
        }
        return v;
    }

    public void uploadImg(ArrayList<Movie> currentMovies) {
        if (this.currentMovies == null) {
            this.currentMovies = currentMovies;
        }
        Log.e("uploadImg", "uploading img data");
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View parentRow = mLayout.getChildAt(i);
            if (parentRow instanceof TableRow) {
                for (int j = 0; j < ((TableRow) parentRow).getChildCount(); j++) {
                    final Movie movie = currentMovies.get(i * 2 + j);
                    final String currSel = this.currSelection;
                    ImageView imv = (ImageView) ((TableRow) parentRow).getChildAt(j);
                    if (imv instanceof ImageButton) {
                        ((ImageButton) imv).setId(2 * i + j);
                        ((ImageButton) imv).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetailActivity.class);
                                intent.putExtra(Intent.EXTRA_TEXT, movie);
                                intent.putExtra("movieinfo", movie);
                                //intent.putExtra(Intent.EXTRA_TEXT, currSel);
                                intent.putExtra("currsel", currSel);
                                startActivity(intent);
                                //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                            }
                        });
                        Picasso.with(getActivity())
                                .load(currentMovies.get(i * 2 + j).getImageUrl())
                                .resize(575, 430)
                                .into(imv);

                    }
                }
            }
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, MovieHelper> {
        private final String LOG_TAG = DownloadWebpageTask.class.getSimpleName();

        @Override
        protected MovieHelper doInBackground(String... params)
        {
            if (params.length == 0) {
                return null;
            }
            Log.e("uploadImg", "calling with params "+params[0]);
            final String sortBy = params[0]+".desc";
            final String apiKey = "08158a0a95f48cf8eb04e0b22571a8d0";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";

                final String MOVIE_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=08158a0a95f48cf8eb04e0b22571a8d0";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortBy)
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
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MovieHelper movieData = new MovieHelper(buffer.toString(), params[0]);
                return movieData;
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
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
        protected void onPostExecute(MovieHelper movieData) {
            uploadImg( movieData.getMoviesListLookup().get(currSelection) );
        }


    }
}
