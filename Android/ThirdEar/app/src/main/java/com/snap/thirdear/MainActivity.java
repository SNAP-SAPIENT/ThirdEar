package com.snap.thirdear;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snap.thirdear.db.DataBaseHelper;
import com.snap.thirdear.service.BackgroundSpeechRecognizer;
import com.snap.thirdear.service.BluetoothService;
import com.snap.thirdear.service.SoundLevelDetector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton startStopBtn;
    private int startStopBtnStatus = 0;
    private TextView startStopText;
    private ImageView waveImg;

    //SQLite DB
    DataBaseHelper dataBaseHelper;
    private SharedPreferences sharedPref;
    private String defaultListenFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setUpUI();
        defaultListenFor = getString(R.string.pref_monitor_default);
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

        startStopBtn = (ImageButton) findViewById(R.id.mainButton);
        startStopBtn.setImageResource(R.drawable.start);
        startStopText = (TextView) findViewById(R.id.statusTextView);
        waveImg = (ImageView) findViewById(R.id.waveImageView);
    }

    public void appControl(View view) {
        if ( 0 == startStopBtnStatus){
            startStopBtnStatus = 1;
            startListening();
        }else{
            startStopBtnStatus = 0;
            stopListening();
        }
    }

    public void startListening() {
        startStopBtn.setImageResource(R.drawable.stop);
        startStopText.setText(R.string.started_msg);
        waveImg.setImageResource(R.drawable.soundwave_listening);
        String listenFor  = sharedPref.getString("pref_monitor", defaultListenFor);
        if(listenFor.equals(getString(R.string.alert_for_keywords)))
            startService(new Intent(getBaseContext(), BackgroundSpeechRecognizer.class));
        else if(listenFor.equals(getString(R.string.alert_for_noiseLevel)))
            startService(new Intent(getBaseContext(), SoundLevelDetector.class));
        startService(new Intent(getBaseContext(), BluetoothService.class));
        // startService(new Intent(getBaseContext(), AudioRecorderService.class));
    }


    public void stopListening() {
        startStopText.setText(R.string.stopped_msg);
        startStopBtn.setImageResource(R.drawable.start);
        waveImg.setImageResource(R.drawable.soundwave_quiet);
        String listenFor  = sharedPref.getString("pref_monitor", defaultListenFor);
        if(listenFor.equals(getString(R.string.alert_for_keywords)))
         stopService(new Intent(getBaseContext(), BackgroundSpeechRecognizer.class));
        else if(listenFor.equals(getString(R.string.alert_for_noiseLevel)))
            stopService(new Intent(getBaseContext(), SoundLevelDetector.class));
        stopService(new Intent(getBaseContext(), BluetoothService.class));
        // stopService(new Intent(getBaseContext(), AudioRecorderService.class));
    }

    public void quickView(View view) {
        Intent intent = new Intent(this, QuickViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            Intent intent = new Intent(this, QuickViewActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_alert_preferences) {
            Intent intent = new Intent(this, AlertPreferencesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_monitors) {

        } else if (id == R.id.nav_actors) {

        } else if (id == R.id.nav_key_words) {
            Intent intent = new Intent(this, KeyWordsActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_general_settings) {
            Intent intent = new Intent(this, GeneralSettingsActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_load_data) {
            loadTestData();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
