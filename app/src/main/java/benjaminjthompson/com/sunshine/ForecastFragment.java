package benjaminjthompson.com.sunshine;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;

import benjaminjthompson.com.sunshine.data.WeatherContract;
import benjaminjthompson.com.sunshine.data.WeatherContract.LocationEntry;
import benjaminjthompson.com.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by bjt on 7/23/2014.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ForecastAdapter mForecastAdapter;


    private static final int FORECAST_LOADER = 0;
    private String mLocation;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;

    public ForecastFragment() {
    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateWeather() {
        String location = Utility.getPreferredLocation(getActivity());
        new FetchWeatherTask(getActivity()).execute(location);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(getActivity());
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        updateWeather();
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String forecast;
                String forecast_id;
                String forecast_date;
                ForecastAdapter adapter = (ForecastAdapter)adapterView.getAdapter();
                Cursor cursor = adapter.getCursor();
                if (cursor.moveToFirst()) {
                    cursor.moveToPosition(position);
                    int date_index = cursor.getColumnIndex(WeatherEntry.COLUMN_DATETEXT);
                    int desc_index = cursor.getColumnIndex(WeatherEntry.COLUMN_SHORT_DESC);
                    int hi_index = cursor.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP);
                    int lo_index = cursor.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP);
                    String formattedDate = Utility.formatDate(cursor.getString(date_index));
                    String shortDesc = cursor.getString(desc_index);
                    String formattedHiTemp = Utility.formatTemperature(getActivity(),
                            cursor.getDouble(hi_index), Utility.isMetric(getActivity()));
                    String formattedLoTemp = Utility.formatTemperature(getActivity(),
                            cursor.getDouble(lo_index), Utility.isMetric(getActivity()));

                    forecast = formattedDate + " - " + shortDesc + " - " + formattedHiTemp + "/"+
                            formattedLoTemp;
                    forecast_id = Integer.toString(cursor.getColumnIndex(WeatherEntry._ID));
                    forecast_date = cursor.getString(date_index);
                } else {
                    forecast = "failure";
                    forecast_id = "0";
                    forecast_date = "0";
                }

                try {
                    ((MyActivity)getActivity()).onItemSelected(forecast_date);
                }
                catch (Exception e) {
                    Log.e("ForecastFragment", e.getMessage());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mForecastAdapter.swapCursor(null);
    }
}

