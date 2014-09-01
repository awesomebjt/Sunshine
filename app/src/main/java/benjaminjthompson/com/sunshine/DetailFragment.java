package benjaminjthompson.com.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import benjaminjthompson.com.sunshine.data.WeatherContract;

/**
 * Created by bjt on 8/23/2014.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    private static final String LOG_TAG = DetailFragment.class.toString();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastStr;
    private String mForecastDate;
    private String mLocation;
    View rootView;
    private static final int DETAIL_LOADER = 1;
    private static final String LOCATION_KEY = "location";

    public static DetailFragment newInstance(int index) {
        DetailFragment f = new DetailFragment();

        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(DetailActivity.DATE_KEY) &&
                mLocation != null &&
                !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");

        Intent intent = getActivity().getIntent();
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mForecastDate = intent.getStringExtra(Intent.EXTRA_TEXT);
            getLoaderManager().initLoader(1, null, this);
            //((TextView)rootView.findViewById(R.id.detail_text)).setText(mForecastStr);

        } else {
            Bundle args = getArguments();
            mForecastDate = args.getString("date");
            getLoaderManager().initLoader(1,null,this);
        }
        return rootView;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr+FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader");
        if (args != null) {
            mForecastDate = args.getString("date");
        }
        Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                Utility.getPreferredLocation(getActivity()),
                mForecastDate);
        Log.v(LOG_TAG,weatherUri.toString());
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                weatherUri,
                null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Cursor cursor = (Cursor)data;
        if (cursor.moveToFirst()) {
            int id_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT);
            int id_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);
            int id_hi = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
            int id_lo = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
            int id_humid = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY);
            int id_wind = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED);
            int id_press = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE);

            String date = cursor.getString(id_date);
            String desc = cursor.getString(id_desc);
            String formattedHiTemp = Utility.formatTemperature(getActivity(),
                    cursor.getDouble(id_hi), Utility.isMetric(getActivity()));
            String formattedLoTemp = Utility.formatTemperature(getActivity(),
                    cursor.getDouble(id_lo), Utility.isMetric(getActivity()));
            String day = Utility.getDayName(getActivity(), cursor.getString(id_date));
            String humidity = cursor.getString(id_humid);
            String windspeed = cursor.getString(id_wind);
            String pressure = cursor.getString(id_press);

            ((TextView)rootView.findViewById(R.id.detail_day_textview)).setText(day);
            ((TextView)rootView.findViewById(R.id.detail_date_textview)).setText(Utility.formatDate(date));
            ((TextView)rootView.findViewById(R.id.detail_desc_textview)).setText(desc);
            ((TextView)rootView.findViewById(R.id.detail_hi_textview)).setText(formattedHiTemp);
            ((TextView)rootView.findViewById(R.id.detail_lo_textview)).setText(formattedLoTemp);
            ((TextView)rootView.findViewById(R.id.detail_humidity_textview)).setText(humidity);
            ((TextView)rootView.findViewById(R.id.detail_wind_textview)).setText(windspeed);
            ((TextView)rootView.findViewById(R.id.detail_press_textview)).setText(pressure);
            ((ImageView)rootView.findViewById(R.id.detail_img_textview)).setImageResource(
                    Utility.getArtResourceForWeatherCondition(
                            cursor.getInt(
                                    cursor.getColumnIndex(
                                            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
                                    )
                            )
                    )
            );
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}