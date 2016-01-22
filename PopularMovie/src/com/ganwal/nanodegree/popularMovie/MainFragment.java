package com.ganwal.nanodegree.popularMovie;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Picasso;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    MovieImageAdapter movieImageAdapter = null;

    private List<Movie> mMovies;

    OnMovieSelectedListener mMovieSelectedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        movieImageAdapter = new MovieImageAdapter(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_view);
        gridView.setAdapter(movieImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = (Movie) movieImageAdapter.getItem(position);
                mMovieSelectedListener.onMovieSelected(selectedMovie);
            }
        });
        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        //find the screen size of device and set it in helper so we don't have to find it over and over
        //it will be used to download images
        HelperUtility.setImageSize(HelperUtility.findImageSize(this.getActivity()));
        Log.d(LOG_TAG, "Image Size for poster images is :" + HelperUtility.getImageSize());
        //calling fetch movies twice; in onAttach and onStart, for some reasons android emulators
        // pre API level 21, are not showing the list of movies for the first time app is opened
        fetchMovies();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mMovieSelectedListener = (OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMovieSelectedListener");
        }
        fetchMovies();

    }

    private void fetchMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //first make sure we have the api_key defined in the string resource file
        String apiKey = getString(R.string.api_key);
        if(HelperUtility.isStringNullOREmpty(apiKey)) {
            Log.e(LOG_TAG, "***** ERROR - No API key found. Define api_key in string resource file ***");
        } else {
            String sortOrderValue = prefs.getString("pref_sort_order",  HelperUtility.MOVIES_POPULARITY_SORT_ORDER_VALUE);
            String sortByQueryParam = sortOrderValue.equalsIgnoreCase(HelperUtility.MOVIES_RATINGS_SORT_ORDER_VALUE) ?
                    HelperUtility.MOVIES_RATINGS_SORT_ORDER: HelperUtility.MOVIES_POPULARITY_SORT_ORDER;
            String urlStr = new String(HelperUtility.MOVIE_DB_URL)
                    .replaceAll(":1", sortByQueryParam)
                    .replaceAll(":2", getString(R.string.api_key));
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(urlStr);

        }
    }

    public List<Movie> getmMovies() {
        return mMovies;
    }

    public void setmMovies(List<Movie> mMovies) {
        this.mMovies = mMovies;
    }

    /* ****************** Adapter class for showing images in GridView  ********************** */

    public class MovieImageAdapter extends BaseAdapter {

        private Activity mActivity;

        public MovieImageAdapter(Activity mActivity) {
            this.mActivity = mActivity;
        }


        @Override
        public int getCount() {
            return (getmMovies() != null) ? getmMovies().size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return getmMovies().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                convertView = this.mActivity.getLayoutInflater().
                        inflate(R.layout.movie_item_view, null);
            }
            imageView = (ImageView) convertView;
            //use Picasso library to load images
            Picasso.with(mActivity)
                    .load(HelperUtility.buildPosterImageURL(getmMovies().get(position).getThumbnailName()))
                    .into(imageView);
            return imageView;
        }

    }


    /* ****************** AsyncTask to get list of movies ********************** */
    /**
     * Fetches the list of movies from themoviedb.com apis
     */
    public class FetchMoviesTask extends AsyncTask<String, Integer, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        @Override
        protected List<Movie> doInBackground(String...urlStr) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            List<Movie> movieList = null;
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

                //Now parse the returned JSON String and get the list of movies
                MovieDataParser parser = new MovieDataParser();
                movieList = parser.getMoviesFromJSONString(response.toString());
                publishProgress(movieList == null ? 0 : movieList.size());

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
            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            setmMovies(movies);
        }

        @Override
        protected void onProgressUpdate(Integer... resultSize) {
            if(resultSize.length > 0 && movieImageAdapter != null) {
                movieImageAdapter.notifyDataSetChanged();
            }
            super.onProgressUpdate(resultSize);
        }
    }

    /* ****************** Callback interface needs to be implemented by calling activity ********************** */
    public interface OnMovieSelectedListener {
        void onMovieSelected(Movie movie);
    }

}





