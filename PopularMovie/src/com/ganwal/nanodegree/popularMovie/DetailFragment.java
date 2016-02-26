package com.ganwal.nanodegree.popularMovie;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.RemoteException;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.ganwal.nanodegree.popularMovie.data.ProviderContract;
import com.squareup.picasso.Picasso;
import org.json.JSONException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A detail fragment containing a Movie detail view.
 */
public class DetailFragment extends Fragment {

    public static final String LOG_TAG = DetailFragment.class.getSimpleName();

    LinearLayout mTrailersLayout;
    LinearLayout mReviewsLayout;
    Movie mMovie;
    ShareActionProvider mShareActionProvider;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            //User is in phone view
            mMovie = intent.getParcelableExtra(MainActivity.EXTRA_MOVIE_ID);
            //String movieID = intent.getStringExtra(MainActivity.EXTRA_MOVIE_ID);
            //User is in tablet view
            Bundle arguments = getArguments();
            if (arguments != null) {
                mMovie = arguments.getParcelable(MainActivity.EXTRA_MOVIE_ID);
            }
            if (mMovie != null) {
                //now show all the movie attributes on the details page
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(mMovie.getTitle());
                final ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_image);
                //use Picasso library to load images
                Picasso.with(getActivity())
                        .load(HelperUtility.buildPosterImageURL(mMovie.getThumbnailName()))
                        .into(imageView);
                ((TextView) rootView.findViewById(R.id.user_rating)).setText(mMovie.getUserRating());
                ((TextView) rootView.findViewById(R.id.release_date)).setText(
                        HelperUtility.convertDatetoString(mMovie.getReleaseDate()));
                ((TextView) rootView.findViewById(R.id.synopsis)).setText(mMovie.getSynopsis());
                //favorite button
                //Change button color if movie has been already marked favorite,
                // which means we have it in local sqllite db
                //getActivity().getContentResolver().
                Button favoriteButton = (Button) rootView.findViewById(R.id.favorite);
                //first see if this movie has already been marked favorite
                Movie favoriteMovie = getLocalMovieById(mMovie.getMovieDBId());
                if(favoriteMovie != null) {
                    Log.d(LOG_TAG, "Found in DB, changing color of button");
                    //movie is already marked as favorite
                    favoriteButton.setBackgroundColor(getResources().getColor(R.color.color_accent));
                }
                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d(LOG_TAG, "Clicked the favorite button");
                        Log.d(LOG_TAG, "Movie ID:"+ mMovie.getId());
                        Movie innerFavoriteMovie = getLocalMovieById(mMovie.getMovieDBId());
                        if(innerFavoriteMovie != null) {
                            Log.d(LOG_TAG, "Found movie in local DB");
                            //movie is already marked as favorite, unmarking it now and deleting from DB
                            //movie reviews and videos will also be deleted with it
                            v.setBackgroundColor(getResources().getColor(R.color.button_material_light));
                            int numRowsDeleted = getActivity().getContentResolver().delete(
                                    ProviderContract.MovieEntry.CONTENT_URI,
                                    ProviderContract.MovieEntry._ID + " = ?",
                                    new String[]{innerFavoriteMovie.getId()+""});
                            Log.d(LOG_TAG, "No of rows deleted is:" + numRowsDeleted);
                        } else {
                            Log.d(LOG_TAG, "Not Found in DB, inserting movie");
                            //also get the thumbnail image and store in db for offline viewing
                            if(imageView.getDrawable() != null) {
                                Bitmap bm = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                Log.d(LOG_TAG, "The thumbnail image byte array:" + stream.toByteArray());
                                mMovie.setThumbnailImage(stream.toByteArray());
                            }
                            insertMovie();
                            v.setBackgroundColor(getResources().getColor(R.color.color_accent));
                        }
                    }
                });

                //hide select a movie text view for two-pane layouts
                ((TextView) rootView.findViewById(R.id.no_movie_selected)).setVisibility(View.GONE);
                //now get the Layout to show trailers
                mTrailersLayout = (LinearLayout) rootView.findViewById(R.id.trailers_view);
                mReviewsLayout = (LinearLayout) rootView.findViewById(R.id.reviews_view);

            } else {
                //In two pane layout when no movie is selected, show movie selection text view
                // but don't show the movie ones
                (rootView.findViewById(R.id.no_movie_selected)).setVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.movie_title)).setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.user_rating_label)).setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.release_date_label)).setVisibility(View.GONE);
                ((LinearLayout) rootView.findViewById(R.id.trailers_view)).setVisibility(View.GONE);
                ((LinearLayout) rootView.findViewById(R.id.reviews_view)).setVisibility(View.GONE);
                ((Button) rootView.findViewById(R.id.favorite)).setVisibility(View.GONE);
            }

        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movie_detail_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider)menuItem.getActionProvider();
        setShareIntent();

    }
    @Override
    public void onStart() {
        super.onStart();
        fetchMovieVideos();
        fetchMovieReviews();
    }

    private void fetchMovieVideos() {
        //first make sure we have the api_key defined in the string resource file
        String apiKey = getString(R.string.api_key);
        if (HelperUtility.isStringNullOREmpty(apiKey)) {
            Log.e(LOG_TAG, "***** ERROR - No API author found. Define api_key in string resource file ***");
        } else {
            if(mMovie != null) {
                String urlStr = new String(HelperUtility.MOVIE_DB_MOVIE_VIDEO_URL)
                        .replaceAll(":1", mMovie.getMovieDBId())
                        .replaceAll(":2", getString(R.string.api_key));
                Log.d(LOG_TAG, "Loading trailer for Movie:"+ mMovie.getTitle());
                MovieTrailerFetchTask movieTrailerFetchTask = new MovieTrailerFetchTask();
                movieTrailerFetchTask.execute(urlStr);
            }
        }
    }

    private void fetchMovieReviews() {
        //first make sure we have the api_key defined in the string resource file
        String apiKey = getString(R.string.api_key);
        if (HelperUtility.isStringNullOREmpty(apiKey)) {
            Log.e(LOG_TAG, "***** ERROR - No API author found. Define api_key in string resource file ***");
        } else {
            if(mMovie != null) {
                String urlStr = new String(HelperUtility.MOVIE_DB_REVIEWS_VIDEO_URL)
                        .replaceAll(":1", mMovie.getMovieDBId())
                        .replaceAll(":2", getString(R.string.api_key));
                Log.d(LOG_TAG, "Loading reviews for Movie:"+ mMovie.getTitle());
                MovieReviewFetchTask task = new MovieReviewFetchTask();
                task.execute(urlStr);
            }
        }
    }

    private Movie getLocalMovieById(String movieDBId) {
        Movie favMovie = null;
        Cursor cursor = getActivity().getContentResolver().query(
                ProviderContract.MovieEntry.CONTENT_URI,
                ProviderContract.MovieEntry.projections,
                ProviderContract.MovieEntry.COL_MOVIE_DB_ID + " = ?",
                new String[]{movieDBId},
                null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                favMovie = ProviderContract.MovieEntry.cursorToMovie(cursor);
                Log.d(LOG_TAG, "Found Movie:"+favMovie);
                cursor.moveToNext();
            }
            cursor.close();

            //now get movie trailers
            cursor = getActivity().getContentResolver().query(
                    ProviderContract.MovieVideoEntry.CONTENT_URI,
                    ProviderContract.MovieVideoEntry.projections,
                    ProviderContract.MovieVideoEntry.COL_MOVIE_ID+ " = ?",
                    new String[]{favMovie.getId()+""},
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                List<MovieVideo> videos = new ArrayList<MovieVideo>();
                while (!cursor.isAfterLast()) {
                    videos.add(ProviderContract.MovieVideoEntry.cursorToMovieVideo(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
                favMovie.setTrailers(videos);
            }

            //now get movie reviews
            cursor = getActivity().getContentResolver().query(
                    ProviderContract.MovieReviewEntry.CONTENT_URI,
                    ProviderContract.MovieReviewEntry.projections,
                    ProviderContract.MovieReviewEntry.COL_MOVIE_ID+ " = ?",
                    new String[]{favMovie.getId()+""},
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                List<MovieReview> reviews = new ArrayList<MovieReview>();
                while (!cursor.isAfterLast()) {
                    reviews.add(ProviderContract.MovieReviewEntry.cursorToMovieReview(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
                favMovie.setReviews(reviews);
            }
        }
        return favMovie;
    }

    private void setShareIntent() {
        Log.d(LOG_TAG, "Setting Share Intent");
        //If we have any trailers to show, then only set intent to first trailer video
        if(mMovie != null && mMovie.getTrailers() != null && mMovie.getTrailers().size() > 0){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");
            String urlStr = new String(HelperUtility.YOU_TUBE_URL)
                    .replaceAll(":1", mMovie.getTrailers().get(0).getKey());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Link to Movie Trailer");
            shareIntent.putExtra(Intent.EXTRA_TEXT, urlStr);
            Log.d(LOG_TAG, "updating Share Intent with text:" + urlStr);
            if(mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareIntent);
            } else {
                Log.e(LOG_TAG, "Share action provider is NULL");
            }
        } else {
            //else clear the previous data
            mShareActionProvider.setShareIntent(null);
        }
    }

    /**
     * Call the provider and insert this record in database, steps need to follow:
     * insert movie, get id of inserted movie, insert movie video and insert movie review
     */
    private void insertMovie() {

        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        //prepare operation to insert Movie
        operations.add(ContentProviderOperation.newInsert(ProviderContract.MovieEntry.CONTENT_URI)
                .withValues(ProviderContract.MovieEntry.loadContentValues(mMovie))
                .build());
        //prepare operation to insert all videos/trailers in the local db
        List<MovieVideo> trailers = mMovie.getTrailers();
        if(trailers != null && trailers.size() > 0) {
            Uri uri = ProviderContract.BASE_CONTENT_URI.buildUpon().
                    appendPath(ProviderContract.MovieVideoEntry.TABLE_MOVIE_VIDEO).
                    build();
            for (int i = 0; i < trailers.size(); i++) {
                MovieVideo trailer = mMovie.getTrailers().get(i);
                operations.add(ContentProviderOperation.newInsert(uri)
                        //get movie id and use it to insert video
                        .withValueBackReference(ProviderContract.MovieVideoEntry.COL_MOVIE_ID, 0)
                        .withValues(ProviderContract.MovieVideoEntry.loadContentValues(trailers.get(i)))
                        .build());

            }
        }

        //prepare operation to insert all reviews in the local db
        List<MovieReview> reviews = mMovie.getReviews();
        if(reviews != null && reviews.size() > 0) {
            Uri uri = ProviderContract.BASE_CONTENT_URI.buildUpon().
                    appendPath(ProviderContract.MovieReviewEntry.TABLE_MOVIE_REVIEW).
                    build();
            for (int i = 0; i < reviews.size(); i++) {
                operations.add(ContentProviderOperation.newInsert(uri)
                        //get movie id and use it to insert review
                        .withValueBackReference(ProviderContract.MovieReviewEntry.COL_MOVIE_ID, 0)
                        .withValues(ProviderContract.MovieReviewEntry.loadContentValues(reviews.get(i)))
                        .build());
            }
        }
        //now execute these operations on content provider
        ContentProviderResult[] results = new ContentProviderResult[0];
        try {
            results = getActivity().getContentResolver().applyBatch(ProviderContract.CONTENT_AUTHORITY, operations);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "RemoteException while inserting:" + e.getMessage());
        } catch (OperationApplicationException e) {
            Log.e(LOG_TAG, "OperationApplicationException while inserting:" + e.getMessage());
        }
        Log.d(LOG_TAG, "Returned result:" + results);

    }



    /*************************************************** Trailers *****************************************************/
    /**
     * Fetches the list of trailers from themoviedbapi
     */
    public class MovieTrailerFetchTask extends AsyncTask<String, Integer, List<MovieVideo>> {

        private final String LOG_TAG = MovieTrailerFetchTask.class.getSimpleName();


        @Override
        protected List<MovieVideo> doInBackground(String... urlStr) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<MovieVideo> videoList = null;

            try {
                URL url = new URL(urlStr[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                Log.d(LOG_TAG, "Sending Query to APIs:" + urlConnection.toString());
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                StringBuffer response = null;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                response = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Log.d(LOG_TAG, "Returned response from APIs:" + response.toString());

                //Now parse the returned JSON String and get the list of videos
                MovieDataParser parser = new MovieDataParser();
                videoList = parser.getVideosFromJSONString(response.toString());
                //publishProgress(videoList == null ? 0 : videoList.size());

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
            return videoList;
        }

        @Override
        protected void onPostExecute(List<MovieVideo> trailers) {
            Log.d(LOG_TAG, "MovieTrailerFetchTask finished, now update UI. Trailers:" + trailers);
            mMovie.setTrailers(trailers);
            if (trailers != null && trailers.size() > 0 && mTrailersLayout != null) {
                Collections.reverse(trailers);
                for(int i=0;i<trailers.size();i++) {
                    final MovieVideo trailer = trailers.get(i);
                    //first make sure the button hasn't already been added to layout
                    View v = mTrailersLayout.findViewById(i);
                    boolean buttonExists = false;
                    if(v != null
                            && v instanceof Button
                            && ((Button)v).getText().toString().equalsIgnoreCase(trailer.getName())) {
                        buttonExists = true;
                    }
                    if(!buttonExists) {
                        Button trailerButton = new Button(mTrailersLayout.getContext());
                        trailerButton.setText(trailer.getName());
                        trailerButton.setId(i);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(10, 10, 10, 10);
                        trailerButton.setLayoutParams(params);
                        trailerButton.setBackgroundColor(getResources().getColor(R.color.color_accent));
                        trailerButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                // Perform action on click
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setType("video/*");
                                String urlStr = new String(HelperUtility.YOU_TUBE_URL)
                                        .replaceAll(":1", trailer.getKey());
                                intent.setData(Uri.parse(urlStr));
                                //startActivity(intent);
                                startActivity(Intent.createChooser(intent, "Select Video App"));
                            }
                        });
                        mTrailersLayout.addView(trailerButton);
                        Log.d(LOG_TAG, "Added button for trailer:" + trailer.getName());
                    }
                }
                //also update share intent with new link for trailers
                setShareIntent();
            }

        }
    }
    /*************************************************** Reviews *****************************************************/

    /**
     * Fetches the list of reviews from themoviedbapi
     */
    public class MovieReviewFetchTask extends AsyncTask<String, Integer, List<MovieReview>> {

        private final String LOG_TAG = MovieReviewFetchTask.class.getSimpleName();

        @Override
        protected List<MovieReview> doInBackground(String... urlStr) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<MovieReview> reviewList = null;

            try {
                URL url = new URL(urlStr[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                Log.d(LOG_TAG, "Sending Query to APIs:" + urlConnection.toString());
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                StringBuffer response = null;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                response = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Log.d(LOG_TAG, "Returned response from APIs:" + response.toString());

                //Now parse the returned JSON String and get the list of videos
                MovieDataParser parser = new MovieDataParser();
                reviewList = parser.getReviewsFromJSONString(response.toString());
                //publishProgress(reviewList == null ? 0 : reviewList.size());

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
            return reviewList;
        }

        @Override
        protected void onPostExecute(List<MovieReview> reviews) {
            Log.d(LOG_TAG, "MovieReviewFetchTask finished, now update UI. Reviews:" + reviews);
            mMovie.setReviews(reviews);
            if (reviews != null && reviews.size() > 0 && mReviewsLayout != null) {
                ListView listView = (ListView) mReviewsLayout.findViewById(R.id.reviews_list);
                listView.setAdapter(new ReviewAdapter(reviews));
            }

        }
    }


    /* ****************** Custom Adapter to display reviews ********************** */
    private class ReviewAdapter extends ArrayAdapter<MovieReview> {

        List<MovieReview> mReviews;

        public ReviewAdapter(List<MovieReview> reviews) {
            //super(getActivity(), 0, reviews); //Replaced 0 with layout id
            super(getActivity(), R.layout.list_item_review, reviews);
            this.mReviews = reviews;
        }

        @Override
        public int getCount() {
            return (mReviews != null) ? mReviews.size() : 0;
        }

        @Override
        public MovieReview getItem(int position) {
            return mReviews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_review, null);
            }
            // Configure the view
            MovieReview review = getItem(position);
            TextView authorTextView = (TextView) convertView.findViewById(R.id.review_author);
            authorTextView.setText(review.getAuthor());

            TextView contentTextView = (TextView) convertView.findViewById(R.id.review_content);
            contentTextView.setText(HelperUtility.getShortenedText(review.getContent()));

            TextView urlTextView = (TextView) convertView.findViewById(R.id.review_url);
            urlTextView.setText(review.getUrl());
            return convertView;
        }

    }

}