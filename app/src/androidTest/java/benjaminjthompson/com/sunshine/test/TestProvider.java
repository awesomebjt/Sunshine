package benjaminjthompson.com.sunshine.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import benjaminjthompson.com.sunshine.data.WeatherContract.LocationEntry;
import benjaminjthompson.com.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by bjt on 8/4/2014.
 */
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    static public String TEST_CITY_NAME = "North Pole";
    public static String TEST_LOCATION = "99705";
    public static String TEST_DATE = "20141205";

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {
        double testLatitude = 64.772;
        double testLongitude = -147.355;

        ContentValues locationValues = getLocationContentValues(TEST_CITY_NAME, TEST_LOCATION, testLatitude, testLongitude);
        Uri locationInsertUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
        long locationRowId = ContentUris.parseId(locationInsertUri);
        Log.d(LOG_TAG, "New Location Row ID: "+locationRowId);

        ContentValues weatherValues = getWeatherContentValues(locationRowId);
        Uri weatherInsertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
        long weatherRowId = ContentUris.parseId(weatherInsertUri);

        Log.d(LOG_TAG, "New Weather Row ID: "+weatherRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor locationCursor1 = mContext.getContentResolver().query(LocationEntry.CONTENT_URI,
                null, //columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        validateCursor(locationValues, locationCursor1);
        locationCursor1.close();

        Cursor locationCursor2 = mContext.getContentResolver().
                query(LocationEntry.buildLocationUri(locationRowId),
                        null,
                        null,
                        null,
                        null);
        validateCursor(locationValues, locationCursor2);
        locationCursor2.close();

        Cursor weatherCursor1 = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI,
                null, //columns
                null, //cols for where clause
                null, //values for where clause
                null //sort order
        );

        validateCursor(weatherValues, weatherCursor1);
        weatherCursor1.close();

        Cursor weatherCursor2 = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocation(TEST_LOCATION),
                null,
                null,
                null,
                null);
        validateCursor(weatherValues, weatherCursor2);
        weatherCursor2.close();

        Cursor weatherCursor3 = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE),
                null,
                null,
                null,
                null);
        validateCursor(weatherValues, weatherCursor3);
        weatherCursor3.close();

        Cursor weatherCursor4 = mContext.getContentResolver().query(WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                null,
                null,
                null,
                null);
        validateCursor(weatherValues, weatherCursor4);
        weatherCursor4.close();


    }

    public ContentValues getWeatherContentValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, TEST_DATE);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

    public ContentValues getLocationContentValues(String testCityName, String testLocationSetting, double testLatitude, double testLongitude) {
        ContentValues locationValues = new ContentValues();
        locationValues.put(LocationEntry.COLUMN_CITY_NAME, testCityName);
        locationValues.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        locationValues.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        locationValues.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return locationValues;
    }

    static void validateCursor(ContentValues expectedValues, Cursor valueCursor) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    // brings our database to an empty state
    public void deleteAllRecords() {
        int deletedWeather = mContext.getContentResolver().delete(
                WeatherEntry.CONTENT_URI,
                null,
                null
        );

        int deletedLocations = mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testUpdateLocation() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        values.put(LocationEntry.COLUMN_COORD_LAT, 64.772d);
        values.put(LocationEntry.COLUMN_COORD_LONG, -147.355d);

        Uri locationUri = mContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LocationEntry._ID, locationRowId);
        updatedValues.put(LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int count = mContext.getContentResolver().update(
                LocationEntry.CONTENT_URI, updatedValues, LocationEntry._ID + " = ?",
                new String[] { Long.toString(locationRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        if(cursor.moveToFirst()) {
            validateCursor(updatedValues, cursor);
        }
    }

    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }

}
