package com.snap.thirdear;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snap.thirdear.db.DataBaseHelper;
import com.snap.thirdear.service.BackgroundSpeechRecognizer;
import com.snap.thirdear.service.BluetoothService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton startStopBtn;
    private int startStopBtnStatus = 0;
    private TextView startStopText;
    private ImageView waveImg;

    //SQLite DB
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUI();

        //set up Db
        dataBaseHelper = new DataBaseHelper(this);

        /*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.device1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
            startListening(view);
        }else{
            startStopBtnStatus = 0;
            stopListening(view);
        }
    }

    private void startListening(View view) {
        startStopBtn.setImageResource(R.drawable.stop);
        startStopText.setText(R.string.started_msg);
        waveImg.setImageResource(R.drawable.soundwave_listening);
        startService(new Intent(getBaseContext(), BackgroundSpeechRecognizer.class));
        startService(new Intent(getBaseContext(), BluetoothService.class));
        // startService(new Intent(getBaseContext(), AudioRecorderService.class));
    }


    private void stopListening(View view) {
        startStopText.setText(R.string.stopped_msg);
        startStopBtn.setImageResource(R.drawable.start);
        waveImg.setImageResource(R.drawable.soundwave_quiet);
        stopService(new Intent(getBaseContext(), BackgroundSpeechRecognizer.class));
        stopService(new Intent(getBaseContext(), BluetoothService.class));
        // stopService(new Intent(getBaseContext(), AudioRecorderService.class));
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
