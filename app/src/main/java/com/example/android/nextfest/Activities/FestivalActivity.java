package com.example.android.nextfest.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.nextfest.Fragments.FestivalActivityFragment;
import com.example.android.nextfest.R;
import com.example.android.nextfest.Fragments.VenueActivityFragment;
import com.example.android.nextfest.Adapters.ViewPagerAdapter;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FestivalActivity extends AppCompatActivity {
    private final String LOG_TAG = FestivalActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup realm configuration
        RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);

        setContentView(R.layout.activity_festival);

        //Setup toolbar
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //Setup Tab pagination
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FestivalActivityFragment(), "Events");
        adapter.addFragment(new VenueActivityFragment(), "Venues");
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_festival, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_log_in) {
            startActivity(new Intent(this, LogInActivity.class));
            return true;
        }

        if (id == R.id.action_map){
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openPreferredLocationInMap(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String locationSetting = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",locationSetting).build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
        else{
            Log.d(LOG_TAG, "Couldn't call " + locationSetting + ", no receiving apps installed!");
        }
    }
}
