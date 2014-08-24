package benjaminjthompson.com.sunshine;

/**
 * Created by bjt on 8/23/2014.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import benjaminjthompson.com.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
        ImageView iconView = (ImageView) view.findViewById(R.id.weather_list_image_view);
        iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        String dateString = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));
        // Find TextView and set formatted date on it
        TextView dateView = (TextView) view.findViewById(R.id.weather_list_date_textview);
        dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
        // Find TextView and set weather forecast on it
        TextView descriptionView = (TextView) view.findViewById(R.id.weather_list_desc_textview);
        descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP));
        // Find TextView and set formatted high temperature on it
        TextView hightempView = (TextView) view.findViewById(R.id.weather_list_hi_textview);
        hightempView.setText(Utility.formatTemperature(high,Utility.isMetric(context)));

        // Read low temperature from cursor
        float low = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP));
        // Find TextView and set formatted low temperature on it
        TextView lowtempView = (TextView) view.findViewById(R.id.weather_list_lo_textview);
        lowtempView.setText(Utility.formatTemperature(low,Utility.isMetric(context)));
    }
}