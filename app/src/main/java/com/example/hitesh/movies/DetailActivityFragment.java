package com.example.hitesh.movies;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private String sortKey;

    public DetailActivityFragment() {
        this.sortKey = "popularity";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if( intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            Movie movie = intent.getExtras().getParcelable("movieinfo");
            final String getStVal = "currsel";
            this.sortKey = intent.getExtras().getString(getStVal);
            ((TextView)v.findViewById(R.id.title) ).setText(movie.getOriginal_title());
            ((TextView)v.findViewById(R.id.title2) ).setText(movie.getOverview());
            ImageView imv = (ImageView)v.findViewById(R.id.poster);
            Picasso.with(getActivity())
                    .load( "http://image.tmdb.org/t/p/w185//" + movie.getBackdrop_path())
                    .resize(800, 800)
                    .into(imv);
        }

        Log.e("checkDataTransfer", "activity2::onCreateView recieved " + this.sortKey);
        return v;
    }



}
