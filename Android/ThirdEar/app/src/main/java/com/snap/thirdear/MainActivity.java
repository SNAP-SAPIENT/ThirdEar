package com.snap.thirdear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.snap.thirdear.db.DataBaseHelper;
import com.snap.thirdear.service.BackgroundSpeechRecognizer;
import com.snap.thirdear.service.SoundLevelDetector;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    //SQLite DB
    DataBaseHelper dataBaseHelper;
    private SharedPreferences sharedPref;
    private String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setUpUI();
        homeFragment();
    }

    private void homeFragment() {
        Fragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        //fragmentTransaction.addToBackStack("HomeFragment");
        fragmentTransaction.commit();
    }

    private void quickViewFragment() {
        stopListeningServices();
        Fragment fragment = new QuickViewFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("QuickViewFragment");
        fragmentTransaction.commit();
    }

    private void settingsFragment(){
        //stopListeningServices();
        Intent intent = new Intent(this, GeneralSettingsActivity.class);
        startActivity(intent);
    }

    private void alertPreferences() {
       // stopListeningServices();
        Intent intent = new Intent(this, AlertPreferencesActivity.class);
        startActivity(intent);
    }


    private void stopListeningServices() {
        stopService(new Intent(this, BackgroundSpeechRecognizer.class));
        stopService(new Intent(this, SoundLevelDetector.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(MainActivity.this, "onResume", Toast.LENGTH_SHORT).show();
    }

    private void loadTestData() {
        dataBaseHelper = new DataBaseHelper(this);
        dataBaseHelper.dropAllTables();
        dataBaseHelper.createAllTables();
        dataBaseHelper.loadData();
    }

    private void setUpUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }



    public void quickView(View view) {
        quickViewFragment();
    }

    public void homeView(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        homeFragment();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.d(TAG, "onBackPressed: BackStack count:"+ fragmentManager.getBackStackEntryCount());
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //for this version action bar item  are not needed so it will be hidden hence this
    //function will not be called
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_quick_view) {
            quickViewFragment();
        } else if (id == R.id.nav_alert_preferences) {
            alertPreferences();
        } else if (id == R.id.nav_monitors) {
            Toast.makeText(MainActivity.this, "This menu is not available in  prototype version", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_actors) {
            Toast.makeText(MainActivity.this, "This menu is not available in  prototype version", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_key_words) {
            Intent intent = new Intent(this, KeyWordsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_general_settings) {
            settingsFragment();
        } else if (id == R.id.nav_load_data) {
            loadTestData();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
