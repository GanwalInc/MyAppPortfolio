package com.ganwal.nanodegree.popularMovie;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * A fragment containing a Movie review view.
 */
public class ReviewFragment extends Fragment {

    public static final String LOG_TAG = ReviewFragment.class.getSimpleName();

    LinearLayout reviewsLayout;
    Movie movie;

    public ReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            movie = intent.getParcelableExtra(MainActivity.EXTRA_MOVIE_ID);
            //String movieID = intent.getStringExtra(MainActivity.EXTRA_MOVIE_ID);
            Bundle arguments = getArguments();
            if (arguments != null) {
                movie = arguments.getParcelable(MainActivity.EXTRA_MOVIE_ID);
            }
            if (movie != null) {
                //now show all the movie attributes on the details page
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getTitle());
                reviewsLayout = (LinearLayout) rootView.findViewById(R.id.reviews_view);

            } else {
                //In two pane layout when no movie is selected, show movie selection text view
                // but don't show the movie ones
                ((TextView) rootView.findViewById(R.id.no_movie_selected)).setVisibility(View.VISIBLE);
                ((TextView) rootView.findViewById(R.id.movie_title)).setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.user_rating_label)).setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.release_date_label)).setVisibility(View.GONE);
                ((LinearLayout) rootView.findViewById(R.id.trailers_view)).setVisibility(View.GONE);
                ((LinearLayout) rootView.findViewById(R.id.reviews_view)).setVisibility(View.GONE);
            }

        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMovieReviews();
    }

    private void fetchMovieReviews() {
        //first make sure we have the api_key defined in the string resource file
        String apiKey = getString(R.string.api_key);
        if (HelperUtility.isStringNullOREmpty(apiKey)) {
            Log.e(LOG_TAG, "***** ERROR - No API author found. Define api_key in string resource file ***");
        } else {
            if(movie != null) {
                String urlStr = new String(HelperUtility.MOVIE_DB_REVIEWS_VIDEO_URL)
                        .replaceAll(":1", movie.getMovieDBId())
                        .replaceAll(":2", getString(R.string.api_key));
                Log.d(LOG_TAG, "Loading trailer for Movie:"+movie.getTitle());
                MovieReviewFetchTask task = new MovieReviewFetchTask();
                task.execute(urlStr);
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
            Log.d(LOG_TAG, "MovieReviewFetchTask finished, now update UI. Trailers:" + reviews);
            if (reviews != null && reviews.size() > 0 && reviewsLayout != null) {
               for(int i=0;i<reviews.size();i++) {
                    final MovieReview review = reviews.get(i);
                    TextView reviewTextView = new TextView(reviewsLayout.getContext());
                    reviewTextView.setText(review.getAuthor()+": "+review.getContent());
                    reviewTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    reviewsLayout.addView(reviewTextView);
                    Log.d(LOG_TAG, "Added text box for review:" + review.getMovieDBId());
                }

            }

        }
    }
}