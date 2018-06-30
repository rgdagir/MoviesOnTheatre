package io.github.rgdagir.flixster.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {

    public final static String TAG = "MovieListMain";

    // base url for loading images
    String imageBaseUrl;
    // the poster size to use when fetching images, part of the url
    String posterSize;
    // the size of the backdrop image
    String backdropImgSize;

    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        //get the image base URL
        imageBaseUrl = images.getString("secure_base_url");
        //get poster size
        JSONArray posterSizes = images.getJSONArray("poster_sizes");
        // use the fourth or w342 as standard
        posterSize = posterSizes.optString(3, "w342");
        //get backdrop sizes
        JSONArray bdSizes = images.getJSONArray("backdrop_sizes");
        // set the size of the backdrop image
        backdropImgSize = bdSizes.optString(1, "w780");
    }

    public String getImageUrl(String size, String path){
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getBackdropImgSize() { return backdropImgSize; }

    public static String getTag() {
        return TAG;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }
}
