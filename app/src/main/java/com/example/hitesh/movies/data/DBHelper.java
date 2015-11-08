package com.example.hitesh.movies.data;

/**
 * Created by hitesh on 11/4/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 10;

    static final String DATABASE_NAME = "movies.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITES = "CREATE TABLE " +
                Favourites.FavouriteEntry.TABLE_FAVOURITES + "(" + Favourites.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Favourites.FavouriteEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                Favourites.FavouriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                Favourites.FavouriteEntry.COLUMN_TITLE + " INTEGER NOT NULL, " +
                Favourites.FavouriteEntry.COLUMN_IMAGE + " BLOB, " +
                Favourites.FavouriteEntry.COLUMN_TRAILER + " TEXT, "+
                Favourites.FavouriteEntry.COLUMN_POPULARITY + " INTEGER NOT NULL, " +
                Favourites.FavouriteEntry.COLUMN_USER_RATINGS + " REAL, " +
                Favourites.FavouriteEntry.COLUMN_ADULT + " TEXT, " +
                Favourites.FavouriteEntry.COLUMN_VOTE_COUNT + " INTEGER, " +
                Favourites.FavouriteEntry.COLUMN_VOTE_AVG + " REAL,  " +
                Favourites.FavouriteEntry.COLUMN_BACKDROPPATH + " TEXT, " +
                Favourites.FavouriteEntry.COLUMN_RELEASE_DATE + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Favourites.FavouriteEntry.TABLE_FAVOURITES);
        onCreate(sqLiteDatabase);
    }
}
