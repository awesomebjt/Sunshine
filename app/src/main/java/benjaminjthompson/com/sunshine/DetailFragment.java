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
    View rootView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mForecastDate = intent.getStringExtra(Intent.EXTRA_TEXT);
            getLoaderManager().initLoader(1, null, this);
            //((TextView)rootView.findViewById(R.id.detail_text)).setText(mForecastStr);

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

            String date = cursor.getString(id_date);
            String desc = cursor.getString(id_desc);
            String formattedHiTemp = Utility.formatTemperature(
                    cursor.getDouble(id_hi), Utility.isMetric(getActivity()));
            String formattedLoTemp = Utility.formatTemperature(
                    cursor.getDouble(id_lo), Utility.isMetric(getActivity()));


            ((TextView)rootView.findViewById(R.id.detail_date_textview)).setText(Utility.formatDate(date));
            ((TextView)rootView.findViewById(R.id.detail_shortdesc_textview)).setText(desc);
            ((TextView)rootView.findViewById(R.id.detail_hi_textview)).setText(formattedHiTemp);
            ((TextView)rootView.findViewById(R.id.detail_lo_textview)).setText(formattedLoTemp);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}