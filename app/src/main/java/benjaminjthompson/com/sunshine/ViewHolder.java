package benjaminjthompson.com.sunshine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Cache of the children views for a forecast list item.
 */
public class ViewHolder {
    public final ImageView iconView;
    public final TextView dateView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;

    public ViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.weather_list_image_view);
        dateView = (TextView) view.findViewById(R.id.weather_list_date_textview);
        descriptionView = (TextView) view.findViewById(R.id.weather_list_desc_textview);
        highTempView = (TextView) view.findViewById(R.id.weather_list_hi_textview);
        lowTempView = (TextView) view.findViewById(R.id.weather_list_lo_textview);
    }
}
