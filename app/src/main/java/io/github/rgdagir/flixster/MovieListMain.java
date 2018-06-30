package io.github.rgdagir.flixster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.rgdagir.flixster.models.Config;
import io.github.rgdagir.flixster.models.Movie;

public class MovieListMain extends AppCompatActivity {

    // constants
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String API_KEY_PARAM = "api_key";
    public final static String TAG = "MovieListMain";

    // instance fields - fields that only have values associated with a specific instance of MovieList
    AsyncHttpClient client;

    // movies on theatre rn
    ArrayList<Movie> movies;

    // the recycler view
    RecyclerView rvMovies;

    // the adapter wired to recycler view
    MovieAdapter adapter;

    // image config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list_main);

        // initialize client
        client = new AsyncHttpClient();
        // initialize the movie list
        movies = new ArrayList<>();
        // initialize the adapter -- movies array cannot be reinitialized after this point
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        getConfig();

    }

    // get the movie list from the API
    private void getNowPlaying(){
        // build URL
        String url = API_BASE_URL + "/movie/now_playing";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // put results in movie list
                    JSONArray results = response.getJSONArray("results");
                    // walk through array and add each movie in the array to our list
                    for (int i = 0; i < results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    // log the successful operation
                    Log.i(TAG, String.format("Successfully logged %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e , true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("1Failed getting movie list form now_playing endpoint", throwable, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                logError("2Failed getting movie list form now_playing endpoint", throwable, true);
                Log.e(TAG, throwable.getMessage());
                Log.e(TAG, errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                logError("3Failed getting movie list form now_playing endpoint", throwable, true);
            }
        });
    }

    // get configurations for app from the API
    private void getConfig() {
        // build URL
        String url = API_BASE_URL + "/configuration";
        // set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configs with image url %s, poster size %s and backdrop image size %s", config.getImageBaseUrl(), config.getPosterSize(), config.getBackdropImgSize()));
                    // pass config object to adapter
                    adapter.setConfig(config);
                    // get the now playing movies
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration.", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("1Failed getting configs", throwable, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                logError("2Failed getting configs", throwable, true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                logError("3Failed getting configs", throwable, true);

            }
        });

    }

    // handling errors and notifying user
    public void logError(String message, Throwable error, boolean alertUser){
        Log.e(TAG, message, error);
        //alert user with a toast
        if (alertUser) {
            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        }
    }
}
