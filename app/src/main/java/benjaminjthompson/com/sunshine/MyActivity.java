package benjaminjthompson.com.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends ActionBarActivity {
    private static final String LOG_TAG = "Main Activity";

    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, " onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        Log.v(LOG_TAG, " onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.v(LOG_TAG, " onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.v(LOG_TAG, " onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.v(LOG_TAG, " onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, " onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if(id==R.id.action_viewPrefLoc) {
            Intent viewPrefLocIntent = new Intent(Intent.ACTION_VIEW);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String location = sharedPref.getString(
                    getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default)
            );
            Uri geoLoc = Uri.parse("geo:0,0?q="+location);
            viewPrefLocIntent.setData(geoLoc);
            if (viewPrefLocIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(viewPrefLocIntent);
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
