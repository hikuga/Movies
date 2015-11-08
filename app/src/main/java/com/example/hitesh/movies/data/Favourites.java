package com.example.hitesh.movies.data;

/**
 * Created by hitesh on 11/6/15.
 */
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

// Contract class for movie favourites
public class Favourites{

    public static final String CONTENT_AUTHORITY = "com.example.hitesh.movies.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class FavouriteEntry implements BaseColumns{
        // table name
        public static final String TABLE_FAVOURITES = "favourite";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_MOVIE_ID = "movieid";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TRAILER = "trailer";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_USER_RATINGS = "userratings";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_BACKDROPPATH = "backdrop";
        public static final String COLUMN_VOTE_AVG = "voteavg";
        public static final String COLUMN_VOTE_COUNT = "votecount";
        public static final String COLUMN_RELEASE_DATE = "release";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_FAVOURITES).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_FAVOURITES;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_FAVOURITES;

        // for building URIs on insertion
        public static Uri buildFavouritesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}