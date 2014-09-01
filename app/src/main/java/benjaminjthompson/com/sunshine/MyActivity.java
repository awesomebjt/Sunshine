package benjaminjthompson.com.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends ActionBarActivity implements Callback {
    private static final String LOG_TAG = "Main Activity";
    private boolean mTwoPane = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        //If there's a detail pane, populate it
        if (findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
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

    @Override
    public void onItemSelected(String date) {
        if(mTwoPane) {
            DetailFragment df = new DetailFragment();
            Bundle args = new Bundle();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, date);
            startActivity(intent);
        }
    }
}
