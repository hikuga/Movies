package com.example.hitesh.movies.data;

/**
 * Created by hitesh on 11/6/15.
 */
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class FavouritesProvider extends ContentProvider {
    private static final String LOG_TAG = FavouritesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;

    // Codes for the UriMatcher //////
    private static final int FAVOURITE = 100;
    private static final int FAVOURITE_WITH_ID = 200;
    ////////

    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Favourites.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, Favourites.FavouriteEntry.TABLE_FAVOURITES, FAVOURITE);
        matcher.addURI(authority, Favourites.FavouriteEntry.TABLE_FAVOURITES + "/#", FAVOURITE_WITH_ID);

        return matcher;
    }
    @Override
    public boolean onCreate(){
        mOpenHelper = new DBHelper(getContext());

        return true;
    }

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVOURITE:{
                return Favourites.FavouriteEntry.CONTENT_DIR_TYPE;
            }
            case FAVOURITE_WITH_ID:{
                return Favourites.FavouriteEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Flavors selected
            case FAVOURITE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Favourites.FavouriteEntry.TABLE_FAVOURITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on Id selected
            case FAVOURITE_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Favourites.FavouriteEntry.TABLE_FAVOURITES,
                        projection,
                        Favourites.FavouriteEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        Log.e("todel", "values.size here is "+values.size());
        switch (sUriMatcher.match(uri)) {
            case FAVOURITE: {
                long _id = db.insert(Favourites.FavouriteEntry.TABLE_FAVOURITES, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    Log.e("todel", "inserting into db here");
                    returnUri = Favourites.FavouriteEntry.buildFavouritesUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case FAVOURITE:
                numDeleted = db.delete(
                        Favourites.FavouriteEntry.TABLE_FAVOURITES, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        Favourites.FavouriteEntry.TABLE_FAVOURITES + "'");
                break;
            case FAVOURITE_WITH_ID:
                numDeleted = db.delete(Favourites.FavouriteEntry.TABLE_FAVOURITES,
                        Favourites.FavouriteEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        Favourites.FavouriteEntry.TABLE_FAVOURITES + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case FAVOURITE:{
                numUpdated = db.update(Favourites.FavouriteEntry.TABLE_FAVOURITES,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case FAVOURITE_WITH_ID: {
                numUpdated = db.update(Favourites.FavouriteEntry.TABLE_FAVOURITES,
                        contentValues,
                        Favourites.FavouriteEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
