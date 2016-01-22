package com.ganwal.nanodegree.popularMovie;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


/**
 * A detail fragment containing a movie detail view.
 */
public class DetailFragment extends Fragment {

    public static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if(intent != null ){
            Movie movie = intent.getParcelableExtra(MainActivity.EXTRA_MOVIE_ID);
            //String movieID = intent.getStringExtra(MainActivity.EXTRA_MOVIE_ID);
            Bundle arguments = getArguments();
            if (arguments != null) {
                movie = arguments.getParcelable(MainActivity.EXTRA_MOVIE_ID);
            }
            if(movie != null) {
                //now show all the movie attributes on the details page
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getTitle());
                ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_image);
                //use Picasso library to load images
                Picasso.with(getActivity())
                        .load(HelperUtility.buildPosterImageURL(movie.getThumbnailName()))
                        .into(imageView);
                ((TextView) rootView.findViewById(R.id.user_rating)).setText(movie.getUserRating());
                ((TextView) rootView.findViewById(R.id.release_date)).setText(
                        HelperUtility.convertDatetoString(movie.getReleaseDate()));
                ((TextView) rootView.findViewById(R.id.synopsis)).setText(movie.getSynopsis());
                //hide select a movie text view for two-pane layouts
                ((TextView) rootView.findViewById(R.id.no_movie_selected)).setVisibility(View.GONE);
            } else {
                //In two pane layout when no movie is selected, show movie selection text view
                // but don't show the movie ones
                ((TextView) rootView.findViewById(R.id.no_movie_selected)).setVisibility(View.VISIBLE);
                ((TextView) rootView.findViewById(R.id.movie_title)).setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.user_rating_label)).setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.release_date_label)).setVisibility(View.GONE);
            }

        }

        return rootView;
    }
}